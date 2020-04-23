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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.KStreamEnh;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.*;
import ro.go.adrhc.springkafkastreams.transformers.aggregators.DaysPeriodExpensesAggregator;

import java.time.Duration;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.enhancer.KafkaEnh.enhance;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.periodTotalSpentByClientIdStoreName;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.joinPeriodTotalSpentWithClientProfileOnClientId;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.printPeriodTotalExpenses;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOf;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

/**
 * see https://issues.apache.org/jira/browse/KAFKA-6817
 * see https://stackoverflow.com/questions/49872827/unknownproduceridexception-in-kafka-streams-when-enabling-exactly-once
 * transactional.id.expiration.ms set by default to 7 days
 * see also log.retention.hours which defaults to 168h = 7 days
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@Profile("!test")
@Slf4j
public class PaymentsConfig {
	private final Environment env;
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper helper;

	public PaymentsConfig(Environment env, TopicsProperties properties, AppProperties app, StreamsHelper helper) {
		this.env = env;
		this.app = app;
		this.properties = properties;
		this.helper = helper;
	}

	@Bean
	public KStream<String, ?> transactions(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEnh streamsBuilder = enhance(pStreamsBuilder);

		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStreamEnh<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		processClientProfiles(clientProfileTable);

		// total expenses per day
		KGroupedStream<String, Transaction> trGroupedByCl = transactionsGroupedByClientId(transactions);
		dailyExceeds(trGroupedByCl, clientProfileTable, streamsBuilder);

		// total expenses for a period
		if (app.isKafkaEnhanced()) {
			periodExceedsWithEnhancer(transactions, clientProfileTable);
			report(periodTotalSpentByClientIdStoreName(
					app.getWindowSize(), app.getWindowUnit()), streamsBuilder);
		} else {
			periodExceeds(trGroupedByCl, clientProfileTable, streamsBuilder);
			report(streamsBuilder);
		}

//		periodExceedsWithTransformer(transactions, clientProfileTable, streamsBuilder);
//		reports(periodTotalSpentByClientIdStoreName(windowSize, windowUnit), streamsBuilder);

		return transactions;
	}

	private void processClientProfiles(KTable<String, ClientProfile> clientProfileTable) {
		clientProfileTable.toStream().foreach((clientId, profile) -> log.debug("\n\t{}", profile));
	}

	/**
	 * It needs the ktable stores of:
	 * KTable<String, Integer> dailyTotalSpentTable
	 * KTable<String, Integer> periodTotalSpentTable
	 */
	private void report(StreamsBuilderEnh streamsBuilder) {
		report(properties.getPeriodTotalSpent(), streamsBuilder);
	}

	private void report(String periodTotalSpentStoreName, StreamsBuilderEnh streamsBuilder) {
		KStream<String, Command> stream = helper.commandsStream(streamsBuilder);
		// daily report
		stream
				.filter((k, v) -> v.getParameters().contains("daily"))
				.transformValues(
						new DailyValueTransformerSupp(properties.getDailyTotalSpent()),
						properties.getDailyTotalSpent())
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(DailyTotalSpent::getTime));
					log.debug("\n\tDaily totals:\n\t{}", list.stream().map(it ->
							it.getClientId() + ", " + format(it.getTime()) +
									": " + it.getAmount() + " " + app.getCurrency())
							.collect(Collectors.joining("\n\t")));
				});
		// period report
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(periodTotalSpentStoreName),
						periodTotalSpentStoreName)
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(PeriodTotalSpent::getTime));
					log.debug("\n\t{} {} totals:\n\t{}", app.getWindowSize(), app.getWindowUnit(),
							list.stream().map(it -> it.getClientId() + ", " +
									format(it.getTime().minus(app.getWindowSize(), app.getWindowUnit()).plusDays(1))
									+ " - " + format(it.getTime()) + ": " +
									it.getAmount() + " " + app.getCurrency())
									.collect(Collectors.joining("\n\t")));
				});
		// configuration report
		stream
				.filter((k, v) -> v.getParameters().contains("config"))
				.foreach((k, v) -> log.debug("\n\tConfiguration:\n\tspring profiles = {}\n\tapp version = {}" +
								"\n\twindowSize = {}\n\twindowUnit = {}\n\tKafka enhancements = {}",
						env.getActiveProfiles(), app.getVersion(), app.getWindowSize(),
						app.getWindowUnit(), app.isKafkaEnhanced()));
	}

	/**
	 * group transactions by clientId
	 */
	private KGroupedStream<String, Transaction> transactionsGroupedByClientId(
			KStreamEnh<String, Transaction> transactions) {
		return transactions
				/*
				 * Wire Tap operator implementation
				 * see https://www.enterpriseintegrationpatterns.com/patterns/messaging/WireTap.html
				 * similar to peek() but also allow partially access to ProcessorContext
				 */
				.tap(it -> {
					log.trace("\n\ttopic: {}\n\ttimestamp: {}",
							it.context.topic(), localDateTimeOf(it.context.timestamp()));
					log.debug("\n\t{} spent {} {} on {}", it.key,
							it.value.getAmount(), app.getCurrency(), format(it.value.getTime()));
					it.context.headers().forEach(h -> log.trace(h.toString()));
				})
				.groupByKey(helper.transactionsGroupedByClientId());
	}

	/**
	 * calculating total expenses per day
	 * using Tumbling time window
	 */
	private void dailyExceeds(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilderEnh streamsBuilder) {
		groupedTransactions
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1))
						.grace(Duration.ofDays(app.getDailyGrace())))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId(app.getDailyGrace() + 1, 1, DAYS))
				// clientIdDay:amount
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
	 * using Hopping time window
	 */
	private void periodExceeds(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilderEnh streamsBuilder) {
		Duration windowDuration = Duration.of(app.getWindowSize(), app.getWindowUnit());
		groupedTransactions
/*
				// UnsupportedTemporalTypeException: Unit must not have an estimated duration
				.windowedBy(TimeWindows.of(Duration.of(1, MONTHS))...
*/
				// group by 3 days
				.windowedBy(TimeWindows.of(windowDuration)
						.advanceBy(Duration.ofDays(1)).grace(Duration.ofDays(app.getPeriodGrace())))
				// aggregate amount per clientId-period
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						helper.dailyTotalSpentByClientId(app.getPeriodGrace() + (int) windowDuration.toDays(),
								app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount
				.toStream((win, amount) -> keyOf(win))
				.to(properties.getPeriodTotalSpent(),
						helper.produceInteger("to-" + properties.getPeriodTotalSpent()));

		// not using through(properties.getPeriodTotalSpent()) because we later need the related store
		KTable<String, Integer> periodTotalSpentTable = helper.periodTotalSpentTable(streamsBuilder);

		periodTotalSpentTable
				.toStream()
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, app.getWindowSize(), DAYS))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(app.getWindowSize(), DAYS),
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
	private void periodExceedsWithEnhancer(KStreamEnh<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable) {
		transactions
				// group by e.g. 1 month
				.windowedBy(app.getWindowSize(), app.getWindowUnit())
				// aggregate amount per clientId-period
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						helper.periodTotalSpentByClientId(app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(
						clientIdPeriod, amount, app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(app.getWindowSize(), app.getWindowUnit()),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}

	/**
	 * calculating total expenses for a days-period
	 * equivalent to windowedBy + aggregate
	 */
	private void periodExceedsWithTransformer(KStream<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		StoreBuilder<KeyValueStore<String, Integer>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore(periodTotalSpentByClientIdStoreName(app.getWindowSize(), app.getWindowUnit())),
						Serdes.String(), Serdes.Integer());
		streamsBuilder.addStateStore(periodTotalSpentStore);

		transactions
				.flatTransform(new DaysPeriodExpensesAggregator(app.getWindowSize(),
						periodTotalSpentStore.name()), periodTotalSpentStore.name())
				// clientIdWindow:amount
				.peek((clientIdWindow, amount) -> printPeriodTotalExpenses(clientIdWindow, amount, app.getWindowSize(), DAYS))
				// clientIdWindow:amount -> clientIdPeriod:PeriodTotalSpent
				.map(PaymentsUtils::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(app.getWindowSize(), DAYS),
						helper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), helper.producePeriodExceeded());
	}
}
