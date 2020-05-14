package ro.go.adrhc.springkafkastreams.infrastructure.phone;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyExceeded;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodExceeded;

public interface PhoneMessageSender {
	void send(DailyExceeded de);

	void send(PeriodExceeded pe);
}
