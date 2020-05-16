package ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream.operators.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOf;

@Slf4j
public class TransformerDebugger<K, V> implements TransformerSupplier<K, V, KeyValue<K, V>> {
	@Override
	public Transformer<K, V, KeyValue<K, V>> get() {
		return new Transformer<>() {
			private ProcessorContext context;

			@Override
			public void init(ProcessorContext context) {
				this.context = context;
			}

			@Override
			public KeyValue<K, V> transform(K key, V value) {
				log.debug("\n\ttopic: {}\n\ttimestamp: {}\n\tkey: {}\n\tvalue: {}",
						this.context.topic(), localDateTimeOf(this.context.timestamp()), key, value);
				this.context.headers().forEach(h -> log.debug(h.toString()));
				return KeyValue.pair(key, value);
			}

			@Override
			public void close() {

			}
		};
	}
}
