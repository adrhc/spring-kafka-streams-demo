package ro.go.adrhc.springkafkastreams.topologies.exceeds.period;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.topologies.exceeds.AbstractExceeds;
import ro.go.adrhc.springkafkastreams.topologies.exceeds.period.messages.PeriodExceeded;
import ro.go.adrhc.springkafkastreams.topologies.exceeds.period.messages.PeriodTotalSpent;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateOf;

@Slf4j
public abstract class AbstractPeriodExceeds extends AbstractExceeds {
	protected final TopicsProperties topicsProperties;
	protected final AppProperties appProperties;

	protected AbstractPeriodExceeds(TopicsProperties topicsProperties, AppProperties appProperties) {
		this.topicsProperties = topicsProperties;
		this.appProperties = appProperties;
	}

	protected Joined<String, PeriodTotalSpent, ClientProfile> periodTotalSpentJoinClientProfile() {
		return Joined.as("periodTotalSpentJoinClientProfile");
	}

	protected Produced<String, PeriodExceeded> producePeriodExceeded() {
		return Produced.as(topicsProperties.getPeriodExceeds());
	}

	protected ValueJoiner<PeriodTotalSpent, ClientProfile, PeriodExceeded>
	joinPeriodTotalSpentWithClientProfileOnClientId(int windowSize, ChronoUnit windowUnit) {
		return (PeriodTotalSpent pts, ClientProfile cp) -> {
			if (cp.getPeriodMaxAmount() < pts.getAmount()) {
				return new PeriodExceeded(cp.getPeriodMaxAmount(), pts);
			}
			log.trace("\n\tskipping total spent for {} {} under {} {}\n\t{}\n\t{}",
					windowSize, windowUnit.toString().toLowerCase(),
					cp.getPeriodMaxAmount(), appProperties.getCurrency(), pts, cp);
			return null;
		};
	}

	protected KeyValue<String, PeriodTotalSpent> clientIdPeriodTotalSpentOf(Windowed<String> clientIdWindow, Integer amount) {
		String clientId = clientIdWindow.key();
		LocalDate time = localDateOf(clientIdWindow.window().end()).minusDays(1);
		log.trace("\n\t{} spent a total of {} {} until {} (including)",
				clientId, amount, appProperties.getCurrency(), format(time));
		return KeyValue.pair(clientId, new PeriodTotalSpent(clientId, time, amount));
	}

	protected void printPeriodTotalExpenses(Windowed<String> clientIdWindow,
			Integer amount, int windowSize, TemporalUnit unit) {
		LocalDate time = localDateOf(clientIdWindow.window().end()).minusDays(1);
		log.debug("\n\t{} spent a total of {} {} during {} - {}",
				clientIdWindow.key(), amount, appProperties.getCurrency(),
				format(time.minus(windowSize, unit).plusDays(1)), format(time));
	}

	public String periodTotalSpentByClientIdStoreName() {
		return "periodTotalSpentByClientId-" + appProperties.getWindowSize() + appProperties.getWindowUnit().toString();
	}
}
