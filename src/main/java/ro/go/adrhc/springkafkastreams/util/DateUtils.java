package ro.go.adrhc.springkafkastreams.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {
	private static DateTimeFormatter formatter =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

	public static long localDateTimeToLong(LocalDateTime ldt) {
		return ldt.toEpochSecond(ZoneOffset.UTC);
	}

	public static LocalDateTime localDateTimeOfLong(long seconds) {
		return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
	}

	public static String localDateTimeToString(LocalDateTime ldt) {
		return formatter.format(ldt);
	}
}
