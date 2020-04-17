package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.time.temporal.TemporalUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class TotalExpensesAggregator implements TransformerSupplier<String, Transaction, Iterable<KeyValue<String, Integer>>> {
	private final int period;
	private final String storeName;
	private final TemporalUnit unit;

	public TotalExpensesAggregator(int period, TemporalUnit unit, String storeName) {
		this.period = period;
		this.unit = unit;
		this.storeName = storeName;
	}

	@Override
	public Transformer<String, Transaction, Iterable<KeyValue<String, Integer>>> get() {
		return new Transformer<>() {
			private KeyValueStore<String, Integer> kvStore;

			@Override
			public void init(ProcessorContext context) {
				this.kvStore = (KeyValueStore) context.getStateStore(storeName);
			}

			@Override
			public Iterable<KeyValue<String, Integer>> transform(
					String clientId, Transaction transaction) {
				return IntStream.range(0, period)
						.mapToObj(it -> {
							String key = keyOf(clientId, transaction.getTime().plus(it, unit));
							Integer previousPeriodAmount = this.kvStore.get(key);
							if (previousPeriodAmount == null) {
								this.kvStore.put(key, transaction.getAmount());
								return KeyValue.pair(key, transaction.getAmount());
							}
							int newAmount = transaction.getAmount() + previousPeriodAmount;
							this.kvStore.put(key, newAmount);
							return KeyValue.pair(key, newAmount);
						})
						.collect(Collectors.toList());
			}

			@Override
			public void close() {}
		};
	}
}
