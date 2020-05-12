package ro.go.adrhc.springkafkastreams.streams.topologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.ksdsl.KStreamEnh;
import ro.go.adrhc.springkafkastreams.ksdsl.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.topologies.exceeds.DailyExceeds;
import ro.go.adrhc.springkafkastreams.streams.topologies.exceeds.period.PeriodExceeds;
import ro.go.adrhc.springkafkastreams.streams.topologies.exceeds.period.PeriodExceedsWithEnhancer;
import ro.go.adrhc.springkafkastreams.streams.topologies.reports.PaymentsReport;

import static ro.go.adrhc.springkafkastreams.ksdsl.KafkaEnh.enhance;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOf;

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
	private final AppProperties app;
	private final TopicsProperties topicsProperties;
	private final PaymentsReport paymentsReport;
	private final DailyExceeds dailyExceeds;
	private final PeriodExceeds periodExceeds;
	private final PeriodExceedsWithEnhancer periodExceedsWithEnhancer;

	public PaymentsConfig(AppProperties app, TopicsProperties topicsProperties, PaymentsReport paymentsReport, DailyExceeds dailyExceeds, PeriodExceeds periodExceeds, PeriodExceedsWithEnhancer periodExceedsWithEnhancer) {
		this.app = app;
		this.topicsProperties = topicsProperties;
		this.paymentsReport = paymentsReport;
		this.dailyExceeds = dailyExceeds;
		this.periodExceeds = periodExceeds;
		this.periodExceedsWithEnhancer = periodExceedsWithEnhancer;
	}

	@Bean
	public KStream<String, ?> transactions(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEnh streamsBuilder = enhance(pStreamsBuilder);

		KTable<String, ClientProfile> clientProfileTable = clientProfileTable(streamsBuilder);
		KStreamEnh<String, Transaction> transactions = transactionsStream(streamsBuilder);

		processClientProfiles(clientProfileTable);

		// total expenses per day
		KGroupedStream<String, Transaction> txGroupedByCli = txGroupedByClientId(transactions);
		dailyExceeds.accept(txGroupedByCli, clientProfileTable, streamsBuilder);

		// total expenses for a period
		if (app.isKafkaEnhanced()) {
			periodExceedsWithEnhancer.accept(transactions, clientProfileTable);
			paymentsReport.accept(periodExceedsWithEnhancer.periodTotalSpentByClientIdStoreName(), streamsBuilder);
		} else {
			periodExceeds.accept(txGroupedByCli, clientProfileTable, streamsBuilder);
			paymentsReport.accept(topicsProperties.getPeriodTotalSpent(), streamsBuilder);
		}

		return transactions;
	}

	/**
	 * group transactions by clientId
	 */
	private KGroupedStream<String, Transaction> txGroupedByClientId(
			KStreamEnh<String, Transaction> transactions) {
		return transactions
				.peek(it -> {
					log.trace("\n\ttopic: {}\n\ttimestamp: {}",
							it.context.topic(), localDateTimeOf(it.context.timestamp()));
					log.debug("\n\t{} spent {} {} on {}", it.key,
							it.value.getAmount(), app.getCurrency(), format(it.value.getTime()));
					it.context.headers().forEach(h -> log.trace(h.toString()));
				})
				.groupByKey(transactionsGroupedByClientId());
	}

	private void processClientProfiles(KTable<String, ClientProfile> clientProfileTable) {
		clientProfileTable.toStream().foreach((clientId, profile) -> log.debug("\n\t{}", profile));
	}

	private KTable<String, ClientProfile> clientProfileTable(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getClientProfiles(),
				Consumed.as(topicsProperties.getClientProfiles()),
				Materialized.as(topicsProperties.getClientProfiles()));
	}

	private KStreamEnh<String, Transaction> transactionsStream(StreamsBuilderEnh streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getTransactions(),
				Consumed.as(topicsProperties.getTransactions()));
	}

	private Grouped<String, Transaction> transactionsGroupedByClientId() {
		return Grouped.as("transactionsGroupedByClientId");
	}
}
