package ro.go.adrhc.springkafkastreams.payments.exceeds;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class AbstractExceeds {
	protected Produced<String, Integer> produceInteger(String processorName) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	protected Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(
			int retentionDays, int windowSize, ChronoUnit windowUnit) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentByClientId-" + windowSize + windowUnit.toString())
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(retentionDays));
	}
}
