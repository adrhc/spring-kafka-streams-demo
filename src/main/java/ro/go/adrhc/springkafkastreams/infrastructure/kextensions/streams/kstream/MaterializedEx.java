package ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.StateStore;

import static com.rainerhahnekamp.sneakythrow.Sneaky.sneak;

@AllArgsConstructor
public class MaterializedEx<K, V, S extends StateStore> {
	private final Materialized<K, V, S> materialized;

	public String getStoreName() {
		return (String) sneak(() -> FieldUtils.getField(Materialized.class, "storeName", true).get(materialized));
	}

	public Serde<K> getKeySerde() {
		return (Serde) sneak(() -> FieldUtils.getField(Materialized.class, "keySerde", true).get(materialized));
	}

	public Serde<V> getValueSerde() {
		return (Serde) sneak(() -> FieldUtils.getField(Materialized.class, "valueSerde", true).get(materialized));
	}
}
