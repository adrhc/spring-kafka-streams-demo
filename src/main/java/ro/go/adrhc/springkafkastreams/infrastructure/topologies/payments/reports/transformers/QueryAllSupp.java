package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.messages.Command;

import java.util.ArrayList;
import java.util.List;

import static ro.go.adrhc.springkafkastreams.util.StreamsUtils.valueFrom;

public class QueryAllSupp<T> implements ValueTransformerSupplier<Command, List<T>> {
	private final String storeName;

	public QueryAllSupp(String storeName) {
		this.storeName = storeName;
	}

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
						records.add(valueFrom((ValueAndTimestamp) kv.value));
					}
					return records;
				}
			}

			@Override
			public void close() {}
		};
	}
}
