package ro.go.adrhc.springkafkastreams.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Windowed;

import java.text.MessageFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;

@Slf4j
public class WindowUtils {
	private static final ThreadLocal<MessageFormat> WINDOW_KEY =
			ThreadLocal.withInitial(() -> new MessageFormat("{0}-{1}"));
	private static final DateTimeFormatter keyLocalDateFormat =
			DateTimeFormatter.ofPattern("yyyy.MM.dd");

	public static <T> String keyOf(Windowed<T> windowed) {
		return WINDOW_KEY.get().format(new Object[]{windowed.key().toString(),
				localDateOf(windowed.window().start()).format(keyLocalDateFormat)});
	}

	public static Optional<WindowBasedKey<String>> parse(String windowBasedKey) {
		Object[] parts;
		try {
			parts = WINDOW_KEY.get().parse(windowBasedKey);
			return Optional.of(new WindowBasedKey<>((String) parts[0],
					keyLocalDateFormat.parse((String) parts[1], LocalDate::from)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class WindowBasedKey<T> {
		private final T data;
		private final LocalDate time;
	}
}
