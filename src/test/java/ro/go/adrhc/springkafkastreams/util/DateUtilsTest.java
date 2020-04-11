package ro.go.adrhc.springkafkastreams.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.*;

@Slf4j
class DateUtilsTest {
	@Test
	void dateUtilsTest() {
		LocalDateTime initLdt = LocalDateTime.now();
		long time = localDateTimeToLong(initLdt);
		log.debug("initial long time: {}", time);
		String initFormatted = localDateTimeToString(initLdt);
		log.debug("initial formatted time: {}", initFormatted);
		LocalDateTime ldt = localDateTimeOfSeconds(time);
		assertThat(ldt).isEqualToIgnoringNanos(initLdt);
		String formatted = localDateTimeToString(ldt);
		log.debug("formatted time: {}", formatted);
		assertThat(formatted).isEqualTo(initFormatted);
	}
}
