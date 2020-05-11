package ro.go.adrhc.springkafkastreams.streams.topologies.exceeds;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey;

import java.time.Duration;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey.keyOf;
import static ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey.parseWithStringData;

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
	public void accept(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilderEnh streamsBuilder) {
		groupedTransactions
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
//				.through(properties.getDailyTotalSpent(),
//						helper.produceInteger("to-" + properties.getDailyTotalSpent() + "-stream"))

		// not using through(properties.getDailyTotalSpent() because we later need the related store
		KTable<String, Integer> dailyTotalSpentTable = dailyTotalSpentTable(streamsBuilder);

		dailyTotalSpentTable
				.toStream()
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(this::clientIdDailyTotalSpentOf)
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						this::joinDailyTotalSpentWithClientProfileOnClientId,
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

	private KTable<String, Integer> dailyTotalSpentTable(StreamsBuilderEnh streamsBuilder) {
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

	private DailyExceeded joinDailyTotalSpentWithClientProfileOnClientId(DailyTotalSpent dts, ClientProfile cp) {
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
