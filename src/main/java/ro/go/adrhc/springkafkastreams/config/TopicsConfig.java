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
		TopicBuilder builder = TopicBuilder.name(properties.getStars());
		if (!properties.isStarsAsStream()) {
			builder.compact();
		}
		return builder.build();
	}

	@Bean
	public NewTopic starsMultipliedTopic() {
		TopicBuilder builder = TopicBuilder.name(properties.getStarsMultiplied());
		if (!properties.isStarsAsStream()) {
			builder.compact();
		}
		return builder.build();
	}

	@Bean
	public NewTopic personsStars() {
		TopicBuilder builder = TopicBuilder.name(properties.getPersonsStars());
		if (!properties.isStarsAsStream()) {
			builder.compact();
		}
		return builder.build();
	}
}
