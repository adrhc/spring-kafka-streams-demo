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
	// used as stream/destination topic name, Consumed.processorName
	private String transactions;
	// used as destination topic name, KTable topic name, Consumed.processorName, Materialized.storeName
	private String dailyTotalSpent;
	// used as destination topic name, Produced.processorName
	private String dailyExceeds;
	// used as destination topic name, KTable topic name, Consumed.processorName, Materialized.storeName
	private String clientProfiles;
	// used as destination topic name, KTable topic name, Consumed.processorName, Materialized.storeName
	private String periodTotalSpent;
	// used as destination topic name, Produced.processorName
	private String periodExceeds;
	// used as stream/destination topic name, Consumed.processorName
	private String commands;
}
