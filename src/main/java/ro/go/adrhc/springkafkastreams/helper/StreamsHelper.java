package ro.go.adrhc.springkafkastreams.helper;

import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;

@Component
public class StreamsHelper {
	private final AppProperties appProperties;

	public StreamsHelper(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	public String periodTotalSpentByClientIdStoreName() {
		return "periodTotalSpentByClientId-" + appProperties.getWindowSize() + appProperties.getWindowUnit().toString();
	}
}
