package ro.go.adrhc.springkafkastreams.streams;

import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;

import java.time.LocalDate;

public class DailyValueTransformerSupp extends CmdValueTransformerSupp<DailyTotalSpent> {
	public DailyValueTransformerSupp(String storeName) {
		super(storeName);
	}

	@Override
	protected DailyTotalSpent newT(String clientId, LocalDate time, Integer amount) {
		return new DailyTotalSpent(clientId, time, amount);
	}
}
