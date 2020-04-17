package ro.go.adrhc.springkafkastreams.streams;

import ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent;

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
