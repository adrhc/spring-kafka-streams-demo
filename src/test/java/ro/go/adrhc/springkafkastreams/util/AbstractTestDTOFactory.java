package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.concurrent.ThreadLocalRandom;

public class AbstractTestDTOFactory {
	public static KeyValue<String, Integer> createStar() {
		int no = ThreadLocalRandom.current().nextInt(0, 100);
		return KeyValue.pair("adr-" + no, no);
	}

	public static KeyValue<String, Person> createPerson() {
		int age = ThreadLocalRandom.current().nextInt(0, 100);
		String key = "adr-" + age;
		return KeyValue.pair(key, new Person(key, age));
	}
}
