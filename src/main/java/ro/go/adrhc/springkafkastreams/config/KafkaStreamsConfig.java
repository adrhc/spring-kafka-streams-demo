package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.helper.SerdeHelper;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;

import java.time.Duration;

import static ro.go.adrhc.springkafkastreams.util.KStreamsUtils.joinName;

@Configuration
@EnableKafka
@EnableKafkaStreams
@EnableConfigurationProperties
@Slf4j
public class KafkaStreamsConfig {
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private SerdeHelper serde;
	@Autowired
	private JsonSerde<?> jsonSerde;

	@Bean
	public KStream<String, Person> personsToUpper(StreamsBuilder streamsBuilder) {
		KStream<String, Person> stream = serde.kStreamOf(properties.getPersons(), streamsBuilder);
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.map((k, v) -> {
					v.setName(v.getName().toUpperCase());
					return new KeyValue<>(k, v);
				})
				.to(properties.getPersonsUpper(), serde.stringKeyProduced("to-personsUpperTopic"));
		return stream;
	}

	@Bean
	public KStream<String, Integer> starsToMultiplied(StreamsBuilder streamsBuilder) {
		KStream<String, Integer> stream = serde.kStreamOf(properties.getStars(), streamsBuilder);
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.mapValues(v -> v * 2)
				.to(properties.getStarsMultiplied(), serde.stringKeyProduced("to-starsMultipliedTopic"));
		return stream;
	}

	@Bean
	public KStream<String, PersonStars> personJoinStars(StreamsBuilder streamsBuilder) {
		KStream<String, Person> persons = serde.kStreamOf(properties.getPersonsUpper(), streamsBuilder);
		KStream<String, Integer> stars = serde.kStreamOf(properties.getStarsMultiplied(), streamsBuilder);
		KStream<String, PersonStars> stream = persons.join(stars, PersonStars::new,
				JoinWindows.of(Duration.ofSeconds(10)),
				Joined.with(Serdes.String(), (Serde<Person>) jsonSerde, Serdes.Integer(),
						joinName(properties.getPersonsUpper(), properties.getStarsMultiplied())));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.to(properties.getPersonsStars(), serde.stringKeyProduced("to-personsStarsTopic"));
		return stream;
	}

/*
	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
