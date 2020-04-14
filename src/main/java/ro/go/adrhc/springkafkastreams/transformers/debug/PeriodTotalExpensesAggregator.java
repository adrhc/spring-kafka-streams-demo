package ro.go.adrhc.springkafkastreams.transformers.debug;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.util.model.ValueHolder;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class PeriodTotalExpensesAggregator implements TransformerSupplier<String, DailyTotalSpent, Iterable<KeyValue<String, Integer>>> {
	private final int totalPeriod;
	private final TopicsProperties properties;

	public PeriodTotalExpensesAggregator(int totalPeriod, TopicsProperties properties) {
		this.totalPeriod = totalPeriod;
		this.properties = properties;
	}

	@Override
	public Transformer<String, DailyTotalSpent, Iterable<KeyValue<String, Integer>>> get() {
		return new Transformer<>() {
			private KeyValueStore<String, ValueAndTimestamp<Integer>> kvStore;

			@Override
			public void init(ProcessorContext context) {
				this.kvStore = (KeyValueStore) context.getStateStore(properties.getPeriodTotalExpenses());
			}

			@Override
			public Iterable<KeyValue<String, Integer>> transform(
					String clientId, DailyTotalSpent dailyTotalSpent) {
				ValueHolder<LocalDate> timeHolder = new ValueHolder<>(dailyTotalSpent.getTime());
				return IntStream.range(-1, totalPeriod - 1)
						.mapToObj(it -> {
							LocalDate time = timeHolder.getValue();
							timeHolder.setValue(time.plusDays(1));
							String key = keyOf(clientId, time);
							ValueAndTimestamp<Integer> value = this.kvStore.get(key);
							if (value == null) {
								return KeyValue.pair(key, dailyTotalSpent.getAmount());
							}
							return KeyValue.pair(key, dailyTotalSpent.getAmount() + value.value());
						})
						.collect(Collectors.toList());
			}

			@Override
			public void close() {}
		};
	}
}
