package ro.go.adrhc.springkafkastreams.infrastructure.outbound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodExceeded;
import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodTotalSpent;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class PhoneMessageSenderImpl implements PhoneMessageSender {
	private final AppProperties app;

	public PhoneMessageSenderImpl(AppProperties app) {
		this.app = app;
	}

	@Override
	public void send(DailyExceeded de) {
		DailyTotalSpent dts = de.getDailyTotalSpent();
		log.debug("\n\tNotification:\t{} spent a total of {} {} on {}\n\tOverdue:\t{} {}\n\tLimit:\t\t{} {}",
				dts.getClientId(), dts.getAmount(), app.getCurrency(), format(dts.getTime()),
				dts.getAmount() - de.getDailyMaxAmount(), app.getCurrency(),
				de.getDailyMaxAmount(), app.getCurrency());
	}

	@Override
	public void send(PeriodExceeded de) {
		PeriodTotalSpent dts = de.getPeriodTotalSpent();
		log.debug("\n\tNotification:\t{} spent a total of {} {} during {} - {}\n\tOverdue:\t{} {}\n\tLimit:\t\t{} {}",
				dts.getClientId(), dts.getAmount(), app.getCurrency(),
				format(dts.getTime().minus(app.getWindowSize(), app.getWindowUnit()).plusDays(1)),
				format(dts.getTime()), dts.getAmount() - de.getPeriodMaxAmount(),
				app.getCurrency(), de.getPeriodMaxAmount(), app.getCurrency());
	}
}
