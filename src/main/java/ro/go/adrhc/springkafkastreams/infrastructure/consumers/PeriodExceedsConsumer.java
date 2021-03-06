package ro.go.adrhc.springkafkastreams.infrastructure.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.infrastructure.phone.PhoneMessageSender;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodExceeded;

@Profile("!test")
@Component
@Slf4j
public class PeriodExceedsConsumer {
	private final PhoneMessageSender phoneMessageSender;

	public PeriodExceedsConsumer(PhoneMessageSender phoneMessageSender) {this.phoneMessageSender = phoneMessageSender;}

	@KafkaListener(id = "periodExceedsNotifier", topics = "${topic.period-exceeds}",
			clientIdPrefix = "periodExceedsConsumer")
	public void consume(PeriodExceeded pe) {
		phoneMessageSender.send(pe);
	}
}
