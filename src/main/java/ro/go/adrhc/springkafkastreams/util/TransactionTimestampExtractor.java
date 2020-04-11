package ro.go.adrhc.springkafkastreams.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class TransactionTimestampExtractor implements TimestampExtractor {
	private static final Map<Class<?>, Function<Object, Long>> map = Map.of(
			JsonNode.class, value -> ((JsonNode) value).get("timestamp").longValue(),
			Transaction.class, value -> ((Transaction) value).ofEpochSecond());

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		return Optional.ofNullable(map.get(record.value().getClass()))
				.orElse(it -> record.timestamp()).apply(record.value());
	}
}