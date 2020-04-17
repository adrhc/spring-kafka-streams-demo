package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import static java.time.temporal.ChronoUnit.MONTHS;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class MonthsPeriodExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public MonthsPeriodExpensesAggregator(int period, String storeName) {
		super(period, storeName, kvi -> keyOf(kvi.getKey(),
				kvi.getValue().getTime().plus(kvi.getIteration(), MONTHS)),
				() -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount());
	}
}
