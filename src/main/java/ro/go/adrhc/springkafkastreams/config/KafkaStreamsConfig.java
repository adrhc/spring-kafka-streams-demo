package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.helper.SerdeHelper;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;
import ro.go.adrhc.springkafkastreams.transformers.debug.ValueTransformerWithKeyDebugger;

import static ro.go.adrhc.springkafkastreams.util.KStreamsUtils.joinName;

/**
 * Requires clean topics!
 * Don't use starsasstream profile!
 * <p>
 * Join persons-stream with stars-table into personsStarsTopic.
 */
@Configuration
@Profile("!test")
@Slf4j
public class KafkaStreamsConfig {
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private SerdeHelper serde;
	@Autowired
	@Qualifier("personSerde")
	private JsonSerde<Person> personSerde;

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
}
