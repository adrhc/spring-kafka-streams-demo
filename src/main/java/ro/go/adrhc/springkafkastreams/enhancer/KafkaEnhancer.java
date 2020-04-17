package ro.go.adrhc.springkafkastreams.enhancer;

import org.apache.kafka.streams.kstream.KStream;

public class KafkaEnhancer {
	public static <K, V> KStreamEx<K, V> enhance(KStream<K, V> stream) {
		return new KStreamEx<>(stream);
	}
}
