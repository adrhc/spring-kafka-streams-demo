package ro.go.adrhc.springkafkastreams.util;

import org.apache.kafka.streams.kstream.Windowed;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;

public class WindowUtils {
	private static final ThreadLocal<MessageFormat> WINDOW_KEY =
			ThreadLocal.withInitial(() -> new MessageFormat("{0}-{1}"));
	private static final DateTimeFormatter keyLocalDateFormat =
			DateTimeFormatter.ofPattern("yyyyMMdd");

	public static <T> String keyOf(Windowed<T> windowed) {
		return WINDOW_KEY.get().format(new Object[]{windowed.key().toString(),
				localDateOf(windowed.window().start()).format(keyLocalDateFormat)});
	}
}
