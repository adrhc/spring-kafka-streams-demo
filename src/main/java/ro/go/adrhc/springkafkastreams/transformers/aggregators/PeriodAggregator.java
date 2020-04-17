package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import ro.go.adrhc.springkafkastreams.enhancer.KeyValueOffsetMapper;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class PeriodAggregator<K, V, R> implements TransformerSupplier<K, V, Iterable<KeyValue<K, R>>> {
	protected final int period;
	private final String storeName;
	private final KeyValueOffsetMapper<K, V> keyMapper;
	private final Initializer<R> initializer;
	private final Aggregator<K, V, R> aggregator;

	public PeriodAggregator(int period, KeyValueOffsetMapper<K, V> keySelector,
			Initializer<R> initializer, Aggregator<K, V, R> aggregator, String storeName) {
		this.period = period;
		this.storeName = storeName;
		this.keyMapper = keySelector;
		this.initializer = initializer;
		this.aggregator = aggregator;
	}

	@Override
	public Transformer<K, V, Iterable<KeyValue<K, R>>> get() {
		return new Transformer<>() {
			private KeyValueStore<K, R> kvStore;

			@Override
			public void init(ProcessorContext context) {
				this.kvStore = (KeyValueStore) context.getStateStore(storeName);
			}

			@Override
			public Iterable<KeyValue<K, R>> transform(K key, V value) {
				return IntStream.range(0, period)
						.mapToObj(it -> {
							K newKey = keyMapper.apply(key, value, it);
							R existingAggregate = this.kvStore.get(newKey);
							if (existingAggregate == null) {
								existingAggregate = initializer.apply();
							}
							R newAggregate = aggregator.apply(key, value, existingAggregate);
							this.kvStore.put(newKey, newAggregate);
							return KeyValue.pair(newKey, newAggregate);
						})
						.collect(Collectors.toList());
			}

			@Override
			public void close() {}
		};
	}
}
