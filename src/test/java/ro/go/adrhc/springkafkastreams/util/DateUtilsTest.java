package ro.go.adrhc.springkafkastreams.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static org.assertj.core.api.Assertions.assertThat;
import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.util.DateUtils.*;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Slf4j
class DateUtilsTest {
	@Test
	void dateUtilsTest() {
		LocalDateTime initLdt = LocalDateTime.now();
		long milliseconds = millisecondsOf(initLdt);
		log.debug("initial long time: {}", milliseconds);
		String initFormatted = format(initLdt);
		log.debug("initial formatted time: {}", initFormatted);
		LocalDateTime ldt = localDateTimeOf(milliseconds);
		assertThat(ldt).isEqualToIgnoringNanos(initLdt);
		String formatted = format(ldt);
		log.debug("formatted time: {}", formatted);
		assertThat(formatted).isEqualTo(initFormatted);
		LocalDate ld = localDateOf(milliseconds);
		log.debug("formatted LocalDate: {}", format(ld));
		formatted = ld.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
		log.debug("MEDIUM format: " + formatted);
	}
}
