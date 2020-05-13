package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.operators.aggregators.LocalDateBasedKey;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.operators.aggregators.LocalDateBasedKey.keyOf;
import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.operators.aggregators.LocalDateBasedKey.parseWithStringData;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Component
@Slf4j
public class PeriodExceeds extends AbstractPeriodExceeds {
	public PeriodExceeds(TopicsProperties topicsProperties, AppProperties appProperties) {
		super(topicsProperties, appProperties);
	}

	/**
	 * calculating total expenses for a period
	 * using Hopping time window
	 */
	public void accept(KTable<String, ClientProfile> clientProfileTable,
			KGroupedStream<String, Transaction> txGroupedByCli, StreamsBuilderEx streamsBuilder) {
		Duration windowDuration = Duration.of(appProperties.getWindowSize(), appProperties.getWindowUnit());
		txGroupedByCli
/*
				// UnsupportedTemporalTypeException: Unit must not have an estimated duration
				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS))...
*/
				// group by 3 days
				.windowedBy(TimeWindows.of(windowDuration)
						.advanceBy(Duration.ofDays(1)).grace(Duration.ofDays(appProperties.getPeriodGrace())))
				// aggregate amount per clientId-period
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						dailyTotalSpentByClientId(appProperties.getPeriodGrace() + (int) windowDuration.toDays(),
								appProperties.getWindowSize(), appProperties.getWindowUnit()))
				// clientIdPeriod:amount
				.toStream((win, amount) -> keyOf(win))
				.to(topicsProperties.getPeriodTotalSpent(),
						produceInteger("to-" + topicsProperties.getPeriodTotalSpent()));

		// not using through(properties.getPeriodTotalSpent()) because we later need the related store
		KTable<String, Integer> periodTotalSpentTable = periodTotalSpentTable(streamsBuilder);

		periodTotalSpentTable
				.toStream()
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, appProperties.getWindowSize(), DAYS))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(this::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						periodExceededJoiner(appProperties.getWindowSize(), DAYS),
						periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
//				.foreach((clientId, periodExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, periodExceeded));
				.to(topicsProperties.getPeriodExceeds(), producePeriodExceeded());
	}

	private KTable<String, Integer> periodTotalSpentTable(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getPeriodTotalSpent(),
				Consumed.<String, Integer>
						as(topicsProperties.getPeriodTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()),
				Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>
						as(topicsProperties.getPeriodTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()));
	}

	private KeyValue<String, PeriodTotalSpent> clientIdPeriodTotalSpentOf(String clientIdDay, Integer amount) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdDay);
		return winBasedKeyOptional
				.map(it -> {
					String clientId = it.getData();
					log.trace("\n\t{} spent a total of {} {} until {} (including)",
							clientId, amount, appProperties.getCurrency(), format(it.getTime()));
					return KeyValue.pair(clientId, new PeriodTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}

	private void printPeriodTotalExpenses(String clientIdPeriod,
			Integer amount, int windowSize, TemporalUnit unit) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdPeriod);
		winBasedKeyOptional.ifPresent(it -> {
			String clientId = it.getData();
			log.debug("\n\t{} spent a total of {} {} during {} - {}",
					clientId, amount, appProperties.getCurrency(),
					format(it.getTime().minus(windowSize, unit).plusDays(1)),
					format(it.getTime()));
		});
	}
}
