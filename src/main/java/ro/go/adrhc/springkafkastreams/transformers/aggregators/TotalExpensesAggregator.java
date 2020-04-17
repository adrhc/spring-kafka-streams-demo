package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.time.temporal.TemporalUnit;

import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class TotalExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public TotalExpensesAggregator(int period, TemporalUnit unit, String storeName) {
		super(period, storeName, kvi -> keyOf(kvi.getKey(),
				kvi.getValue().getTime().plus(kvi.getIteration(), unit)),
				() -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount());
	}
}
