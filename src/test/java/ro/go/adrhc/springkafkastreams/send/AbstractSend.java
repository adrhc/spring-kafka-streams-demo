package ro.go.adrhc.springkafkastreams.send;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class AbstractSend {
	@Autowired
	private KafkaTemplate<String, Person> template;
	@Value("${topic.persons}")
	private String personsTopic;
	@Autowired
	private Environment env;

	@Test
	void send() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("personsTopic: {}", personsTopic);
		// see header __TypeId__ with the value: ro.go.adrhc.springkafkastreams.model.Person
		template.send(personsTopic, "adr", new Person("adr", ThreadLocalRandom.current().nextInt()));
	}
}
