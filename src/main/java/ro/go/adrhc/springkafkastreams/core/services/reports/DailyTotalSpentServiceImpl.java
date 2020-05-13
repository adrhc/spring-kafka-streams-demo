package ro.go.adrhc.springkafkastreams.core.services.reports;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Service
@Slf4j
public class DailyTotalSpentServiceImpl implements DailyTotalSpentService {
	private final AppProperties appProperties;

	public DailyTotalSpentServiceImpl(AppProperties appProperties) {
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
