package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.DailyExceeds;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.PeriodExceeds;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.PeriodExceedsWithExtensions;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.profiles.messages.ClientProfile;

import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.streams.StreamsBuilderEx.enhance;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeOf;

/**
 * see https://issues.apache.org/jira/browse/KAFKA-6817
 * see https://stackoverflow.com/questions/49872827/unknownproduceridexception-in-kafka-streams-when-enabling-exactly-once
 * transactional.id.expiration.ms set by default to 7 days
 * see also log.retention.hours which defaults to 168h = 7 days
 */
@Configuration
@Profile("!test")
@Slf4j
public class ExceedsConfig {
	private final AppProperties app;
	private final TopicsProperties topicsProperties;
	private final DailyExceeds dailyExceeds;
	private final PeriodExceeds periodExceeds;
	private final PeriodExceedsWithExtensions periodExceedsWithExtensions;

	public ExceedsConfig(AppProperties app, TopicsProperties topicsProperties, DailyExceeds dailyExceeds, PeriodExceeds periodExceeds, PeriodExceedsWithExtensions periodExceedsWithExtensions) {
		this.app = app;
		this.topicsProperties = topicsProperties;
		this.dailyExceeds = dailyExceeds;
		this.periodExceeds = periodExceeds;
		this.periodExceedsWithExtensions = periodExceedsWithExtensions;
	}

	@Bean
	public KStream<String, Transaction> transactions(
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEx streamsBuilder = enhance(pStreamsBuilder);
		KStreamEx<String, Transaction> transactions = transactionsStream(streamsBuilder);

		// total expenses per day
		KGroupedStream<String, Transaction> txGroupedByCli = txGroupedByClientId(transactions);
		dailyExceeds.accept(clientProfileTable, txGroupedByCli, streamsBuilder);

		// total expenses for a period
		if (app.isKafkaEnhanced()) {
			periodExceedsWithExtensions.accept(clientProfileTable, transactions);
		} else {
			periodExceeds.accept(clientProfileTable, txGroupedByCli, streamsBuilder);
		}

		return transactions;
	}

	private KStreamEx<String, Transaction> transactionsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getTransactions(),
				Consumed.as(topicsProperties.getTransactions()));
	}

	/**
	 * group transactions by clientId
	 */
	private KGroupedStream<String, Transaction> txGroupedByClientId(
			KStreamEx<String, Transaction> transactions) {
		return transactions
				.peek(it -> {
					log.trace("\n\ttopic: {}\n\ttimestamp: {}",
							it.context.topic(), localDateTimeOf(it.context.timestamp()));
					log.debug("\n\t{} spent {} {} on {}", it.key,
							it.value.getAmount(), app.getCurrency(), format(it.value.getTime()));
					it.context.headers().forEach(h -> log.trace(h.toString()));
				})
				.groupByKey(Grouped.as("transactionsGroupedByClientId"));
	}
}
