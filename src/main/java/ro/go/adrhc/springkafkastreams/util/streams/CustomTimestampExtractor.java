package ro.go.adrhc.springkafkastreams.util.streams;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.millisecondsOf;

public class CustomTimestampExtractor implements TimestampExtractor {
	private static final Map<Class<?>, Function<Object, Long>> map = Map.of(
			JsonNode.class, value -> ((JsonNode) value).get("timestamp").longValue(),
			Transaction.class, value -> millisecondsOf(((Transaction) value).getTime()));

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
		return Optional.ofNullable(map.get(record.value().getClass()))
				.orElse(it -> record.timestamp()).apply(record.value());
	}
}
