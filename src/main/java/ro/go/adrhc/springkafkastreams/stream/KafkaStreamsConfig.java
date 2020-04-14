package ro.go.adrhc.springkafkastreams.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.util.WindowUtils;

import java.time.Duration;
import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.WindowUtils.keyOf;

/**
 * see https://issues.apache.org/jira/browse/KAFKA-6817
 * see https://stackoverflow.com/questions/49872827/unknownproduceridexception-in-kafka-streams-when-enabling-exactly-once
 * transactional.id.expiration.ms set by default to 7 days
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@Profile("!test")
@Slf4j
public class KafkaStreamsConfig {
	private final TopicsProperties properties;
	private final StreamsHelper helper;

	public KafkaStreamsConfig(TopicsProperties properties, StreamsHelper helper) {
		this.properties = properties;
		this.helper = helper;
	}

	@Bean
	public KStream<String, ?> kstream(StreamsBuilder streamsBuilder) {
		// Hopping time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1));
		// Tumbling time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY));

		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStream<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		transactions
				.peek((clientId, transaction) -> log.debug("\n\t{} spent {} GBP on {}", clientId,
						transaction.getAmount(), format(transaction.getTime())))
//				.transform(new TransformerDebugger<>())
//				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(helper.transactionsGroupedByClientId())
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId())
				.toStream((win, amount) -> keyOf(win)) // clientId-date, amount
				// save clientId-day:amount into a compact stream (aka table)
				.through(properties.getDailyTotalSpent(),
						helper.produceInteger(properties.getDailyTotalSpent()))
				// clientId-day:amount map to clientId:DailyTotalSpent
				.map((k, amount) -> {
					Optional<WindowUtils.WindowBasedKey<String>> winBasedKeyOptional = WindowUtils.parse(k);
					return winBasedKeyOptional
							.map(it -> {
								String clientId = it.getData();
								log.debug("\n\t{} spent a total of {} GBP on {}", clientId, amount, format(it.getTime()));
								return KeyValue.pair(clientId, new DailyTotalSpent(clientId, it.getTime(), amount));
							})
							.orElse(null);
				})
				// clientId:DailyTotalSpent joint clientId:ClientProfile
				.join(clientProfileTable, (dts, cp) -> {
							if (cp.getDailyMaxAmount() < dts.getAmount()) {
								return new DailyExceeded(cp.getDailyMaxAmount(), dts);
							}
							log.trace("\n\tskipping daily total spent under {} GBP\n\t{}\n\t{}", cp.getDailyMaxAmount(), dts, cp);
							return null;
						},
						helper.dailyTotalSpentJoinClientProfile())
				// skip under dailyMaxAmount
				.filter((k, v) -> v != null)
				.to(properties.getDailyExceeds(),
						helper.produceDailyExceeded(properties.getDailyExceeds()));
/*
				.foreach((clientId, de) -> {
					DailyTotalSpent dts = de.getDailyTotalSpent();
					log.debug("\n\tMAIL: {} spent {} GBP on {} (alert set for over {})",
							clientId, dts.getAmount(), format(dts.getTime()), de.getDailyMaxAmount());
				});
*/

		return transactions;
	}

/*
	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
