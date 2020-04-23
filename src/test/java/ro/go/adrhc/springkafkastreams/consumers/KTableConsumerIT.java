package ro.go.adrhc.springkafkastreams.consumers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static ro.go.adrhc.springkafkastreams.util.KConsumerUtils.consumerOf;

@Disabled
@ActiveProfiles({"v2", "test"})
@SpringBootTest(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
@Import(KTableConsumerIT.Config.class)
@Slf4j
class KTableConsumerIT {
	@Autowired
	private Consumer<String, Integer> consumer;

	@Test
	void list() {
		ConsumerRecords<String, Integer> records = KafkaTestUtils.getRecords(consumer);
		records.forEach(it -> log.debug(it.toString()));
	}

	@TestConfiguration
	static class Config {
		@Autowired
		private KafkaProperties kafkaProperties;

		@Bean
		public Consumer<String, Integer> consumer() {
			return consumerOf(kafkaConsumerFactory(), "adrks1-transactions.v2-changelog");
		}

		@Bean
		public ConsumerFactory<String, Integer> kafkaConsumerFactory() {
			Map<String, Object> config = kafkaProperties.buildConsumerProperties();
			config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					org.apache.kafka.common.serialization.StringDeserializer.class);
			config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					org.apache.kafka.common.serialization.IntegerDeserializer.class);
			return new DefaultKafkaConsumerFactory<>(config);
		}
	}
}
