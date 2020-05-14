package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.operators.aggregators.PeriodAggregator;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.Transaction;

import static java.time.temporal.ChronoUnit.MONTHS;

@Slf4j
public class MonthsPeriodExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public MonthsPeriodExpensesAggregator(int windowSize, String storeName) {
		super(windowSize, MONTHS, () -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount(),
				storeName);
	}
}
