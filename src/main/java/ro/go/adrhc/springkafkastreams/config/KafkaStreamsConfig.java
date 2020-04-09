package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.helper.SerdeHelper;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;

@Configuration(proxyBeanMethods = false)
@EnableKafka
@EnableKafkaStreams
@EnableConfigurationProperties
@Slf4j
public class KafkaStreamsConfig {
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private SerdeHelper serde;

	@Bean
	public KStream<String, Person> personsToUpper(StreamsBuilder streamsBuilder) {
		KStream<String, Person> stream = streamsBuilder
				.stream(properties.getPersons(), serde.stringKeyConsumed("personsTopic"));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.map((k, v) -> new KeyValue<>(k.toUpperCase(), v))
				.to(properties.getPersonsUpper(), serde.stringKeyProduced("personsUpperTopic"));
		return stream;
	}

	@Bean
	public KStream<String, Integer> starsToMultiplied(StreamsBuilder streamsBuilder) {
		KStream<String, Integer> stream = streamsBuilder.stream(properties.getStars(),
				serde.stringKeyConsumed("starsTopic"));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.mapValues(v -> v * 2)
				.to(properties.getStarsMultiplied(), serde.stringKeyProduced("starsMultipliedTopic"));
		return stream;
	}

/*
	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
