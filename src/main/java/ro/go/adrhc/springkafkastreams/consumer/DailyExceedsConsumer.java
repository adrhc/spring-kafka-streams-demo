package ro.go.adrhc.springkafkastreams.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;

@Component
@Slf4j
public class DailyExceedsConsumer {
	private final PhoneMessageSender sender;

	public DailyExceedsConsumer(PhoneMessageSender sender) {this.sender = sender;}

	@KafkaListener(id = "dailyExceedsNotifier", topics = "${topic.daily-exceeds}")
	public void consume(DailyExceeded de) {
		sender.send(de);
	}
}
