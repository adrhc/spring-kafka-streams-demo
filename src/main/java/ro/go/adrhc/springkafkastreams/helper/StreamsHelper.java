package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.*;

import java.time.Duration;

@Component
public class StreamsHelper {
	public static final int DELAY = 5;
	private final TopicsProperties properties;

	public StreamsHelper(TopicsProperties properties) {
		this.properties = properties;
	}

	public Produced<String, Integer> produceInteger(String processorName) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	public Produced<String, DailyExceeded> produceDailyExceeded() {
		return Produced.as(properties.getDailyExceeds());
	}

	public Produced<String, PeriodExceeded> producePeriodExceeded() {
		return Produced.as(properties.getPeriodExceeds());
	}

	private Consumed<String, Transaction> consumeTransaction() {
		return Consumed.as(properties.getTransactions());
	}

	private Consumed<String, DailyTotalSpent> consumeDailyTotalSpent() {
		return Consumed.as(properties.getDailyTotalSpent());
	}

	public Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(
			int retentionDays, String nameSuffix) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentByClientId-" + nameSuffix)
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(retentionDays));
	}

	public Materialized<String, Integer, KeyValueStore<String, Integer>> periodTotalSpentByClientId() {
		return Materialized.<String, Integer, KeyValueStore<String, Integer>>
				as("periodTotalSpentByClientId")
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer());
	}

	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(properties.getClientProfiles(),
				Consumed.as(properties.getClientProfiles()),
				Materialized.as(properties.getClientProfiles()));
	}

	public KTable<String, Integer> periodTotalSpentTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(properties.getPeriodTotalSpent(),
				Consumed.<String, Integer>
						as(properties.getPeriodTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()),
				Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>
						as(properties.getPeriodTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()));
	}

	public Joined<String, DailyTotalSpent, ClientProfile> dailyTotalSpentJoinClientProfile() {
		return Joined.as("dailyTotalSpentJoinClientProfile");
	}

	public Joined<String, PeriodTotalSpent, ClientProfile> periodTotalSpentJoinClientProfile() {
		return Joined.as("periodTotalSpentJoinClientProfile");
	}

	public KStream<String, DailyTotalSpent> dailyExceedsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyExceeds(), this.consumeDailyTotalSpent());
	}

	public KStream<String, Transaction> transactionsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(), this.consumeTransaction());
	}

	public Grouped<String, Transaction> transactionsGroupedByClientId() {
		return Grouped.as("transactionsGroupedByClientId");
	}
}
