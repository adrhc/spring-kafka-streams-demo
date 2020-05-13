package ro.go.adrhc.springkafkastreams.infrastructure.phone;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}
