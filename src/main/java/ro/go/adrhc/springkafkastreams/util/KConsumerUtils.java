package ro.go.adrhc.springkafkastreams.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class KConsumerUtils {
	public static void consumeFromTopics(Consumer<?, ?> consumer, String... topicsToConsume) {
		final AtomicBoolean assigned = new AtomicBoolean();
		consumer.subscribe(Arrays.asList(topicsToConsume), new ConsumerRebalanceListener() {

			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
			}

			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
				assigned.set(true);
				log.debug("partitions assigned: " + partitions);
			}

		});
		ConsumerRecords<?, ?> records = null;
		int n = 0;
		while (!assigned.get() && n++ < 600) { // NOSONAR magic #
			records = consumer.poll(Duration.ofMillis(100)); // force assignment NOSONAR magic #
		}
		if (records != null && records.count() > 0) {
			final ConsumerRecords<?, ?> theRecords = records;
			log.debug("Records received on initial poll for assignment; re-seeking to beginning; "
					+ theRecords.partitions().stream()
					.flatMap(p -> theRecords.records(p).stream())
					// map to same format as send metadata toString()
					.map(r -> r.topic() + "-" + r.partition() + "@" + r.offset())
					.collect(Collectors.toList()));
			consumer.seekToBeginning(records.partitions());
		}
		if (!assigned.get()) {
			throw new IllegalStateException("Failed to be assigned partitions from the embedded topics");
		}
		log.debug("Subscription Initiated");
	}
}
