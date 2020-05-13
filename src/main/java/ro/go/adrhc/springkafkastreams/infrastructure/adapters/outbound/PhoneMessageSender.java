package ro.go.adrhc.springkafkastreams.infrastructure.adapters.outbound;

import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}
