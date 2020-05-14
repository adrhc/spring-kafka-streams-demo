package ro.go.adrhc.springkafkastreams.infrastructure.reporting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodTotalSpent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class PeriodTotalSpentReportImpl implements PeriodTotalSpentReport {
	private final AppProperties appProperties;

	public PeriodTotalSpentReportImpl(AppProperties appProperties) {
		this.appProperties = appProperties;
	}

	@Override
	public void report(List<PeriodTotalSpent> list) {
		list.sort(Comparator.comparing(PeriodTotalSpent::getTime));
		log.debug("\n\t{} {} totals:\n\t{}", appProperties.getWindowSize(), appProperties.getWindowUnit(),
				list.stream().map(it -> it.getClientId() + ", " +
						format(it.getTime().minus(appProperties.getWindowSize(), appProperties.getWindowUnit()).plusDays(1))
						+ " - " + format(it.getTime()) + ": " +
						it.getAmount() + " " + appProperties.getCurrency())
						.collect(Collectors.joining("\n\t")));
	}
}
