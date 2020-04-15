package ro.go.adrhc.springkafkastreams.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.transformers.debug.PeriodTotalExpensesAggregator;

import java.time.Duration;

import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;
import static ro.go.adrhc.springkafkastreams.stream.PaymentsUtils.joinPeriodTotalSpentWithClientProfileOnClientId;
import static ro.go.adrhc.springkafkastreams.stream.PaymentsUtils.printPeriodTotalExpenses;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

/**
 * see https://issues.apache.org/jira/browse/KAFKA-6817
 * see https://stackoverflow.com/questions/49872827/unknownproduceridexception-in-kafka-streams-when-enabling-exactly-once
 * transactional.id.expiration.ms set by default to 7 days
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@Profile("!test")
@Slf4j
public class PaymentsConfig {
	private final int totalPeriod;
	private final TopicsProperties properties;
	private final StreamsHelper helper;

	public PaymentsConfig(@Value("${total.period}") int totalPeriod, TopicsProperties properties, StreamsHelper helper) {
		this.totalPeriod = totalPeriod;
		this.properties = properties;
		this.helper = helper;
	}

	@Bean
	public KStream<String, ?> transactions(StreamsBuilder streamsBuilder) {
		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStream<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		KGroupedStream<String, Transaction> grouped = transactionsGroupedByClientId(transactions);
		dailyExceeds(grouped, clientProfileTable); // total expenses per day
		periodExceeds(grouped, clientProfileTable); // total expenses for a period

		return transactions;
	}

	/**
	 * group transactions by clientId
	 */
	private KGroupedStream<String, Transaction> transactionsGroupedByClientId(
			KStream<String, Transaction> transactions) {
		return transactions
				.peek((clientId, transaction) -> log.debug("\n\t{} spent {} GBP on {}", clientId,
						transaction.getAmount(), format(transaction.getTime())))
//				.transform(new TransformerDebugger<>())
//				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(helper.transactionsGroupedByClientId());
	}

	/**
	 * calculating total expenses per day
	 */
	private void dailyExceeds(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable) {
		groupedTransactions
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId(DELAY + 1, "1day"))
				// clientId-yyyy.MM.dd:amount
				.toStream((win, amount) -> keyOf(win))
				// save clientIdDay:amount into a compact stream (aka table)
				.through(properties.getDailyTotalSpent(),
						helper.produceInteger(properties.getDailyTotalSpent()))
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(PaymentsUtils::clientIdDailyTotalSpentOf)
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						PaymentsUtils::joinDailyTotalSpentWithClientProfileOnClientId,
						helper.dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// clientId:DailyExceeded stream
				.to(properties.getDailyExceeds(), helper.produceDailyExceeded());
	}

	/**
	 * calculating total expenses for a period
	 */
	private void periodExceeds(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable) {
		groupedTransactions
				// group by 30 days
				.windowedBy(TimeWindows.of(Duration.ofDays(totalPeriod))
						.advanceBy(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-30-days
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId(DELAY + totalPeriod, "3days"))
				// clientId-yyyy.MM.dd:amount
				.toStream((win, amount) -> keyOf(win))
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, totalPeriod))
				// clientIdDay:amount -> clientIdDay:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(totalPeriod),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}

	/**
	 * calculating total expenses for a period
	 * equivalent to windowedBy + aggregate
	 */
	private void periodExceedsWithTransformer(KStream<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		StoreBuilder<KeyValueStore<String, Integer>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore("periodTotalSpentStore"),
						Serdes.String(), Serdes.Integer());
		streamsBuilder.addStateStore(periodTotalSpentStore);

		transactions
				.flatTransform(new PeriodTotalExpensesAggregator(totalPeriod,
						periodTotalSpentStore.name()), periodTotalSpentStore.name())
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, totalPeriod))
				// clientIdDay:amount -> clientIdDay:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(totalPeriod),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}
}
