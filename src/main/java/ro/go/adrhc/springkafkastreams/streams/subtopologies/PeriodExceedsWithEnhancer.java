package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.KStreamEnh;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.PaymentsUtils;

import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.joinPeriodTotalSpentWithClientProfileOnClientId;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.printPeriodTotalExpenses;

@Component
@Slf4j
public class PeriodExceedsWithEnhancer {
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper helper;

	public PeriodExceedsWithEnhancer(TopicsProperties properties, AppProperties app, StreamsHelper helper) {
		this.properties = properties;
		this.app = app;
		this.helper = helper;
	}

	/**
	 * calculating total expenses for a period
	 * equivalent to windowedBy + aggregate
	 */
	public void accept(KStreamEnh<String, Transaction> transactions,
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
}
