package ro.go.adrhc.springkafkastreams.consumer;

import ro.go.adrhc.springkafkastreams.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}
