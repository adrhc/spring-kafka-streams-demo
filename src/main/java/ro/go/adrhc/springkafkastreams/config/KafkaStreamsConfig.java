package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;

import java.time.Duration;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOfLong;

/**
 * Join persons-stream with stars-table into personsStarsTopic.
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@Profile("!test")
@Slf4j
public class KafkaStreamsConfig {
	private final TopicsProperties properties;
	private final StreamsHelper serde;

	public KafkaStreamsConfig(TopicsProperties properties, StreamsHelper serde) {
		this.properties = properties;
		this.serde = serde;
	}

	@Bean
	public KStream<Windowed<String>, Integer> transactions(StreamsBuilder streamsBuilder) {
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1));
		TimeWindows period = TimeWindows.of(Duration.ofDays(30));
		Materialized<String, Integer, WindowStore<Bytes, byte[]>> aggregation = Materialized
				.<String, Integer, WindowStore<Bytes, byte[]>>
						as(properties.getTransactions() + "-store")
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer());

		KStream<Windowed<String>, Integer> transactions = serde.transactionsStream(streamsBuilder)
//				.transform(new TransformerDebugger<>())
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(serde.transactionsByClientID())
//				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS)).advanceBy(Duration.ofMinutes(1))
//				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS)))
				.windowedBy(period)
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(), aggregation)
				.toStream();

		transactions.foreach((windowedClientId, amount) -> log.debug("\nkey = {}, begin = {}, end: {}, amount = {}",
				windowedClientId.key(), localDateTimeOfLong(windowedClientId.window().start()),
				localDateTimeOfLong(windowedClientId.window().end()), amount));

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
*/
}
