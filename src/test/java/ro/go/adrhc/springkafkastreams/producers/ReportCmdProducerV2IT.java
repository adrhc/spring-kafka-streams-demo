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
import ro.go.adrhc.springkafkastreams.messages.Command;

import java.util.List;

@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class ReportCmdProducerV2IT {
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
		log.debug("Command topic: {}", properties.getCommand());
		String reportType = System.getProperty("reportType");
		Command report = new Command("report",
				reportType == null ? List.of("daily") : List.of(reportType));
		log.debug("report command:\n\t{}", report);
		avroKTemplate.send(properties.getCommand(), report.getName(), report);
	}
}
