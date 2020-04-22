package ro.go.adrhc.springkafkastreams.enhancer.operators;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KTapParams<K, V> {
	public final K key;
	public final V value;
	public final KTapContext context;
}
