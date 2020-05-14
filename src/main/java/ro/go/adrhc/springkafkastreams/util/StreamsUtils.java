package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.state.ValueAndTimestamp;

public class StreamsUtils {
	public static <T> T valueFrom(ValueAndTimestamp valueAndTimestamp) {
		return (T) valueAndTimestamp.value();
	}
}
