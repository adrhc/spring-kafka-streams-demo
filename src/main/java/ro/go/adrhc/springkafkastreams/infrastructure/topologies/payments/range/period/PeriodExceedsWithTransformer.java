package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period;

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
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.aggregators.DaysPeriodExpensesAggregator;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.profiles.messages.ClientProfile;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@Slf4j
public class PeriodExceedsWithTransformer extends AbstractPeriodExceeds {
	public PeriodExceedsWithTransformer(TopicsProperties topicsProperties, AppProperties appProperties) {
		super(topicsProperties, appProperties);
	}

	/**
	 * calculating total expenses for a days-period
	 * equivalent to windowedBy + aggregate
	 */
	public void accept(KTable<String, ClientProfile> clientProfileTable,
			KStream<String, Transaction> transactions, StreamsBuilder streamsBuilder) {
		StoreBuilder<KeyValueStore<String, Integer>> periodTotalSpentStore =
				Stores.keyValueStoreBuilder(
						Stores.persistentKeyValueStore(periodTotalSpentByClientIdStoreName()),
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
						periodExceededJoiner(appProperties.getWindowSize(), DAYS),
						periodTotalSpentJoinClientProfile())
				// skip for less than periodMaxAmount
				.filter((clientId, periodExceeded) -> periodExceeded != null)
				// clientId:PeriodExceeded stream
				.to(topicsProperties.getPeriodExceeds(), producePeriodExceeded());
	}
}
