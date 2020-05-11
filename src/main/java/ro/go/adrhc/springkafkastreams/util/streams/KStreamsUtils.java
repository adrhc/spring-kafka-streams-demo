package ro.go.adrhc.springkafkastreams.util.streams;

public class KStreamsUtils {
	public static String joinName(String from, String to) {
		return from + "-join-" + to;
	}
}
