package ro.go.adrhc.springkafkastreams.config;

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
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;
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
	private final JsonSerde<ClientProfile> clientProfileSerde;
	private final JsonSerde<DailyTotalSpent> dailyExpensesSerde;

	public KafkaStreamsConfig(TopicsProperties properties, StreamsHelper helper, JsonSerde<ClientProfile> clientProfileSerde, JsonSerde<DailyTotalSpent> dailyExpensesSerde) {
		this.properties = properties;
		this.helper = helper;
		this.clientProfileSerde = clientProfileSerde;
		this.dailyExpensesSerde = dailyExpensesSerde;
	}

	@Bean
	public KStream<String, ?> kstream(StreamsBuilder streamsBuilder) {
		// Hopping time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1));
		// Tumbling time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY));

		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStream<String, Transaction> transactions = helper.transactions(streamsBuilder);

		transactions
//				.transform(new TransformerDebugger<>())
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(helper.transactionsGroupedByClientId())
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId())
				.toStream((win, amount) -> keyOf(win)) // clientId-date, amount
				.peek((k, amount) -> log.debug("\n\tclientId/day = {}, total spent = {}", k, amount))
				// save clientId-day:amount into table (compact stream)
				.through(properties.getDailyTotalSpent(),
						helper.produceInteger(properties.getDailyTotalSpent()))
				// clientId-day:amount map to clientId:DailyTotalSpent
				.map((k, amount) -> {
					Optional<WindowUtils.WindowBasedKey<String>> winBasedKeyOptional = WindowUtils.parse(k);
					return winBasedKeyOptional
							.map(it -> {
								String key = it.getData();
								return KeyValue.pair(key, new DailyTotalSpent(key, it.getTime(), amount));
							})
							.orElse(null);
				})
				// clientId:DailyTotalSpent joint clientId:ClientProfile
				.join(clientProfileTable, (de, cp) -> {
							if (cp.getDailyMaxAmount() < de.getAmount()) {
								return new DailyExceeded(cp.getDailyMaxAmount(), de);
							}
							log.debug("\n\tskipping daily total spent under {}\n\t{}\n\t{}", cp.getDailyMaxAmount(), de, cp);
							return null;
						},
						helper.dailyTotalSpentJoinClientProfile())
				// skip under dailyMaxAmount
				.filter((k, v) -> v != null)
				.foreach((clientId, ode) -> {
					DailyTotalSpent de = ode.getDailyTotalSpent();
					log.debug("\n\tMAIL: {} spent {} GBP on {} (alert set for over {})",
							de.getClientId(), de.getAmount(), format(de.getTime()), ode.getDailyMaxAmount());
				});

		return transactions;
	}

/*
	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
