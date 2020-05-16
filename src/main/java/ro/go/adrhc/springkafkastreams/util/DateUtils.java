package ro.go.adrhc.springkafkastreams.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {
	private static final DateTimeFormatter LocalDate_FORMATTER =
			DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
	private static final DateTimeFormatter LocalDateTime_FORMATTER =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	public static String format(LocalDate localDateTime) {
		return LocalDate_FORMATTER.format(localDateTime);
	}

	public static String format(LocalDateTime localDateTime) {
		return LocalDateTime_FORMATTER.format(localDateTime);
	}
}
