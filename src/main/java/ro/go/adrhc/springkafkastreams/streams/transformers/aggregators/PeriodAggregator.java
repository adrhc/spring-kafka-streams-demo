package ro.go.adrhc.springkafkastreams.streams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.millisecondsOf;
import static ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey.keyOf;

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
				LocalDate windowEnd = localDateOf(context.timestamp());
				// lastWindowEnd is considered here as inclusive
				LocalDate lastWindowEnd = windowEnd.plus(windowSize, unit).minusDays(1);
				List<KeyValue<Windowed<K>, R>> records = new ArrayList<>();
				while (!windowEnd.isAfter(lastWindowEnd)) {
					String windowKey = keyOf(key, windowEnd);
					R existingAggregate = this.kvStore.get(windowKey);
					if (existingAggregate == null) {
						existingAggregate = initializer.apply();
					}
					R newAggregate = aggregator.apply(key, value, existingAggregate);
					this.kvStore.put(windowKey, newAggregate);
					LocalDate windowStart = windowEnd.minus(windowSize, unit).plusDays(1);
					// window end is exclusive
					Window window = new TimeWindow(millisecondsOf(windowStart), millisecondsOf(windowEnd.plusDays(1)));
					records.add(KeyValue.pair(new Windowed<>(key, window), newAggregate));
					windowEnd = windowEnd.plusDays(1);
				}
				return records;
			}

			@Override
			public void close() {}
		};
	}
}
