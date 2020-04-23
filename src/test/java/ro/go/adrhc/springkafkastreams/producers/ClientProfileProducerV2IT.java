package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomClientProfile;

@EnabledIfSystemProperty(named = "enableIT", matches = "true")
@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class ClientProfileProducerV2IT {
	@Autowired
	@Qualifier("avroKTemplate")
	private KafkaTemplate<Object, Object> avroKTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void upsert() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("ClientProfile topic: {}", properties.getClientProfiles());
		ClientProfile clientProfile = randomClientProfile();
		log.debug("clientProfile:\n\t{}", clientProfile);
		avroKTemplate.send(properties.getClientProfiles(), clientProfile.getClientId(), clientProfile);
	}
}
