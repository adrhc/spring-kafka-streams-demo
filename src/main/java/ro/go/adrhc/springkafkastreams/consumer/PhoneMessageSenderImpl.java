package ro.go.adrhc.springkafkastreams.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class PhoneMessageSenderImpl implements PhoneMessageSender {
	@Override
	public void send(DailyExceeded de) {
		DailyTotalSpent dts = de.getDailyTotalSpent();
		log.debug("\n\tNotification:\t{} spent {} GBP on {}\n\tOverdue:\t\t{} GBP\n\tLimit:\t\t\t{} GBP",
				dts.getClientId(), dts.getAmount(), format(dts.getTime()),
				dts.getAmount() - de.getDailyMaxAmount(), de.getDailyMaxAmount());
	}
}
