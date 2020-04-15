package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.*;

import java.time.Duration;

@Component
public class StreamsHelper {
	public static final int DELAY = 5;
	private final TopicsProperties properties;
	private final JsonSerde<Transaction> transactionSerde;
	private final JsonSerde<ClientProfile> clientProfileSerde;
	private final JsonSerde<DailyTotalSpent> dailyTotalSpentSerde;
	private final JsonSerde<PeriodTotalSpent> periodTotalSpentSerde;
	private final JsonSerde<DailyExceeded> dailyExceededSerde;
	private final JsonSerde<PeriodExceeded> periodExceededSerde;

	public StreamsHelper(TopicsProperties properties, @Qualifier("transactionSerde") JsonSerde<Transaction> transactionSerde, @Qualifier("clientProfileSerde") JsonSerde<ClientProfile> clientProfileSerde, @Qualifier("dailyTotalSpentSerde") JsonSerde<DailyTotalSpent> dailyTotalSpentSerde, @Qualifier("periodTotalSpentSerde") JsonSerde<PeriodTotalSpent> periodTotalSpentSerde, @Qualifier("dailyExceededSerde") JsonSerde<DailyExceeded> dailyExceededSerde, @Qualifier("periodExceededSerde") JsonSerde<PeriodExceeded> periodExceededSerde) {
		this.properties = properties;
		this.transactionSerde = transactionSerde;
		this.clientProfileSerde = clientProfileSerde;
		this.dailyTotalSpentSerde = dailyTotalSpentSerde;
		this.periodTotalSpentSerde = periodTotalSpentSerde;
		this.dailyExceededSerde = dailyExceededSerde;
		this.periodExceededSerde = periodExceededSerde;
	}

	private Consumed<String, Integer> consumeInteger(String processorName) {
		return Consumed.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	public Produced<String, Integer> produceInteger(String processorName) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	public Produced<String, DailyExceeded> produceDailyExceeded() {
		return Produced.with(Serdes.String(), dailyExceededSerde).withName(properties.getDailyExceeds());
	}

	public Produced<String, PeriodExceeded> producePeriodExceeded() {
		return Produced.with(Serdes.String(), periodExceededSerde).withName(properties.getPeriodExceeds());
	}

	private Consumed<String, Transaction> consumeTransaction() {
		return Consumed.with(Serdes.String(), transactionSerde).withName(properties.getTransactions());
	}

	private Consumed<String, DailyTotalSpent> consumeDailyTotalSpent() {
		return Consumed.with(Serdes.String(), dailyTotalSpentSerde).withName(properties.getDailyTotalSpent());
	}

	public Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(int retentionDays) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentByClientId")
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(retentionDays));
	}

	public KStream<String, ClientProfile> clientProfileStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getClientProfiles(),
				Consumed.<String, ClientProfile>
						as(properties.getClientProfiles())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde));
	}

	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(properties.getClientProfiles(),
				Consumed.<String, ClientProfile>
						as(properties.getClientProfiles())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde),
				Materialized.<String, ClientProfile, KeyValueStore<Bytes, byte[]>>
						as(properties.getClientProfiles())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde));
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
		return Joined.with(Serdes.String(), dailyTotalSpentSerde,
				clientProfileSerde, "dailyTotalSpentJoinClientProfile");
	}

	public Joined<String, PeriodTotalSpent, ClientProfile> periodTotalSpentJoinClientProfile() {
		return Joined.with(Serdes.String(), periodTotalSpentSerde,
				clientProfileSerde, "periodTotalSpentJoinClientProfile");
	}

	public KStream<String, DailyTotalSpent> dailyExceedsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyExceeds(), this.consumeDailyTotalSpent());
	}

	public KStream<String, Transaction> transactionsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(), this.consumeTransaction());
	}

	public Grouped<String, Transaction> transactionsGroupedByClientId() {
		return Grouped.with("transactionsGroupedByClientId", Serdes.String(), transactionSerde);
	}
}
