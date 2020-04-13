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
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.time.Duration;

@Component
public class StreamsHelper {
	public static final int DELAY = 5;
	private final TopicsProperties properties;
	private final JsonSerde<Transaction> transactionSerde;
	private final JsonSerde<ClientProfile> clientProfileSerde;
	private final JsonSerde<DailyTotalSpent> dailyTotalSpentSerde;

	public StreamsHelper(TopicsProperties properties, @Qualifier("transactionSerde") JsonSerde<Transaction> transactionSerde, @Qualifier("clientProfileSerde") JsonSerde<ClientProfile> clientProfileSerde, @Qualifier("dailyTotalSpentSerde") JsonSerde<DailyTotalSpent> dailyTotalSpentSerde) {
		this.properties = properties;
		this.transactionSerde = transactionSerde;
		this.clientProfileSerde = clientProfileSerde;
		this.dailyTotalSpentSerde = dailyTotalSpentSerde;
	}

	public Produced<String, Integer> produceInteger(String processorName) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	private Consumed<String, Transaction> consumeTransaction(String processorName) {
		return Consumed.with(Serdes.String(), transactionSerde).withName(processorName);
	}

	private Consumed<String, Integer> consumeInteger(String processorName) {
		return Consumed.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	private Consumed<String, DailyTotalSpent> consumeDailyTotalSpent(String processorName) {
		return Consumed.with(Serdes.String(), dailyTotalSpentSerde).withName(processorName);
	}

	public Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId() {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentByClientId")
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(DELAY + 1));
	}

	public KStream<String, ClientProfile> clientProfileStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getClientProfile(),
				Consumed.<String, ClientProfile>
						as(properties.getClientProfile())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde));
	}

	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(properties.getClientProfile(),
				Consumed.<String, ClientProfile>
						as(properties.getClientProfile())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde),
				Materialized.<String, ClientProfile, KeyValueStore<Bytes, byte[]>>
						as(properties.getClientProfile())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde));
	}

	public Joined<String, DailyTotalSpent, ClientProfile> dailyTotalSpentJoinClientProfile() {
		return Joined.with(Serdes.String(), dailyTotalSpentSerde,
				clientProfileSerde, "dailyTotalSpentJoinClientProfile");
	}

	public KStream<String, Integer> dailyTotalSpent(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyTotalSpent(),
				this.consumeInteger(properties.getDailyTotalSpent()));
	}

	public KStream<String, DailyTotalSpent> dailyExceeds(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyExceeded(),
				this.consumeDailyTotalSpent(properties.getDailyExceeded()));
	}

	public KStream<String, Transaction> transactions(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(),
				this.consumeTransaction(properties.getTransactions()));
	}

	public Grouped<String, Transaction> transactionsGroupedByClientId() {
		return Grouped.with("transactionsGroupedByClientId", Serdes.String(), transactionSerde);
	}
}
