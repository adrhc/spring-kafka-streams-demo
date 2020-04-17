package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.millisecondsOf;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class PeriodAggregator<K, V, R> implements TransformerSupplier<K, V, Iterable<KeyValue<Windowed<K>, R>>> {
	protected final int windowSize;
	protected final TemporalUnit unit;
	private final String storeName;
	private final Initializer<R> initializer;
	private final Aggregator<K, V, R> aggregator;

	public PeriodAggregator(int windowSize, TemporalUnit unit, Initializer<R> initializer,
			Aggregator<K, V, R> aggregator, String storeName) {
		this.windowSize = windowSize;
		this.unit = unit;
		this.storeName = storeName;
		this.initializer = initializer;
		this.aggregator = aggregator;
	}

	@Override
	public Transformer<K, V, Iterable<KeyValue<Windowed<K>, R>>> get() {
		return new Transformer<>() {
			private ProcessorContext context;
			private KeyValueStore<String, R> kvStore;

			@Override
			public void init(ProcessorContext context) {
				this.context = context;
				this.kvStore = (KeyValueStore) context.getStateStore(storeName);
			}

			@Override
			public Iterable<KeyValue<Windowed<K>, R>> transform(K key, V value) {
				LocalDate recTime = localDateOf(context.timestamp());
				Period diff = Period.between(recTime, recTime.plus(windowSize, unit));
				List<KeyValue<Windowed<K>, R>> records = new ArrayList<>();
				while (!diff.isZero()) {
					String windowKey = keyOf(key, recTime);
					R existingAggregate = this.kvStore.get(windowKey);
					if (existingAggregate == null) {
						existingAggregate = initializer.apply();
					}
					R newAggregate = aggregator.apply(key, value, existingAggregate);
					this.kvStore.put(windowKey, newAggregate);
					LocalDate start = recTime;
					LocalDate end = recTime.plusDays(1);
					Window window = new TimeWindow(millisecondsOf(start), millisecondsOf(end));
					records.add(KeyValue.pair(new Windowed<>(key, window), newAggregate));
					diff = diff.minusDays(1);
					recTime = end;
				}
				return records;
			}

			@Override
			public void close() {}
		};
	}
}
