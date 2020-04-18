package ro.go.adrhc.springkafkastreams.services;

import ro.go.adrhc.springkafkastreams.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}