package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.concurrent.ThreadLocalRandom;

public class AbstractTestDTOFactory {
	public static KeyValue<String, Integer> createStar() {
		return KeyValue.pair("adr", ThreadLocalRandom.current().nextInt(0, 100));
	}

	public static KeyValue<String, Person> createPerson() {
		return KeyValue.pair("adr",
				new Person("adr", ThreadLocalRandom.current().nextInt(0, 100)));
	}
}
