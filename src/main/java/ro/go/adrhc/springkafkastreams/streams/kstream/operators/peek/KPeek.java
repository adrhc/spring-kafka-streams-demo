package ro.go.adrhc.springkafkastreams.streams.kstream.operators.peek;

import lombok.AllArgsConstructor;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.function.Consumer;

@AllArgsConstructor
public class KPeek<K, V> implements ValueTransformerWithKeySupplier<K, V, V> {
	private final Consumer<KPeekParams<K, V>> consumer;

	public ValueTransformerWithKey<K, V, V> get() {
		return new ValueTransformerWithKey<>() {

			private ProcessorContext context;

			@Override
			public void init(ProcessorContext context) {
				this.context = context;
			}

			@Override
			public V transform(K readOnlyKey, V value) {
				consumer.accept(new KPeekParams<>(readOnlyKey, value, new KPeekContext(context)));
				return value;
			}

			@Override
			public void close() {}
		};
	}
}
