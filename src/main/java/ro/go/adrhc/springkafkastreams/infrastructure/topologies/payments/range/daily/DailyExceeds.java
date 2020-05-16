package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream.operators.aggregation.LocalDateBasedKey;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.AbstractExceeds;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.profiles.messages.ClientProfile;

import java.time.Duration;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream.operators.aggregation.LocalDateBasedKey.keyOf;
import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream.operators.aggregation.LocalDateBasedKey.parseWithStringData;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Component
@Slf4j
public class DailyExceeds extends AbstractExceeds {
	private final TopicsProperties topicsProperties;
	private final AppProperties appProperties;

	public DailyExceeds(TopicsProperties topicsProperties, AppProperties appProperties) {
		this.topicsProperties = topicsProperties;
		this.appProperties = appProperties;
	}

	/**
	 * calculating total expenses per day
	 * using Tumbling time window
	 */
	public void accept(KTable<String, ClientProfile> clientProfileTable,
			KGroupedStream<String, Transaction> txGroupedByCli, StreamsBuilderEx streamsBuilder) {
		txGroupedByCli
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1))
						.grace(Duration.ofDays(appProperties.getDailyGrace())))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						dailyTotalSpentByClientId(appProperties.getDailyGrace() + 1, 1, DAYS))
				// clientIdDay:amount
				.toStream((win, amount) -> keyOf(win))
				// save clientIdDay:amount into a compact stream (aka table)
				.to(topicsProperties.getDailyTotalSpent(),
						produceInteger("to-" + topicsProperties.getDailyTotalSpent()));
//				.through(topicsProperties.getDailyTotalSpent(),
//						produceInteger("to-" + properties.getDailyTotalSpent() + "-stream"))

		// Not using KStream.through(...) because we later need the related KTable store.
		// With this line we pop-up into existence the topicsProperties.getDailyTotalSpent() KTable store.
		KTable<String, Integer> dailyTotalSpentTable = dailyTotalSpentTable(streamsBuilder);

		dailyTotalSpentTable
				.toStream()
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(this::clientIdDailyTotalSpentOf)
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						this::dailyExceededJoiner,
						dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// clientId:DailyExceeded streams
//				.foreach((clientId, dailyExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, dailyExceeded));
				.to(topicsProperties.getDailyExceeds(), produceDailyExceeded());
	}

	private Produced<String, DailyExceeded> produceDailyExceeded() {
		return Produced.as(topicsProperties.getDailyExceeds());
	}

	private KTable<String, Integer> dailyTotalSpentTable(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getDailyTotalSpent(),
				Consumed.<String, Integer>
						as(topicsProperties.getDailyTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()),
				Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>
						as(topicsProperties.getDailyTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()));
	}

	private Joined<String, DailyTotalSpent, ClientProfile> dailyTotalSpentJoinClientProfile() {
		return Joined.as("dailyTotalSpentJoinClientProfile");
	}

	private DailyExceeded dailyExceededJoiner(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			return new DailyExceeded(cp.getDailyMaxAmount(), dts);
		}
		log.trace("\n\tskipping daily total spent under {} {}\n\t{}\n\t{}",
				cp.getDailyMaxAmount(), appProperties.getCurrency(), dts, cp);
		return null;
	}

	private KeyValue<String, DailyTotalSpent> clientIdDailyTotalSpentOf(String clientIdDay, Integer amount) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdDay);
		return winBasedKeyOptional
				.map(it -> {
					String clientId = it.getData();
					log.debug("\n\t{} spent a total of {} {} on {}",
							clientId, amount, appProperties.getCurrency(), format(it.getTime()));
					return KeyValue.pair(clientId, new DailyTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}
}
