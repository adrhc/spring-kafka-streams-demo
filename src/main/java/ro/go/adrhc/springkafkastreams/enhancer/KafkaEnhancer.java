package ro.go.adrhc.springkafkastreams.enhancer;

import org.apache.kafka.streams.StreamsBuilder;

public class KafkaEnhancer {
	public static StreamsBuilderEnhancer enhance(StreamsBuilder streamsBuilder) {
		return new StreamsBuilderEnhancer(streamsBuilder);
	}
}
