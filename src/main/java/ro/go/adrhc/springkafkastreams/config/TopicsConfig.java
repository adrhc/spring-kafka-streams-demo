package ro.go.adrhc.springkafkastreams.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class TopicsConfig {
	private final TopicsProperties properties;

	public TopicsConfig(TopicsProperties properties) {this.properties = properties;}

	@Bean
	public NewTopic transactionsTopic() {
		return TopicBuilder.name(properties.getTransactions()).build();
	}

	@Bean
	public NewTopic dailyTotalSpentTopic() {
		return TopicBuilder.name(properties.getDailyTotalSpent()).compact().build();
	}

	@Bean
	public NewTopic clientProfileTopic() {
		return TopicBuilder.name(properties.getClientProfiles()).compact().build();
	}

	@Bean
	public NewTopic dailyExceededTopic() {
		return TopicBuilder.name(properties.getDailyExceeds()).compact().build();
	}

	@Bean
	public NewTopic periodTotalSpentTopic() {
		return TopicBuilder.name(properties.getPeriodTotalSpent()).compact().build();
	}

	@Bean
	public NewTopic periodExceededTopic() {
		return TopicBuilder.name(properties.getPeriodExceeds()).compact().build();
	}

	@Bean
	public NewTopic commandTopic() {
		return TopicBuilder.name(properties.getCommands()).build();
	}
}
