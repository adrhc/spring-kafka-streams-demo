package ro.go.adrhc.springkafkastreams.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.model.Person;

import java.util.Map;

@Configuration
public class KafkaTemplateConfig {
	private final KafkaProperties properties;
	private final JsonSerde<Person> personSerde;

	public KafkaTemplateConfig(KafkaProperties properties,
			@Qualifier("personSerde") JsonSerde<Person> personSerde) {
		this.properties = properties;
		this.personSerde = personSerde;
	}

	@Bean
	public KafkaTemplate<?, Person> personTemplate(
			ObjectProvider<ProducerListener<Object, Person>> kafkaProducerListener,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		return kafkaTemplateImpl("persons",
				personSerde.serializer(), kafkaProducerListener, messageConverter);
	}

	@Bean
	public KafkaTemplate<?, ?> starTemplate(
			ObjectProvider<ProducerListener<Object, Integer>> kafkaProducerListener,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		return kafkaTemplateImpl("stars",
				new IntegerSerializer(), kafkaProducerListener, messageConverter);
	}

	private <V> KafkaTemplate<?, V> kafkaTemplateImpl(
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
