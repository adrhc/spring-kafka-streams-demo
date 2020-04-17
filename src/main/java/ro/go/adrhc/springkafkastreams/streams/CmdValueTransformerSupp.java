package ro.go.adrhc.springkafkastreams.streams;

import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.Command;

import java.util.List;

public class CmdValueTransformerSupp implements ValueTransformerSupplier<Command, List<String>> {
	private final TopicsProperties properties;

	public CmdValueTransformerSupp(TopicsProperties properties) {this.properties = properties;}

	@Override
	public ValueTransformer<Command, List<String>> get() {
		return new ValueTransformer<>() {
			private KeyValueStore<String, ValueAndTimestamp<Integer>> dailyTotalSpent;
			private KeyValueStore<String, ValueAndTimestamp<Integer>> periodTotalSpent;

			@Override
			public void init(ProcessorContext context) {
				dailyTotalSpent = (KeyValueStore) context.getStateStore(properties.getDailyTotalSpent());
				periodTotalSpent = (KeyValueStore) context.getStateStore(properties.getPeriodTotalSpent());
			}

			@Override
			public List<String> transform(Command value) {
				return null;
			}

			@Override
			public void close() {}
		};
	}
}
