package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.helper.SerdeHelper;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;

import java.time.Duration;

import static ro.go.adrhc.springkafkastreams.util.KStreamsUtils.joinName;

/**
 * Requires clean topics!
 * Should run with starsasstream profile! aka topic.starsAsStream=true
 * Generate data using PersonAndStarProducerV2IT with starsasstream profile!
 */
@Profile({"!test"})
@Slf4j
public class KafkaStreamsConfig1 {
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private SerdeHelper serde;
	@Autowired
	@Qualifier("personSerde")
	private JsonSerde<Person> personSerde;

	@Bean
	public KStream<String, PersonStars> personJoinStars(StreamsBuilder streamsBuilder) {
		KStream<String, Person> personsUpper = serde.personStream(properties.getPersons(), streamsBuilder);
		personsUpper
				.map((k, v) -> {
					v.setName(v.getName().toUpperCase());
					return new KeyValue<>(k, v);
				})
				.to(properties.getPersonsUpper(), serde.producedWithPerson("to-personsUpperTopic"));

		KStream<String, Integer> starsMultiplied = serde.integerStream(properties.getStars(), streamsBuilder);
		starsMultiplied
				.mapValues(v -> v * 2)
				.to(properties.getStarsMultiplied(), serde.producedWithInteger("to-starsMultipliedTopic"));

		KStream<String, PersonStars> stream = personsUpper
				.join(starsMultiplied, PersonStars::new, JoinWindows.of(Duration.ofSeconds(10)),
						Joined.with(Serdes.String(), personSerde, Serdes.Integer(),
								joinName(properties.getPersonsUpper(), properties.getStarsMultiplied())));

		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.to(properties.getPersonsStars(), serde.producedWithPersonStars("to-personsStarsTopic"));

		return stream;
	}

/*
	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/

/*
	@Bean
	public KStream<String, Integer> starsToMultipliedDebug(StreamsBuilder streamsBuilder) {
		KStream<String, Integer> stream = serde.integerStream(properties.getStars(), streamsBuilder);
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.mapValues(v -> v * 2)
				.foreach((k, v) -> log.debug("key: {}, value: {}", k, v));
		return stream;
	}
*/
}
