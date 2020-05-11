package ro.go.adrhc.springkafkastreams.streams.topologies.exceeds.period;

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
import ro.go.adrhc.springkafkastreams.helpers.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.topologies.exceeds.period.aggregators.DaysPeriodExpensesAggregator;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@Slf4j
public class PeriodExceedsWithTransformer extends AbstractPeriodExceeds {
	private final StreamsHelper streamsHelper;

	public PeriodExceedsWithTransformer(TopicsProperties topicsProperties, AppProperties appProperties, StreamsHelper streamsHelper) {
		super(topicsProperties, appProperties);
		this.streamsHelper = streamsHelper;
	}

	/**
	 * calculating total expenses for a days-period
	 * equivalent to windowedBy + aggregate
	 */
	public void accept(KStream<String, Transaction> transactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilder streamsBuilder) {
		StoreBuilder<KeyValueStore<String, Integer>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore(streamsHelper.periodTotalSpentByClientIdStoreName()),
						Serdes.String(), Serdes.Integer());
		streamsBuilder.addStateStore(periodTotalSpentStore);

		transactions
				.flatTransform(new DaysPeriodExpensesAggregator(appProperties.getWindowSize(),
						periodTotalSpentStore.name()), periodTotalSpentStore.name())
				// clientIdWindow:amount
				.peek((clientIdWindow, amount) -> printPeriodTotalExpenses(clientIdWindow, amount, appProperties.getWindowSize(), DAYS))
				// clientIdWindow:amount -> clientIdPeriod:PeriodTotalSpent
				.map(this::clientIdPeriodTotalSpentOf)
				// clientId:PeriodTotalSpent join clientId:ClientProfile -> clientId:PeriodExceeded
				.join(clientProfileTable,
						joinPeriodTotalSpentWithClientProfileOnClientId(appProperties.getWindowSize(), DAYS),
						periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(topicsProperties.getPeriodExceeds(), producePeriodExceeded());
	}
}
