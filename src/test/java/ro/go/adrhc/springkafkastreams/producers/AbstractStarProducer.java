package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.createStar;

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
		KeyValue<String, Integer> pair = createStar();
		// see header __TypeId__ with the value: java.lang.Integer
		template.send(properties.getStars(), pair.key, pair.value);
	}
}
