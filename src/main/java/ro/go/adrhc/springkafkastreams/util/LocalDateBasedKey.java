package ro.go.adrhc.springkafkastreams.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Windowed;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;

@Getter
@AllArgsConstructor
@Slf4j
public class LocalDateBasedKey<T> {
	private static final ThreadLocal<MessageFormat> WINDOW_KEY =
			ThreadLocal.withInitial(() -> new MessageFormat("{0}-{1}"));
	private static final DateTimeFormatter keyLocalDateFormat =
			DateTimeFormatter.ofPattern("yyyy.MM.dd");

	private final T data;
	private final LocalDate time;

	public static <T> String keyOf(T data, LocalDate time) {
		return data.toString() + '-' + time.format(keyLocalDateFormat);
	}

	public static <T> String keyOf(Windowed<T> windowed) {
		return LocalDateBasedKey.keyOf(windowed.key().toString(),
				localDateOf(windowed.window().start()));
	}

	public static Optional<LocalDateBasedKey<String>> parseWithStringData(String key) {
		Object[] parts;
		try {
			parts = WINDOW_KEY.get().parse(key);
			return Optional.of(new LocalDateBasedKey<>((String) parts[0],
					keyLocalDateFormat.parse((String) parts[1], LocalDate::from)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Optional.empty();
		}
	}
}
