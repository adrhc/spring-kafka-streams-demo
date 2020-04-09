package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class AbstractStarProducer {
	@Autowired
	private KafkaTemplate<String, Integer> template;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void send() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("stars topic: {}", properties.getStars());
		// see header __TypeId__ with the value: ro.go.adrhc.springkafkastreams.model.Person
		template.send(properties.getStars(), "adr", ThreadLocalRandom.current().nextInt(0, 100));
	}
}
