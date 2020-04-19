package ro.go.adrhc.springkafkastreams.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.messages.PeriodExceeded;
import ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent;

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
		log.debug("\n\tNotification:\t{} spent a total of {} GBP on {}\n\tOverdue:\t{} GBP\n\tLimit:\t\t{} GBP",
				dts.getClientId(), dts.getAmount(), format(dts.getTime()),
				dts.getAmount() - de.getDailyMaxAmount(), de.getDailyMaxAmount());
	}

	@Override
	public void send(PeriodExceeded de) {
		PeriodTotalSpent dts = de.getPeriodTotalSpent();
		log.debug("\n\tNotification:\t{} spent a total of {} GBP for the period {} - {}\n\tOverdue:\t{} GBP\n\tLimit:\t\t{} GBP",
				dts.getClientId(), dts.getAmount(),
				format(dts.getTime().minus(app.getWindowSize(), app.getWindowUnit()).plusDays(1)),
				format(dts.getTime()), dts.getAmount() - de.getPeriodMaxAmount(), de.getPeriodMaxAmount());
	}
}
