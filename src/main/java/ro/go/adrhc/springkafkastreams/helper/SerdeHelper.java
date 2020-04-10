package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;

@Component
public class SerdeHelper {
	@Autowired
	@Qualifier("personSerde")
	private JsonSerde<Person> personSerde;
	@Autowired
	@Qualifier("personStarsSerde")
	private JsonSerde<PersonStars> personStarsSerde;

	public Produced<String, PersonStars> producedWithPersonStars(String name) {
		return Produced.with(Serdes.String(), personStarsSerde).withName(name);
	}

	public Produced<String, Person> producedWithPerson(String name) {
		return Produced.with(Serdes.String(), personSerde).withName(name);
	}

	public Produced<String, Integer> producedWithInteger(String name) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(name);
	}

	public Consumed<String, Person> consumedWithPerson(String name) {
		return Consumed.with(Serdes.String(), personSerde).withName(name);
	}

	public Consumed<String, Integer> consumedWithInteger(String name) {
		return Consumed.with(Serdes.String(), Serdes.Integer()).withName(name);
	}

	public KStream<String, Person> personStream(String topic, StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(topic, this.consumedWithPerson("stream-" + topic));
	}

	public KStream<String, Integer> integerStream(String topic, StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(topic, consumedWithInteger("stream-" + topic));
	}
}
