package ro.go.adrhc.springkafkastreams.streams.processors;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import ro.go.adrhc.springkafkastreams.messages.Command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.parseWithStringData;

public abstract class CmdValueTransformerSupp<T> implements ValueTransformerSupplier<Command, List<T>> {
	private final String storeName;

	public CmdValueTransformerSupp(String storeName) {this.storeName = storeName;}

	protected abstract T newT(String clientId, LocalDate time, Integer amount);

	@Override
	public ValueTransformer<Command, List<T>> get() {
		return new ValueTransformer<>() {
			private KeyValueStore<String, ?> store;

			@Override
			public void init(ProcessorContext context) {
				store = (KeyValueStore) context.getStateStore(storeName);
			}

			@Override
			public List<T> transform(Command value) {
				// https://docs.confluent.io/current/streams/faq.html#why-does-my-kstreams-application-use-so-much-memory
				try (KeyValueIterator<String, ?> iterator = store.all()) {
					List<T> records = new ArrayList<>();
					while (iterator.hasNext()) {
						KeyValue<String, ?> kv = iterator.next();
						parseWithStringData(kv.key).ifPresent(it ->
								records.add(newT(it.getData(), it.getTime(), integerOf(kv.value))));
					}
					return records;
				}
			}

			private Integer integerOf(Object o) {
				if (o instanceof Integer) {
					return (Integer) o;
				}
				if (o instanceof ValueAndTimestamp) {
					return integerOf(((ValueAndTimestamp<?>) o).value());
				}
				throw new UnsupportedOperationException();
			}

			@Override
			public void close() {}
		};
	}
}