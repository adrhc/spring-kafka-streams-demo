package ro.go.adrhc.springkafkastreams.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.enhancer.KStreamEnh;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.subtopologies.DailyExceeds;
import ro.go.adrhc.springkafkastreams.streams.subtopologies.PaymentsReport;
import ro.go.adrhc.springkafkastreams.streams.subtopologies.PeriodExceeds;
import ro.go.adrhc.springkafkastreams.streams.subtopologies.PeriodExceedsWithEnhancer;

import static ro.go.adrhc.springkafkastreams.enhancer.KafkaEnh.enhance;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.periodTotalSpentByClientIdStoreName;
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
	private final StreamsHelper helper;
	private final PaymentsReport paymentsReport;
	private final DailyExceeds dailyExceeds;
	private final PeriodExceeds periodExceeds;
	private final PeriodExceedsWithEnhancer periodExceedsWithEnhancer;

	public PaymentsConfig(AppProperties app, StreamsHelper helper, PaymentsReport paymentsReport, DailyExceeds dailyExceeds, PeriodExceeds periodExceeds, PeriodExceedsWithEnhancer periodExceedsWithEnhancer) {
		this.app = app;
		this.helper = helper;
		this.paymentsReport = paymentsReport;
		this.dailyExceeds = dailyExceeds;
		this.periodExceeds = periodExceeds;
		this.periodExceedsWithEnhancer = periodExceedsWithEnhancer;
	}

	@Bean
	public KStream<String, ?> transactions(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEnh streamsBuilder = enhance(pStreamsBuilder);

		KTable<String, ClientProfile> clientProfileTable = helper.clientProfileTable(streamsBuilder);
		KStreamEnh<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		processClientProfiles(clientProfileTable);

		// total expenses per day
		KGroupedStream<String, Transaction> txGroupedByCli = txGroupedByClientId(transactions);
		dailyExceeds.accept(txGroupedByCli, clientProfileTable, streamsBuilder);

		// total expenses for a period
		if (app.isKafkaEnhanced()) {
			periodExceedsWithEnhancer.accept(transactions, clientProfileTable);
			paymentsReport.accept(periodTotalSpentByClientIdStoreName(
					app.getWindowSize(), app.getWindowUnit()), streamsBuilder);
		} else {
			periodExceeds.accept(txGroupedByCli, clientProfileTable, streamsBuilder);
			paymentsReport.accept(streamsBuilder);
		}

		return transactions;
	}

	private void processClientProfiles(KTable<String, ClientProfile> clientProfileTable) {
		clientProfileTable.toStream().foreach((clientId, profile) -> log.debug("\n\t{}", profile));
	}

	/**
	 * group transactions by clientId
	 */
	private KGroupedStream<String, Transaction> txGroupedByClientId(
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

}
