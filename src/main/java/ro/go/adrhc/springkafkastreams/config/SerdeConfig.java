package ro.go.adrhc.springkafkastreams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;

import static org.springframework.kafka.support.serializer.JsonSerializer.TYPE_MAPPINGS;

@Configuration
public class SerdeConfig {
	@Value("${spring.kafka.streams.properties.spring.json.type.mapping:NULL}")
	private String typeMapping;

	@Bean
	public JsonSerde<?> jsonSerde() {
		JsonSerde<?> serde = new JsonSerde();
		if (typeMapping.equals("NULL")) {
			return serde;
		}
		serde.configure(Map.of(TYPE_MAPPINGS, typeMapping), false);
		return serde;
	}
}
