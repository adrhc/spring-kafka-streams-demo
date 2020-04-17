package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeyValueIteration<K, V> {
	private final K key;
	private final V value;
	private final int iteration;
}
