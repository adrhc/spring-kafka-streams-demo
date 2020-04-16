package ro.go.adrhc.springkafkastreams.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class DateUtils {
	private static final DateTimeFormatter LocalDate_FORMATTER =
			DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
	private static final DateTimeFormatter LocalDateTime_FORMATTER =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	public static long millisecondsOf(LocalDate localDate) {
		return localDate.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC) * 1000;
	}

	public static long millisecondsOf(LocalDateTime ldt) {
		return ldt.toEpochSecond(ZoneOffset.UTC) * 1000;
	}

	public static LocalDate localDateOf(long milliseconds) {
		return LocalDate.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC);
	}

	public static LocalDate localDateOf(List<Integer> integers) {
		return LocalDate.of(integers.get(0), integers.get(1), integers.get(2));
	}

	public static List<Integer> integersOf(LocalDate localDate) {
		return List.of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
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
