package ro.go.adrhc.springkafkastreams.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {
	private static DateTimeFormatter formatter =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	public static long millisecondsOf(LocalDateTime ldt) {
		return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	public static LocalDateTime localDateTimeOf(long milliseconds) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC);
	}

	public static String format(LocalDateTime ldt) {
		return formatter.format(ldt);
	}
}
