package ro.go.adrhc.springkafkastreams.ksdsl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeyValueOffset<K, V> {
	private final K key;
	private final V value;
	private final int periodStartOffset;
}
