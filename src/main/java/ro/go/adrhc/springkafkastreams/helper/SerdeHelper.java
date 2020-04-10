package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class SerdeHelper {
	@Autowired
	private JsonSerde<?> jsonSerde;

	public <V> Produced<String, V> stringKeyProduced(String name) {
		return Produced.with(Serdes.String(), (Serde<V>) jsonSerde).withName(name);
	}

	public <V> Consumed<String, V> stringKeyConsumed(String name) {
		return Consumed.with(Serdes.String(), (Serde<V>) jsonSerde).withName(name);
	}

	public <V> KStream<String, V> kStreamOf(String topic, StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(topic, this.stringKeyConsumed("stream-" + topic));
	}
}
