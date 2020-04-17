package ro.go.adrhc.springkafkastreams.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.messages.PeriodExceeded;
import ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class PhoneMessageSenderImpl implements PhoneMessageSender {
	private final int totalPeriod;

	public PhoneMessageSenderImpl(@Value("${total.period}") int totalPeriod) {this.totalPeriod = totalPeriod;}

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
				dts.getClientId(), dts.getAmount(), format(dts.getTime().minusDays(totalPeriod - 1)),
				format(dts.getTime()), dts.getAmount() - de.getPeriodMaxAmount(), de.getPeriodMaxAmount());
	}
}
