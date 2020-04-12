package ro.go.adrhc.springkafkastreams.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

@Slf4j
public class KConsumerUtils {
	public static <K, V> Consumer<K, V> consumerOf(ConsumerFactory<K, V> factory, String... topics) {
		Consumer<K, V> consumer = factory.createConsumer();
		EmbeddedKafkaBroker broker = new EmbeddedKafkaBroker(1, true, topics);
		broker.consumeFromAllEmbeddedTopics(consumer);
		consumer.seekToBeginning(consumer.assignment());
		return consumer;
	}
}
