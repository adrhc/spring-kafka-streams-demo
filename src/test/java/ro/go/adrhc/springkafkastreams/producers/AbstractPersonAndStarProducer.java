package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.Person;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.createPerson;
import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.createStar;

@Slf4j
class AbstractPersonAndStarProducer {
	@Autowired
	@Qualifier("starTemplate")
	private KafkaTemplate<String, Integer> starTemplate;
	@Autowired
	@Qualifier("personTemplate")
	private KafkaTemplate<String, Person> personTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void send() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("persons topic: {}", properties.getPersons());
		log.debug("stars topic: {}", properties.getStars());
		KeyValue<String, Person> personPair = createPerson();
		log.debug("personPair: {}", personPair);
		KeyValue<String, Integer> starPair = createStar();
		log.debug("starPair: {}", starPair);
		personTemplate.send(properties.getPersons(), personPair.key, personPair.value);
		starTemplate.send(properties.getStars(), starPair.key, starPair.value);
	}
}
