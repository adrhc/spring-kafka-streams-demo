package ro.go.adrhc.springkafkastreams.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {
	private static final DateTimeFormatter LocalDate_FORMATTER =
			DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
	private static final DateTimeFormatter LocalDateTime_FORMATTER =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	public static long millisecondsOf(LocalDateTime ldt) {
		return ldt.toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	public static LocalDate localDateOf(long milliseconds) {
		return LocalDate.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC);
	}

	public static LocalDateTime localDateTimeOf(long milliseconds) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC);
	}

	public static String format(LocalDate localDateTime) {
		return LocalDate_FORMATTER.format(localDateTime);
	}

	public static String format(LocalDateTime localDateTime) {
		return LocalDateTime_FORMATTER.format(localDateTime);
	}
}
