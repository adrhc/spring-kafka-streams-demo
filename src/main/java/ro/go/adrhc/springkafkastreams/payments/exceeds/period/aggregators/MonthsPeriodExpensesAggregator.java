package ro.go.adrhc.springkafkastreams.payments.exceeds.period.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.payments.messages.Transaction;
import ro.go.adrhc.springkafkastreams.kenhancements.kstream.operators.aggregators.PeriodAggregator;

import static java.time.temporal.ChronoUnit.MONTHS;

@Slf4j
public class MonthsPeriodExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public MonthsPeriodExpensesAggregator(int windowSize, String storeName) {
		super(windowSize, MONTHS, () -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount(),
				storeName);
	}
}
