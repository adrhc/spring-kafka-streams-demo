package ro.go.adrhc.springkafkastreams.core.services.reports;

import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodTotalSpent;

import java.util.List;

public interface PeriodTotalSpentService {
	void report(List<PeriodTotalSpent> list);
}
