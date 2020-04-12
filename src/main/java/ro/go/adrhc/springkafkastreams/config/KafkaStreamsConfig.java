package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.util.WindowUtils;

import java.time.Duration;
import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;
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
	public static final int DELAY = 5;
	private final TopicsProperties properties;
	private final StreamsHelper serde;

	public KafkaStreamsConfig(TopicsProperties properties, StreamsHelper serde) {
		this.properties = properties;
		this.serde = serde;
	}

	@Bean
	public KStream<String, Transaction> transactions(StreamsBuilder streamsBuilder) {
		// Hopping time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1));
		// Tumbling time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30));
		TimeWindows period = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY));
		// Tumbling time windows
//		TimeWindows period = TimeWindows.of(Duration.ofMinutes(1));

		Materialized<String, Integer, WindowStore<Bytes, byte[]>> aggStore =
				Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
						as(properties.getTransactions()) // changelog
						.withValueSerde(Serdes.Integer())
						.withRetention(Duration.ofDays(DELAY + 1));
//						.withRetention(Duration.ofDays(12 * 30));

		KStream<String, Transaction> transactions = serde.transactionsStream(streamsBuilder);

		KTable<Windowed<String>, Integer> aggTable = transactions
//		KStream<Windowed<String>, Integer> transactions = serde.transactionsStream(streamsBuilder)
//				.transform(new TransformerDebugger<>())
//				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(serde.transactionsByClientID())
//				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS)).advanceBy(Duration.ofMinutes(1))
//				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS)))
				.windowedBy(period)
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(), aggStore);

		/*
		 * TimeWindowedKStream: "windowed" implies that the KTable key is a combined key of the original record key and a window ID
		 * CachingWindowStore.putAndMaybeForward: binaryWindowKey = cacheFunction.key(entry.key()).get()
		 * WindowKeySchema.toStoreKeyBinary(key, windowStartTimestamp, 0)
		 * WindowKeySchema.extractWindow(byte[] binaryKey, long windowSize)
		 */
		aggTable.toStream()
//				.peek((windowedClientId, amount) -> log.debug("\n\tkey = {}, [{} to {}), amount = {}",
//						windowedClientId.key(),
//						localDateOf(windowedClientId.window().start()),
//						localDateOf(windowedClientId.window().end()), amount))
				.map((w, amount) -> KeyValue.pair(keyOf(w), amount))
				.through(properties.getDailyExpenses(),
						serde.producedWithInteger(properties.getDailyExpenses()))
				.foreach((k, amount) -> {
					Optional<WindowUtils.WindowBasedKey<String>> wkOptional = WindowUtils.parse(k);
					wkOptional.ifPresent(wk -> log.debug("\n\tMAIL: {} spent {} GBP on {}",
							wk.getData(), amount, format(wkOptional.get().getTime())));
				});

		return transactions;
	}

/*
	@Bean
	public KStream<String, PersonStars> personJoinStars(StreamsBuilder streamsBuilder) {
		KStream<String, Person> persons = serde.personStream(properties.getPersons(), streamsBuilder);
		KTable<String, Integer> stars = serde.integerTable(properties.getStars(), streamsBuilder);

		KStream<String, PersonStars> stream = persons
				.join(stars, PersonStars::new,
						Joined.with(Serdes.String(), personSerde, Serdes.Integer(),
								joinName(properties.getPersonsUpper(), properties.getStars())));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.to(properties.getPersonsStars(), serde.producedWithPersonStars("to-personsStarsTopic"));

		return stream;
	}

	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
