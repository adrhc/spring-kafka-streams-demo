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
	private final JsonSerde<DailyTotalSpent> dailyExpensesSerde;

	public StreamsHelper(TopicsProperties properties, @Qualifier("transactionSerde") JsonSerde<Transaction> transactionSerde, @Qualifier("clientProfileSerde") JsonSerde<ClientProfile> clientProfileSerde, JsonSerde<DailyTotalSpent> dailyExpensesSerde) {
		this.properties = properties;
		this.transactionSerde = transactionSerde;
		this.clientProfileSerde = clientProfileSerde;
		this.dailyExpensesSerde = dailyExpensesSerde;
	}

	public Produced<String, Integer> producedWithInteger(String processorName) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	private Consumed<String, Transaction> consumedWithTransaction(String processorName) {
		return Consumed.with(Serdes.String(), transactionSerde).withName(processorName);
	}

	private Consumed<String, Integer> consumedWithDailyExpenses(String processorName) {
		return Consumed.with(Serdes.String(), Serdes.Integer()).withName(processorName);
	}

	private Consumed<String, DailyTotalSpent> consumedWithDailyExpensesDetails(String processorName) {
		return Consumed.with(Serdes.String(), dailyExpensesSerde).withName(processorName);
	}

	public Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailySpentByClientId() {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailySpentByClientId")
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(DELAY + 1));
//				.withRetention(Duration.ofDays(12 * 30));
	}

	public KStream<String, ClientProfile> clientProfiles(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getClientProfile(),
				Consumed.<String, ClientProfile>
						as(properties.getClientProfile())
						.withKeySerde(Serdes.String())
						.withValueSerde(clientProfileSerde));
	}

	public KTable<String, ClientProfile> clientProfilesTable(StreamsBuilder streamsBuilder) {
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

	public Joined<String, DailyTotalSpent, ClientProfile> dailyExpensesDetailsByClientIdJoin() {
		return Joined.with(Serdes.String(), dailyExpensesSerde,
				clientProfileSerde, "dailyExpenses_join_clientProfiles");
	}

	public KStream<String, Integer> dailyExpenses(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyTotalSpent(),
				this.consumedWithDailyExpenses(properties.getDailyTotalSpent()));
	}

	public KStream<String, DailyTotalSpent> dailyExpensesDetails(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getDailyExpensesDetails(),
				this.consumedWithDailyExpensesDetails(properties.getDailyExpensesDetails()));
	}

	public KStream<String, Transaction> transactionsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(),
				this.consumedWithTransaction(properties.getTransactions()));
	}

	public Grouped<String, Transaction> transactionsByClientId() {
		return Grouped.with("transactionsByClientId_group", Serdes.String(), transactionSerde);
	}
}
