package ro.go.adrhc.springkafkastreams.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;
import ro.go.adrhc.springkafkastreams.model.PeriodExceeded;

@Component
@Slf4j
public class PeriodExceedsConsumer {
	private final PhoneMessageSender sender;

	public PeriodExceedsConsumer(PhoneMessageSender sender) {this.sender = sender;}

	@KafkaListener(id = "periodExceedsNotifier", topics = "${topic.period-exceeds}")
	public void consume(PeriodExceeded pe) {
		sender.send(pe);
	}
}
