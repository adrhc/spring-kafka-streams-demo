package ro.go.adrhc.springkafkastreams.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@ConfigurationProperties("app")
@Data
@NoArgsConstructor
public class AppProperties {
	private boolean kafkaEnhanced;
	private String version;
	private int windowSize;
	private ChronoUnit windowUnit;
	private int dailyGrace;
	private int periodGrace;
	private String currency;
}
