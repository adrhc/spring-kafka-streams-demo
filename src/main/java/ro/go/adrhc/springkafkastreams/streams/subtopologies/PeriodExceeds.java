package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.helper.PaymentsHelper;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.util.streams.LocalDateBasedKey.keyOf;

@Component
@Slf4j
public class PeriodExceeds {
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper streamsHelper;
	private final PaymentsHelper paymentsHelper;

	public PeriodExceeds(TopicsProperties properties, AppProperties app, StreamsHelper streamsHelper, PaymentsHelper paymentsHelper) {
		this.properties = properties;
		this.app = app;
		this.streamsHelper = streamsHelper;
		this.paymentsHelper = paymentsHelper;
	}

	/**
	 * calculating total expenses for a period
	 * using Hopping time window
	 */
	public void accept(KGroupedStream<String, Transaction> groupedTransactions,
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
						streamsHelper.dailyTotalSpentByClientId(app.getPeriodGrace() + (int) windowDuration.toDays(),
								app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount
				.toStream((win, amount) -> keyOf(win))
				.to(properties.getPeriodTotalSpent(),
						streamsHelper.produceInteger("to-" + properties.getPeriodTotalSpent()));

		// not using through(properties.getPeriodTotalSpent()) because we later need the related store
		KTable<String, Integer> periodTotalSpentTable = streamsHelper.periodTotalSpentTable(streamsBuilder);

		periodTotalSpentTable
				.toStream()
				.peek((clientIdPeriod, amount) -> paymentsHelper.printPeriodTotalExpenses(clientIdPeriod, amount, app.getWindowSize(), DAYS))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(paymentsHelper::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						paymentsHelper.joinPeriodTotalSpentWithClientProfileOnClientId(app.getWindowSize(), DAYS),
						streamsHelper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
//				.foreach((clientId, periodExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, periodExceeded));
				.to(properties.getPeriodExceeds(), streamsHelper.producePeriodExceeded());
	}
}
