package ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.topologies.payments.reports.transformers;

import ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.topologies.payments.exceeds.period.messages.PeriodTotalSpent;

import java.time.LocalDate;

public class PeriodValueTransformerSupp extends CmdValueTransformerSupp<PeriodTotalSpent> {
	public PeriodValueTransformerSupp(String storeName) {
		super(storeName);
	}

	@Override
	protected PeriodTotalSpent newT(String clientId, LocalDate time, Integer amount) {
		return new PeriodTotalSpent(clientId, time, amount);
	}
}
