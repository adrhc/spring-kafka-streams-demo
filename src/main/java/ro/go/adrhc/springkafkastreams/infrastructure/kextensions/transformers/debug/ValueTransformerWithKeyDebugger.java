package ro.go.adrhc.springkafkastreams.infrastructure.kextensions.transformers.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOf;

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
				log.debug("\n\ttopic: {}\n\ttimestamp: {}\n\tkey: {}\n\tvalue: {}",
						this.context.topic(), localDateTimeOf(this.context.timestamp()), readOnlyKey, value);
				this.context.headers().forEach(h -> log.debug(h.toString()));
				return value;
			}

			@Override
			public void close() {}
		};
	}
}
