package ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.infrastructure.adapters.outbound.PhoneMessageSender;
import ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.topologies.payments.exceeds.period.messages.PeriodExceeded;

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
