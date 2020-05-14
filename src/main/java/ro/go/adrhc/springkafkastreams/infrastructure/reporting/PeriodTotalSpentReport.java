package ro.go.adrhc.springkafkastreams.infrastructure.reporting;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodTotalSpent;

import java.util.List;

public interface PeriodTotalSpentReport {
	void report(List<PeriodTotalSpent> list);
}
