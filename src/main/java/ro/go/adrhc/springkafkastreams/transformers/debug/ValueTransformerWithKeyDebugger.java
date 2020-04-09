package ro.go.adrhc.springkafkastreams.transformers.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

@Slf4j
public class ValueTransformerWithKeyDebugger<K, V> implements ValueTransformerWithKeySupplier<K, V, V> {
	public ValueTransformerWithKey<K, V, V> get() {
		return new ValueTransformerWithKey<>() {

			private ProcessorContext context;

			@Override
			public void init(ProcessorContext context) {
				this.context = context;
			}

			@Override
			public V transform(K readOnlyKey, V value) {
				log.debug("key: {}, value: {}", readOnlyKey, value);
				this.context.headers().forEach(h -> log.debug(h.toString()));
				return value;
			}

			@Override
			public void close() {}
		};
	}
}
