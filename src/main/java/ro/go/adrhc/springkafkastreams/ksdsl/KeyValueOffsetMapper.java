package ro.go.adrhc.springkafkastreams.ksdsl;

public interface KeyValueOffsetMapper<K, V> {
	K apply(K key, V value, int offset);
}
