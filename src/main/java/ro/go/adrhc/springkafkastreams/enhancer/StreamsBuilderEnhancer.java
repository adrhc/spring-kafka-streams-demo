package ro.go.adrhc.springkafkastreams.enhancer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

@RequiredArgsConstructor
public class StreamsBuilderEnhancer {
	private final StreamsBuilder streamsBuilder;

	public <K, V> KStreamEnhancer<K, V> stream(KStream<K, V> kStream) {
		return new KStreamEnhancer<>(kStream, streamsBuilder);
	}
}
