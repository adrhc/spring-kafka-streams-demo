package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.KStreamEnh;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.messages.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class StreamsHelper {
	private final TopicsProperties properties;

	public StreamsHelper(TopicsProperties properties) {
		this.properties = properties;
	}

	public static String dailyTotalSpentByClientIdStoreName(int windowSize, ChronoUnit windowUnit) {
		return "dailyTotalSpentByClientId-" + windowSize + windowUnit.toString();
	}

	public static String periodTotalSpentByClientIdStoreName(int windowSize, ChronoUnit windowUnit) {
		return "periodTotalSpentByClientId-" + windowSize + windowUnit.toString();
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

	private Consumed<String, Command> consumeCommand() {
		return Consumed.as(properties.getCommands());
	}

	private Consumed<String, DailyTotalSpent> consumeDailyTotalSpent() {
		return Consumed.as(properties.getDailyTotalSpent());
	}

	public Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(
			int retentionDays, int windowSize, ChronoUnit windowUnit) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as(dailyTotalSpentByClientIdStoreName(windowSize, windowUnit))
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(retentionDays));
	}

	public Materialized<String, Integer, KeyValueStore<String, Integer>>
	periodTotalSpentByClientId(int windowSize, ChronoUnit windowUnit) {
		return Materialized.<String, Integer, KeyValueStore<String, Integer>>
				as(periodTotalSpentByClientIdStoreName(windowSize, windowUnit))
				.withValueSerde(Serdes.Integer());
	}

	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.table(properties.getClientProfiles(),
				Consumed.as(properties.getClientProfiles()),
				Materialized.as(properties.getClientProfiles()));
	}

	public KTable<String, Integer> dailyTotalSpentTable(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.table(properties.getDailyTotalSpent(),
				Consumed.<String, Integer>
						as(properties.getDailyTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()),
				Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>
						as(properties.getDailyTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()));
	}

	public KTable<String, Integer> periodTotalSpentTable(StreamsBuilderEnh streamsBuilder) {
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

	public KStreamEnh<String, Transaction> transactionsStream(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(), this.consumeTransaction());
	}

	public KStreamEnh<String, Command> commandsStream(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.stream(properties.getCommands(), this.consumeCommand());
	}

	public Grouped<String, Transaction> transactionsGroupedByClientId() {
		return Grouped.as("transactionsGroupedByClientId");
	}
}
