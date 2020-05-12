package ro.go.adrhc.springkafkastreams.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.services.PhoneMessageSender;
import ro.go.adrhc.springkafkastreams.topologies.exceeds.daily.messages.DailyExceeded;

@Profile("!test")
@Component
@Slf4j
public class DailyExceedsConsumer {
	private final PhoneMessageSender sender;

	public DailyExceedsConsumer(PhoneMessageSender sender) {this.sender = sender;}

	@KafkaListener(id = "dailyExceedsNotifier", topics = "${topic.daily-exceeds}",
			clientIdPrefix = "dailyExceedsConsumer")
	public void consume(DailyExceeded de) {
		sender.send(de);
	}
}