package ro.go.adrhc.springkafkastreams.streams;

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
import ro.go.adrhc.springkafkastreams.messages.*;
import ro.go.adrhc.springkafkastreams.transformers.aggregators.DaysPeriodExpensesAggregator;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.enhancer.KafkaEnhancer.enhance;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.joinPeriodTotalSpentWithClientProfileOnClientId;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.printPeriodTotalExpenses;
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
	private final int windowSize;
	private final ChronoUnit windowUnit;
	private final TopicsProperties properties;
	private final StreamsHelper helper;

	public PaymentsConfig(@Value("${window.size}") int windowSize,
			@Value("${window.unit}") ChronoUnit windowUnit, TopicsProperties properties, StreamsHelper helper) {
		this.windowSize = windowSize;
		this.windowUnit = windowUnit;
		this.properties = properties;
		this.helper = helper;
	}

	@Bean
	public KStream<String, ?> transactions(StreamsBuilder streamsBuilder) {
		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStream<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		clientProfile(clientProfileTable);
		KGroupedStream<String, Transaction> grouped = transactionsGroupedByClientId(transactions);
		dailyExceeds(grouped, clientProfileTable, streamsBuilder); // total expenses per day
		periodExceeds(grouped, clientProfileTable, streamsBuilder); // total expenses for a period
//		periodExceedsWithTransformer(transactions, clientProfileTable, streamsBuilder);
//		periodExceedsWithEnhancer(transactions, clientProfileTable, streamsBuilder);
		reports(streamsBuilder);

		return transactions;
	}

	private void clientProfile(KTable<String, ClientProfile> clientProfileTable) {
		clientProfileTable.toStream().foreach((clientId, profile) -> log.debug("\n\t{}", profile));
	}

	/**
	 * It needs the ktable stores of:
	 * KTable<String, Integer> dailyTotalSpentTable
	 * KTable<String, Integer> periodTotalSpentTable
	 */
	private void reports(StreamsBuilder streamsBuilder) {
		KStream<String, Command> stream = streamsBuilder.stream(properties.getCommand());
		stream
				.filter((k, v) -> v.getParameters().contains("daily"))
				.transformValues(new DailyValueTransformerSupp(properties.getDailyTotalSpent()),
						properties.getDailyTotalSpent(), properties.getPeriodTotalSpent())
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(DailyTotalSpent::getTime));
					log.debug("\n\tDaily totals:\n\t{}", list.stream().map(it ->
							it.getClientId() + ", " + format(it.getTime()) + ": " + it.getAmount())
							.collect(Collectors.joining("\n\t")));
				});
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(new PeriodValueTransformerSupp(properties.getPeriodTotalSpent()),
						properties.getDailyTotalSpent(), properties.getPeriodTotalSpent())
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(PeriodTotalSpent::getTime));
					log.debug("\n\t{} {} totals:\n\t{}", windowSize, windowUnit,
							list.stream().map(it -> it.getClientId() + ", " +
									format(it.getTime().minus(windowSize, windowUnit).plusDays(1))
									+ " - " + format(it.getTime()) + ": " + it.getAmount())
									.collect(Collectors.joining("\n\t")));
				});
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
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		groupedTransactions
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId(DELAY + 1, "1day"))
				// clientId-yyyy.MM.dd:amount
				.toStream((win, amount) -> keyOf(win))
				// save clientIdDay:amount into a compact stream (aka table)
				.to(properties.getDailyTotalSpent(),
						helper.produceInteger("to-" + properties.getDailyTotalSpent()));
//				.through(properties.getDailyTotalSpent(),
//						helper.produceInteger("to-" + properties.getDailyTotalSpent() + "-stream"))

		// not using through(properties.getDailyTotalSpent() because we later need the related store
		KTable<String, Integer> dailyTotalSpentTable = helper.dailyTotalSpentTable(streamsBuilder);

		dailyTotalSpentTable
				.toStream()
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(PaymentsUtils::clientIdDailyTotalSpentOf)
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						PaymentsUtils::joinDailyTotalSpentWithClientProfileOnClientId,
						helper.dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// clientId:DailyExceeded streams
//				.foreach((clientId, dailyExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, dailyExceeded));
				.to(properties.getDailyExceeds(), helper.produceDailyExceeded());
	}

	/**
	 * calculating total expenses for a period
	 */
	private void periodExceeds(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		groupedTransactions
				// UnsupportedTemporalTypeException: Unit must not have an estimated duration
/*
				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS))
						.advanceBy(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
*/
				// group by 3 days
				.windowedBy(TimeWindows.of(Duration.of(windowSize, windowUnit))
						.advanceBy(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-3-days
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						helper.dailyTotalSpentByClientId(DELAY + windowSize, "3days"))
				// clientId-yyyy.MM.dd:amount
				.toStream((win, amount) -> keyOf(win))
				.to(properties.getPeriodTotalSpent(),
						helper.produceInteger("to-" + properties.getPeriodTotalSpent()));

		// not using through(properties.getPeriodTotalSpent() because we later need the related store
		KTable<String, Integer> periodTotalSpentTable = helper.periodTotalSpentTable(streamsBuilder);

		periodTotalSpentTable
				.toStream()
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, windowSize, DAYS))
				// clientIdDay:amount -> clientIdDay:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(windowSize, DAYS),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
//				.foreach((clientId, periodExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, periodExceeded));
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}

	/**
	 * calculating total expenses for a period
	 * equivalent to windowedBy + aggregate
	 */
	private void periodExceedsWithEnhancer(KStream<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		enhance(streamsBuilder)
				.stream(transactions)
				.windowedBy(windowSize, windowUnit)
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						helper.periodTotalSpentByClientId(windowSize + windowUnit.toString()))
				// clientIdPeriod:amount (i.e. clientIdDay:amount)
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, windowSize, windowUnit))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(windowSize, windowUnit),
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
				.flatTransform(new DaysPeriodExpensesAggregator(windowSize,
						periodTotalSpentStore.name()), periodTotalSpentStore.name())
				// clientIdWindow:amount
				.peek((clientIdWindow, amount) -> printPeriodTotalExpenses(clientIdWindow, amount, windowSize, DAYS))
				// clientIdWindow:amount -> clientIdPeriod:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(windowSize, DAYS),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}
}
