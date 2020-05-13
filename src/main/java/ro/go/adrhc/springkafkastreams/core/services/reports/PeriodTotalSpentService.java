package ro.go.adrhc.springkafkastreams.core.services.reports;

import ro.go.adrhc.springkafkastreams.infrastructure.adapters.inbound.topologies.payments.exceeds.period.messages.PeriodTotalSpent;

import java.util.List;

public interface PeriodTotalSpentService {
	void report(List<PeriodTotalSpent> list);
}
