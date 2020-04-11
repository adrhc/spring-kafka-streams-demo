package ro.go.adrhc.springkafkastreams.util;

import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static ro.go.adrhc.springkafkastreams.config.KafkaStreamsConfig.DELAY;

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
		Instant randomInstant = Instant.now().minus(randomInt(1, DELAY), DAYS);
		LocalDateTime ldt = LocalDateTime.ofInstant(randomInstant, ZoneOffset.UTC).truncatedTo(SECONDS);
		return new Transaction(ldt,
				"merchant-" + randomInt(1, 10),
				"client-" + randomInt(1, 1),
				randomInt(1, 100));
	}

	private static int randomInt(int origin, int includingBound) {
		return RandomUtils.nextInt(origin, includingBound + 1);
	}
}
