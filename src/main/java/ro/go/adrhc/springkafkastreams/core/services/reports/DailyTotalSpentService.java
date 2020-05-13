package ro.go.adrhc.springkafkastreams.core.services.reports;

import ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.topologies.payments.exceeds.daily.messages.DailyTotalSpent;

import java.util.List;

public interface DailyTotalSpentService {
	void report(List<DailyTotalSpent> list);
}
