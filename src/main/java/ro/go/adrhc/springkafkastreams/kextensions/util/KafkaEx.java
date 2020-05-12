package ro.go.adrhc.springkafkastreams.kextensions.util;

import org.apache.kafka.streams.StreamsBuilder;
import ro.go.adrhc.springkafkastreams.kextensions.StreamsBuilderEx;

public class KafkaEx {
	public static StreamsBuilderEx enhance(StreamsBuilder streamsBuilder) {
		return new StreamsBuilderEx(streamsBuilder);
	}
}
