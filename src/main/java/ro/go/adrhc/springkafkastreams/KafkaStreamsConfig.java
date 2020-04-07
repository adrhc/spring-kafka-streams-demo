package ro.go.adrhc.springkafkastreams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.persons.Person;

@Configuration
@EnableKafka
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {
	@Value("${topic.persons}")
	private String personsTopic;
	@Value("${topic.persons-upper}")
	private String personsUpperTopic;

	@Bean
	public KStream<String, Person> kStream(StreamsBuilder streamsBuilder) {
		KStream<String, Person> stream = streamsBuilder.stream(personsTopic);
		stream
				.peek((k, v) -> log.debug("key: {}, value: {}", k, v))
				.transformValues(() -> new ValueTransformer<Person, Person>() {
					private ProcessorContext context;

					@Override
					public void init(ProcessorContext context) {
						this.context = context;
					}

					@Override
					public Person transform(Person value) {
						this.context.headers().forEach(h -> log.debug(h.toString()));
						return value;
					}

					@Override
					public void close() {

					}
				})
				.map((k, v) -> new KeyValue<>(k.toUpperCase(), v))
				.to(personsUpperTopic, Produced.with(Serdes.String(), new JsonSerde<>()));
		return stream;
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
