package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomClientProfile;

@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class ClientProfileV2IT {
	@Autowired
	@Qualifier("jsonTemplate")
	private KafkaTemplate<Object, Object> jsonTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void upsert() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("ClientProfile topic: {}", properties.getClientProfile());
		ClientProfile clientProfile = randomClientProfile(500);
		log.debug("clientProfile:\n\t{}", clientProfile);
		jsonTemplate.send(properties.getClientProfile(), clientProfile.getClientId(), clientProfile);
	}
}
