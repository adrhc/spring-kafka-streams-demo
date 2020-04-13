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
	private String persons;
	private String personsUpper;
	private String stars;
	private String starsMultiplied;
	private String personsStars;
	private String transactions;
	private String dailyTotalSpent;
	private String dailyExceeded;
	private String clientProfile;
	private boolean starsAsStream;
}
