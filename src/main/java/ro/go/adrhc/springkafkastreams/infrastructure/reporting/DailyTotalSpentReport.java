package ro.go.adrhc.springkafkastreams.infrastructure.reporting;

import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyTotalSpent;

import java.util.List;

public interface DailyTotalSpentReport {
	void report(List<DailyTotalSpent> list);
}
