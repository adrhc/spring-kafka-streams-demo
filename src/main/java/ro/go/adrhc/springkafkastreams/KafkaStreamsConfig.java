package ro.go.adrhc.springkafkastreams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;
import ro.go.adrhc.springkafkastreams.helper.SerdeHelper;

import java.util.Map;

import static org.springframework.kafka.support.serializer.JsonSerializer.TYPE_MAPPINGS;

@Configuration
@EnableKafka
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {
	@Value("${topic.persons}")
	private String personsTopic;
	@Value("${topic.stars}")
	private String starsTopic;
	@Value("${topic.persons-upper}")
	private String personsUpperTopic;
	@Value("${spring.kafka.streams.properties.spring.json.type.mapping:NULL}")
	private String typeMapping;
	@Autowired
	private SerdeHelper serde;

	@Bean
	public KStream<String, Person> personsToUpper(StreamsBuilder streamsBuilder) {
		KStream<String, Person> stream = streamsBuilder
				.stream(personsTopic, serde.stringKeyConsumed("personsTopic"));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.map((k, v) -> new KeyValue<>(k.toUpperCase(), v))
				.to(personsUpperTopic, serde.stringKeyProduced("personsUpperTopic"));
		return stream;
	}

/*
	@Bean
	public KStream<String, Integer> starsTopic(StreamsBuilder streamsBuilder) {
		KStream<String, Integer> stream = streamsBuilder.stream(starsTopic,
				Consumed.with(Serdes.String(), Serdes.Integer()));
		stream
				.peek((k, v) -> log.debug("key: {}, value: {}", k, v))
				.transformValues(ValueTransformerWithKeyDebugger<Integer>::new);
		return stream;
	}
*/

	@Bean
	public JsonSerde<?> jsonSerde() {
		JsonSerde serde = new JsonSerde();
		if (typeMapping.equals("NULL")) {
			return serde;
		}
		serde.configure(Map.of(TYPE_MAPPINGS, typeMapping), false);
		return serde;
	}

	@Bean
	public NewTopic personsTopic() {
		return TopicBuilder.name(personsTopic).build();
	}

	@Bean
	public NewTopic personsUpperTopic() {
		return TopicBuilder.name(personsUpperTopic).build();
	}
}
