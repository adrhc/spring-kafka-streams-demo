package ro.go.adrhc.springkafkastreams.util;

public class KStreamsUtils {
	public static String joinName(String from, String to) {
		return from + "-join-" + to;
	}
}
