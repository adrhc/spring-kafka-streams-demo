package ro.go.adrhc.springkafkastreams.infrastructure.reporting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.daily.messages.DailyTotalSpent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class DailyTotalSpentReportImpl implements DailyTotalSpentReport {
	private final AppProperties appProperties;

	public DailyTotalSpentReportImpl(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	@Override
	public void report(List<DailyTotalSpent> list) {
		list.sort(Comparator.comparing(DailyTotalSpent::getTime));
		log.debug("\n\tDaily totals:\n\t{}", list.stream().map(it ->
				it.getClientId() + ", " + format(it.getTime()) +
						": " + it.getAmount() + " " + appProperties.getCurrency())
				.collect(Collectors.joining("\n\t")));
	}
}
