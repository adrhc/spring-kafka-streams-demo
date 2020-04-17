package ro.go.adrhc.springkafkastreams.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.messages.PeriodExceeded;
import ro.go.adrhc.springkafkastreams.services.PhoneMessageSender;

@Profile("!test")
@Component
@Slf4j
public class PeriodExceedsConsumer {
	private final PhoneMessageSender sender;

	public PeriodExceedsConsumer(PhoneMessageSender sender) {this.sender = sender;}

	@KafkaListener(id = "periodExceedsNotifier", topics = "${topic.period-exceeds}",
			clientIdPrefix = "periodExceedsConsumer")
	public void consume(PeriodExceeded pe) {
		sender.send(pe);
	}
}
