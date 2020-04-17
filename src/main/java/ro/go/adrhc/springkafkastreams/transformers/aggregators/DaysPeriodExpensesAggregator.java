package ro.go.adrhc.springkafkastreams.transformers.aggregators;

import lombok.extern.slf4j.Slf4j;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Slf4j
public class DaysPeriodExpensesAggregator extends PeriodAggregator<String, Transaction, Integer> {
	public DaysPeriodExpensesAggregator(int period, String storeName) {
		super(period, (clientId, transaction, offset) ->
						keyOf(clientId, transaction.getTime().plus(offset, DAYS)),
				() -> 0,
				(clientId, transaction, amount) -> amount + transaction.getAmount(),
				storeName);
	}
}
