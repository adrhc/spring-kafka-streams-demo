package ro.go.adrhc.springkafkastreams.enhancer;

public interface KeyValueOffsetMapper<K, V> {
	K apply(K key, V value, int offset);
}
