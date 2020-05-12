package ro.go.adrhc.springkafkastreams.services;

import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}
