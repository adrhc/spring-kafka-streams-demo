package ro.go.adrhc.springkafkastreams.consumer;

import ro.go.adrhc.springkafkastreams.model.DailyExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);
}
