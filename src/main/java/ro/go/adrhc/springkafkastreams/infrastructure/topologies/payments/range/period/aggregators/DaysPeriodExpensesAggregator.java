package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.operators.aggregators.PeriodAggregator;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
public class DaysPeriodExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public DaysPeriodExpensesAggregator(int windowSize, String storeName) {
		super(windowSize, DAYS, () -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount(),
				storeName);
	}
}
