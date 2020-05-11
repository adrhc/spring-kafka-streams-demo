package ro.go.adrhc.springkafkastreams.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.messages.*;
import ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;
import static ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey.parseWithStringData;

@Component
@Slf4j
public class PaymentsHelper {
	private AppProperties props;

	public PaymentsHelper(AppProperties props) {
		this.props = props;
	}

	public DailyExceeded joinDailyTotalSpentWithClientProfileOnClientId(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			return new DailyExceeded(cp.getDailyMaxAmount(), dts);
		}
		log.trace("\n\tskipping daily total spent under {} {}\n\t{}\n\t{}",
				cp.getDailyMaxAmount(), props.getCurrency(), dts, cp);
		return null;
	}

	public ValueJoiner<PeriodTotalSpent, ClientProfile, PeriodExceeded>
	joinPeriodTotalSpentWithClientProfileOnClientId(int windowSize, ChronoUnit windowUnit) {
		return (PeriodTotalSpent pts, ClientProfile cp) -> {
			if (cp.getPeriodMaxAmount() < pts.getAmount()) {
				return new PeriodExceeded(cp.getPeriodMaxAmount(), pts);
			}
			log.trace("\n\tskipping total spent for {} {} under {} {}\n\t{}\n\t{}",
					windowSize, windowUnit.toString().toLowerCase(),
					cp.getPeriodMaxAmount(), props.getCurrency(), pts, cp);
			return null;
		};
	}

	public KeyValue<String, PeriodTotalSpent> clientIdPeriodTotalSpentOf(String clientIdDay, Integer amount) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdDay);
		return winBasedKeyOptional
				.map(it -> {
					String clientId = it.getData();
					log.trace("\n\t{} spent a total of {} {} until {} (including)",
							clientId, amount, props.getCurrency(), format(it.getTime()));
					return KeyValue.pair(clientId, new PeriodTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}

	public KeyValue<String, PeriodTotalSpent> clientIdPeriodTotalSpentOf(Windowed<String> clientIdWindow, Integer amount) {
		String clientId = clientIdWindow.key();
		LocalDate time = localDateOf(clientIdWindow.window().end()).minusDays(1);
		log.trace("\n\t{} spent a total of {} {} until {} (including)",
				clientId, amount, props.getCurrency(), format(time));
		return KeyValue.pair(clientId, new PeriodTotalSpent(clientId, time, amount));
	}

	public KeyValue<String, DailyTotalSpent> clientIdDailyTotalSpentOf(String clientIdDay, Integer amount) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdDay);
		return winBasedKeyOptional
				.map(it -> {
					String clientId = it.getData();
					log.debug("\n\t{} spent a total of {} {} on {}",
							clientId, amount, props.getCurrency(), format(it.getTime()));
					return KeyValue.pair(clientId, new DailyTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}

	public void printPeriodTotalExpenses(String clientIdPeriod,
			Integer amount, int windowSize, TemporalUnit unit) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdPeriod);
		winBasedKeyOptional.ifPresent(it -> {
			String clientId = it.getData();
			log.debug("\n\t{} spent a total of {} {} during {} - {}",
					clientId, amount, props.getCurrency(),
					format(it.getTime().minus(windowSize, unit).plusDays(1)),
					format(it.getTime()));
		});
	}

	public void printPeriodTotalExpenses(Windowed<String> clientIdWindow,
			Integer amount, int windowSize, TemporalUnit unit) {
		LocalDate time = localDateOf(clientIdWindow.window().end()).minusDays(1);
		log.debug("\n\t{} spent a total of {} {} during {} - {}",
				clientIdWindow.key(), amount, props.getCurrency(),
				format(time.minus(windowSize, unit).plusDays(1)), format(time));
	}
}
