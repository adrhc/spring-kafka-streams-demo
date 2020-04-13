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
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomDailyTotalSpent;
import static ro.go.adrhc.springkafkastreams.util.WindowUtils.keyOf;

@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class TotalSpentDailyProducerV2IT {
	@Autowired
	@Qualifier("intTemplate")
	private KafkaTemplate<Object, Integer> intTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@Test
	void upsert() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("totalSpentDaily topic: {}", properties.getDailyTotalSpent());
		DailyTotalSpent dailyTotalSpent = randomDailyTotalSpent();
		log.debug("totalSpentDaily:\n\t{}", dailyTotalSpent);
		intTemplate.send(properties.getDailyTotalSpent(),
				keyOf(dailyTotalSpent), dailyTotalSpent.getAmount());
	}
}
