package ro.go.adrhc.springkafkastreams.infrastructure.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.infrastructure.phone.PhoneMessageSender;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;

@Profile("!test")
@Component
@Slf4j
public class DailyExceedsConsumer {
	private final PhoneMessageSender phoneMessageSender;

	public DailyExceedsConsumer(PhoneMessageSender phoneMessageSender) {this.phoneMessageSender = phoneMessageSender;}

	@KafkaListener(id = "dailyExceedsNotifier", topics = "${topic.daily-exceeds}",
			clientIdPrefix = "dailyExceedsConsumer")
	public void consume(DailyExceeded de) {
		phoneMessageSender.send(de);
	}
}
