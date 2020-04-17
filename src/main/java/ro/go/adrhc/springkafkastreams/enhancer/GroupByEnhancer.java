package ro.go.adrhc.springkafkastreams.enhancer;

import lombok.AllArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import ro.go.adrhc.springkafkastreams.transformers.aggregators.PeriodAggregator;

@AllArgsConstructor
public class GroupByEnhancer<P, K, V> {
	private final KeyValueOffsetMapper<K, V> keySelector;
	private final KStream<K, V> kStream;
	private final StreamsBuilder streamsBuilder;

	/**
	 * todo: move this to AggregateBuilder.aggregate(...)
	 */
	public <R, S extends StateStore> KStream<K, R> aggregate(Initializer<R> initializer,
			Aggregator<K, V, R> aggregator, int period, Materialized<K, R, S> materialized) {
		MaterializedEx<K, R, S> materializedEx = new MaterializedEx<>(materialized);
		String storeName = materializedEx.getStoreName();
		StoreBuilder<KeyValueStore<K, R>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore(materializedEx.getStoreName()),
						materializedEx.getKeySerde(), materializedEx.getValueSerde());
		// todo: check for same storeName added twice
		streamsBuilder.addStateStore(periodTotalSpentStore);

		return kStream.flatTransform(
				new PeriodAggregator<>(period, keySelector, initializer, aggregator, storeName),
				storeName);
	}
}
