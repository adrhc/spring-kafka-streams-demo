package ro.go.adrhc.springkafkastreams.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("topic")
@Data
@NoArgsConstructor
public class TopicsProperties {
	private String transactions;
	private String dailyTotalSpent;
	private String dailyExceeds;
	private String clientProfiles;
	private String periodTotalSpent;
	private String periodExceeds;
	private String command;
}
