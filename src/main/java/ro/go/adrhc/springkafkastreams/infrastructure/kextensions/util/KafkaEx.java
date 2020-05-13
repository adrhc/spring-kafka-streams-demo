package ro.go.adrhc.springkafkastreams.infrastructure.kextensions.util;

import org.apache.kafka.streams.StreamsBuilder;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.StreamsBuilderEx;

public class KafkaEx {
	public static StreamsBuilderEx enhance(StreamsBuilder streamsBuilder) {
		return new StreamsBuilderEx(streamsBuilder);
	}
}
