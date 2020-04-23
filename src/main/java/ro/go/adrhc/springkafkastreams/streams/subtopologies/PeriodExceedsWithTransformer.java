package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.PaymentsUtils;
import ro.go.adrhc.springkafkastreams.transformers.aggregators.DaysPeriodExpensesAggregator;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.periodTotalSpentByClientIdStoreName;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.joinPeriodTotalSpentWithClientProfileOnClientId;
import static ro.go.adrhc.springkafkastreams.streams.PaymentsUtils.printPeriodTotalExpenses;

@Component
@Slf4j
public class PeriodExceedsWithTransformer {
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper helper;

	public PeriodExceedsWithTransformer(TopicsProperties properties, AppProperties app, StreamsHelper helper) {
		this.properties = properties;
		this.app = app;
		this.helper = helper;
	}

	/**
	 * calculating total expenses for a days-period
	 * equivalent to windowedBy + aggregate
	 */
	public void accept(KStream<String, Transaction> transactions,
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
