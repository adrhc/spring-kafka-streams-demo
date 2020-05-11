package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.KStreamEnh;
import ro.go.adrhc.springkafkastreams.helper.PaymentsHelper;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

@Component
@Slf4j
public class PeriodExceedsWithEnhancer {
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper streamsHelper;
	private final PaymentsHelper paymentsHelper;

	public PeriodExceedsWithEnhancer(TopicsProperties properties, AppProperties app, StreamsHelper streamsHelper, PaymentsHelper paymentsHelper) {
		this.properties = properties;
		this.app = app;
		this.streamsHelper = streamsHelper;
		this.paymentsHelper = paymentsHelper;
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
						streamsHelper.periodTotalSpentByClientId(app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount
				.peek((clientIdPeriod, amount) -> paymentsHelper.printPeriodTotalExpenses(
						clientIdPeriod, amount, app.getWindowSize(), app.getWindowUnit()))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(paymentsHelper::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						paymentsHelper.joinPeriodTotalSpentWithClientProfileOnClientId(app.getWindowSize(), app.getWindowUnit()),
						streamsHelper.periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(properties.getPeriodExceeds(), streamsHelper.producePeriodExceeded());
	}
}
