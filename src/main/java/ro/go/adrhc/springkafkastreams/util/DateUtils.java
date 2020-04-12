package ro.go.adrhc.springkafkastreams.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;

public class DateUtils {
	private static final DateTimeFormatter FORMATTER =
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

	public static String format(TemporalAccessor temporalAccessor) {
		return FORMATTER.format(temporalAccessor);
	}
}
