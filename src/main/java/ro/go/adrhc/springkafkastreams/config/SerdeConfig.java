package ro.go.adrhc.springkafkastreams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;

import java.util.Map;

import static org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES;
import static org.springframework.kafka.support.serializer.JsonSerializer.TYPE_MAPPINGS;

@Configuration
public class SerdeConfig {
	@Value("${type-mapping:}")
	private String typeMapping;

	@Bean
	public JsonSerde<Person> personSerde() {
		return jsonSerdeImpl();
	}

	@Bean
	public JsonSerde<PersonStars> personStarsSerde() {
		return jsonSerdeImpl();
	}

	@Bean
	public JsonSerde<?> jsonSerde() {
		return jsonSerdeImpl();
	}

	private <T> JsonSerde<T> jsonSerdeImpl() {
		JsonSerde<T> serde = new JsonSerde<>();
		if (typeMapping.isEmpty()) {
			serde.configure(Map.of(TRUSTED_PACKAGES, "*"), false);
			return serde;
		}
		serde.configure(Map.of(TRUSTED_PACKAGES, "*", TYPE_MAPPINGS, typeMapping), false);
		return serde;
	}
}
