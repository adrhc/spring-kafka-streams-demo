package ro.go.adrhc.springkafkastreams.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.model.Transaction;
import ro.go.adrhc.springkafkastreams.transformers.debug.PeriodTotalExpensesAggregator;

import java.time.Duration;

import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;
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
	public KStream<String, ?> payments(StreamsBuilder streamsBuilder) {
		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		// monthlyTotalSpentTable needed only for its registered store
		KTable<String, Integer> monthlyTotalSpentTable = helper.periodTotalExpensesTable(streamsBuilder);
		KStream<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		KStream<String, DailyTotalSpent> dailyTotalSpent = transactions
				.peek((clientId, transaction) -> log.debug("\n\t{} spent {} GBP on {}", clientId,
						transaction.getAmount(), format(transaction.getTime())))
//				.transform(new TransformerDebugger<>())
//				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(helper.transactionsGroupedByClientId())
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId())
				// keyOf(win) -> clientId-yyyy.MM.dd
				.toStream((win, amount) -> keyOf(win))
				// save clientIdDay:amount into a compact stream (aka table)
				.through(properties.getDailyTotalSpent(),
						helper.produceInteger(properties.getDailyTotalSpent()))
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(PaymentsUtils::clientIdDailyTotalSpentOf);

		// calculating total expenses per day
		dailyTotalSpent
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable, PaymentsUtils::joinDailyTotalSpentWithClientProfileOnClientId,
						helper.dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				.to(properties.getDailyExceeds(), helper.produceDailyExceeded());

		// calculating total expenses for period
		dailyTotalSpent
				.flatTransform(new PeriodTotalExpensesAggregator(totalPeriod, properties),
						properties.getPeriodTotalExpenses())
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(clientIdPeriod, amount, totalPeriod))
				.to(properties.getPeriodTotalExpenses(), Produced.with(Serdes.String(), Serdes.Integer()));

		return transactions;
	}
}
