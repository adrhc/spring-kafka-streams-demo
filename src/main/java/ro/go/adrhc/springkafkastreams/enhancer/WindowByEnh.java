package ro.go.adrhc.springkafkastreams.enhancer;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import ro.go.adrhc.springkafkastreams.transformers.aggregators.PeriodAggregator;

import java.time.temporal.TemporalUnit;

@AllArgsConstructor
public class WindowByEnh<K, V> {
	private final int windowSize;
	private final TemporalUnit unit;
	private final KStream<K, V> kStream;
	private final StreamsBuilder streamsBuilder;

	/**
	 * todo: move this to AggregateBuilder.aggregate(...)
	 */
	public <R, S extends StateStore> KStream<Windowed<K>, R> aggregate(Initializer<R> initializer,
			Aggregator<K, V, R> aggregator, Materialized<K, R, S> materialized) {
		MaterializedEx<K, R, S> materializedEx = new MaterializedEx<>(materialized);
		String storeName = materializedEx.getStoreName();
		StoreBuilder<KeyValueStore<String, R>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore(materializedEx.getStoreName()),
						Serdes.String(), materializedEx.getValueSerde());
		// todo: check for same storeName added twice
		streamsBuilder.addStateStore(periodTotalSpentStore);

		return kStream.flatTransform(
				new PeriodAggregator<>(windowSize, unit, initializer, aggregator, storeName),
				storeName);
	}
}
