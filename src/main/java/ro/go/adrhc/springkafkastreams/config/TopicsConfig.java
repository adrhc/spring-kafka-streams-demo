package ro.go.adrhc.springkafkastreams.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicsConfig {
	@Autowired
	private TopicsProperties properties;

	@Bean
	public NewTopic personsTopic() {
		return TopicBuilder.name(properties.getPersons()).build();
	}

	@Bean
	public NewTopic personsUpperTopic() {
		return TopicBuilder.name(properties.getPersonsUpper()).build();
	}

	@Bean
	public NewTopic starsTopic() {
		return TopicBuilder.name(properties.getStars()).build();
	}

	@Bean
	public NewTopic starsMultipliedTopic() {
		return TopicBuilder.name(properties.getStarsMultiplied()).build();
	}

	@Bean
	public NewTopic personsStars() {
		return TopicBuilder.name(properties.getPersonsStars()).build();
	}
}
