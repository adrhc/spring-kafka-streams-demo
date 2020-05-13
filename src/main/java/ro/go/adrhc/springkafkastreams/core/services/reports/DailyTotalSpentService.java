package ro.go.adrhc.springkafkastreams.core.services.reports;

import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyTotalSpent;

import java.util.List;

public interface DailyTotalSpentService {
	void report(List<DailyTotalSpent> list);
}
