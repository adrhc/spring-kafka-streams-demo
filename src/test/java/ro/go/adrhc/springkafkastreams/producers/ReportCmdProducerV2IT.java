package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.KafkaTemplateConfig;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.payments.messages.Command;

import java.util.List;

@EnabledIfSystemProperty(named = "enableIT", matches = "true")
@ActiveProfiles({"v2", "test"})
@Import(KafkaTemplateConfig.class)
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
		log.debug("Command topic: {}", properties.getCommands());
		String reportType = System.getProperty("reportType");
		Command report = new Command("report",
				reportType == null ? List.of("daily") : List.of(reportType.split(",")));
		log.debug("report command:\n\t{}", report);
		avroKTemplate.send(properties.getCommands(), report.getName(), report);
	}
}
