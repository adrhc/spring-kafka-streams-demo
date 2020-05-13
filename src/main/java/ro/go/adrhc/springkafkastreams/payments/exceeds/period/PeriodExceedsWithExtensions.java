package ro.go.adrhc.springkafkastreams.payments.exceeds.period;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.payments.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.payments.messages.Transaction;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.KStreamEx;

@Component
@Slf4j
public class PeriodExceedsWithExtensions extends AbstractPeriodExceeds {
	public PeriodExceedsWithExtensions(TopicsProperties topicsProperties, AppProperties appProperties) {
		super(topicsProperties, appProperties);
	}

	/**
	 * calculating total expenses for a period
	 * equivalent to windowedBy + aggregate
	 */
	public void accept(KStreamEx<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable) {
		transactions
				// group by e.g. 1 month
				.windowedBy(appProperties.getWindowSize(), appProperties.getWindowUnit())
				// aggregate amount per clientId-period
				.aggregate(() -> 0, (clientId, transaction, sum) -> sum + transaction.getAmount(),
						periodTotalSpentByClientId())
				// clientIdPeriod:amount
				.peek((clientIdPeriod, amount) -> printPeriodTotalExpenses(
						clientIdPeriod, amount, appProperties.getWindowSize(), appProperties.getWindowUnit()))
				// clientIdPeriod:amount -> clientIdPeriod:PeriodTotalSpent
				.map(this::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(
								appProperties.getWindowSize(), appProperties.getWindowUnit()),
						periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(topicsProperties.getPeriodExceeds(), producePeriodExceeded());
	}

	private Materialized<String, Integer, KeyValueStore<String, Integer>>
	periodTotalSpentByClientId() {
		return Materialized.<String, Integer, KeyValueStore<String, Integer>>
				as(periodTotalSpentByClientIdStoreName())
				.withValueSerde(Serdes.Integer());
	}
}
