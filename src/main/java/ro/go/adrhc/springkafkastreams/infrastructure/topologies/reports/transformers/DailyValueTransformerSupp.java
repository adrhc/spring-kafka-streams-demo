package ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports.transformers;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyTotalSpent;

import java.time.LocalDate;

public class DailyValueTransformerSupp extends AbstractTotalSpentValueTransformerSupp<DailyTotalSpent> {
	public DailyValueTransformerSupp(String storeName) {
		super(storeName);
	}

	@Override
	protected DailyTotalSpent newT(String clientId, LocalDate time, Integer amount) {
		return new DailyTotalSpent(clientId, time, amount);
	}
}
