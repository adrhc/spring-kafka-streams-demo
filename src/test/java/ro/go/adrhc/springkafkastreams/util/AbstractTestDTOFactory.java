package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.temporal.ChronoUnit.SECONDS;

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

	public static Transaction randomTransaction() {
		LocalDateTime ldt = LocalDate.of(randomInt(2019, 2020),
				randomInt(1, 12), 1)
				.atTime(LocalTime.now()).truncatedTo(SECONDS)
				.plusDays(randomInt(0, 30));
		return new Transaction(ldt,
				"merchant-" + randomInt(0, 10),
				"client-" + randomInt(0, 5),
				randomInt(1, 100));
	}

	private static int randomInt(int origin, int includingBound) {
		return ThreadLocalRandom.current().nextInt(origin, includingBound + 1);
	}
}
