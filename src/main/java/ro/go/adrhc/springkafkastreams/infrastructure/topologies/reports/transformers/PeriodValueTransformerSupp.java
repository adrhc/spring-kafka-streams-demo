package ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports.transformers;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodTotalSpent;

import java.time.LocalDate;

public class PeriodValueTransformerSupp extends AbstractTotalSpentValueTransformerSupp<PeriodTotalSpent> {
	public PeriodValueTransformerSupp(String storeName) {
		super(storeName);
	}

	@Override
	protected PeriodTotalSpent newT(String clientId, LocalDate time, Integer amount) {
		return new PeriodTotalSpent(clientId, time, amount);
	}
}
