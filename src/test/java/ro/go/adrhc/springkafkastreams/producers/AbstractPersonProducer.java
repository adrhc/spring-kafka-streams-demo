package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class AbstractPersonProducer {
	@Autowired
	private KafkaTemplate<String, Person> template;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void send() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("persons topic: {}", properties.getPersons());
		// see header __TypeId__ with the value: ro.go.adrhc.springkafkastreams.model.Person
		template.send(properties.getPersons(), "adr",
				new Person("adr", ThreadLocalRandom.current().nextInt(0, 100)));
	}
}
