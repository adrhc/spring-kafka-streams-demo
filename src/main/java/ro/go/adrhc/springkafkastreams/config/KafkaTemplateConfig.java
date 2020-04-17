package ro.go.adrhc.springkafkastreams.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.Map;

@Configuration
public class KafkaTemplateConfig {
	private final KafkaProperties properties;
	private final String schemaRegistryUrl;

	public KafkaTemplateConfig(KafkaProperties properties,
			@Value("${schema.registry.url}") String schemaRegistryUrl) {
		this.properties = properties;
		this.schemaRegistryUrl = schemaRegistryUrl;
	}

	@Bean
	public KafkaTemplate<Object, Integer> intKTemplate(
			ObjectProvider<ProducerListener<Object, Integer>> kafkaProducerListener,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		return kafkaTemplateImpl(properties.getClientId() + "-int",
				new IntegerSerializer(), kafkaProducerListener, messageConverter);
	}

	@Bean
	public KafkaTemplate<Object, Object> avroKTemplate(
			ObjectProvider<ProducerListener<Object, Object>> kafkaProducerListener,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		KafkaAvroSerializer valueSerializer = new KafkaAvroSerializer();
		valueSerializer.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);
		return kafkaTemplateImpl(properties.getClientId() + "-avro",
				valueSerializer, kafkaProducerListener, messageConverter);
	}

	private <V> KafkaTemplate<Object, V> kafkaTemplateImpl(
			String clientIdPrefix, Serializer<V> valueSerializer,
			ObjectProvider<ProducerListener<Object, V>> kafkaProducerListener,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		// producer config
		Map<String, Object> config = this.properties.buildProducerProperties();
		config.put(ProducerConfig.CLIENT_ID_CONFIG, clientIdPrefix + "-producer");
		// producer factory
		DefaultKafkaProducerFactory<Object, V> factory = new DefaultKafkaProducerFactory<>(config);
		factory.setValueSerializer(valueSerializer);
		String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
		if (transactionIdPrefix != null) {
			factory.setTransactionIdPrefix(transactionIdPrefix);
		}
		// KafkaTemplate
		KafkaTemplate<Object, V> kafkaTemplate = new KafkaTemplate<>(factory);
		messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
		kafkaTemplate.setProducerListener(kafkaProducerListener.getIfAvailable());
		kafkaTemplate.setDefaultTopic(this.properties.getTemplate().getDefaultTopic());
		return kafkaTemplate;
	}
}
