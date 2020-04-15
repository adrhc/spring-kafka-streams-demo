package ro.go.adrhc.springkafkastreams.transformers.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.util.model.ValueHolder;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class PeriodTotalExpensesAggregator implements TransformerSupplier<String, Transaction, Iterable<KeyValue<String, Integer>>> {
	private final int totalPeriod;
	private final String storeName;

	public PeriodTotalExpensesAggregator(int totalPeriod, String storeName) {
		this.totalPeriod = totalPeriod;
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
				ValueHolder<LocalDate> timeHolder = new ValueHolder<>(transaction.getTime());
				return IntStream.range(-1, totalPeriod - 1)
						.mapToObj(it -> {
							LocalDate time = timeHolder.getValue();
							timeHolder.setValue(time.plusDays(1));
							String key = keyOf(clientId, time);
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