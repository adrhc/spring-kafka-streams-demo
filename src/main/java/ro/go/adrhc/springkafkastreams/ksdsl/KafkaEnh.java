package ro.go.adrhc.springkafkastreams.ksdsl;

import org.apache.kafka.streams.StreamsBuilder;

public class KafkaEnh {
	public static StreamsBuilderEnh enhance(StreamsBuilder streamsBuilder) {
		return new StreamsBuilderEnh(streamsBuilder);
	}
}
