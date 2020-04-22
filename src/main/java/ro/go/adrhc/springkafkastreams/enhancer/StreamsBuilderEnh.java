package ro.go.adrhc.springkafkastreams.enhancer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

@RequiredArgsConstructor
public class StreamsBuilderEnh {
	private final StreamsBuilder streamsBuilder;

	public <K, V> KStreamEnh<K, V> stream(String topic, Consumed<K, V> consumed) {
		return new KStreamEnh<>(streamsBuilder.stream(topic, consumed), streamsBuilder);
	}

	public <K, V> KTable<K, V> table(String topic, Consumed<K, V> consumed, Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized) {
		return streamsBuilder.table(topic, consumed, materialized);
	}
}
