package ro.go.adrhc.springkafkastreams.streams.util;

import org.apache.kafka.streams.StreamsBuilder;
import ro.go.adrhc.springkafkastreams.streams.StreamsBuilderEnh;

public class KafkaEnh {
	public static StreamsBuilderEnh enhance(StreamsBuilder streamsBuilder) {
		return new StreamsBuilderEnh(streamsBuilder);
	}
}
