package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.concurrent.ThreadLocalRandom;

public class AbstractTestDTOFactory {
	public static KeyValue<String, Integer> createStar() {
		int key = ThreadLocalRandom.current().nextInt(0, 10);
		return createStar("adr-" + key);
	}

	public static KeyValue<String, Integer> createStar(String key) {
		int value = ThreadLocalRandom.current().nextInt(0, 100);
		return KeyValue.pair(key, value);
	}

	public static KeyValue<String, Person> createPerson() {
		int no = ThreadLocalRandom.current().nextInt(0, 10);
		int age = ThreadLocalRandom.current().nextInt(0, 100);
		String key = "adr-" + no;
		return KeyValue.pair(key, new Person(key, age));
	}
}
