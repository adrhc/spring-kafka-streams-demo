package ro.go.adrhc.springkafkastreams.payments.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.payments.exceeds.period.messages.PeriodTotalSpent;
import ro.go.adrhc.springkafkastreams.payments.messages.Command;
import ro.go.adrhc.springkafkastreams.payments.reports.processors.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.payments.reports.processors.PeriodValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.kextensions.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.kextensions.kstream.KStreamEx;

import java.util.Comparator;
import java.util.stream.Collectors;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Component
@Slf4j
public class PaymentsReport {
	private final Environment env;
	private final TopicsProperties topicsProperties;
	private final AppProperties appProperties;

	public PaymentsReport(Environment env, TopicsProperties topicsProperties, AppProperties appProperties) {
		this.env = env;
		this.topicsProperties = topicsProperties;
		this.appProperties = appProperties;
	}

	/**
	 * It needs the ktable stores of:
	 * KTable<String, Integer> dailyTotalSpentTable
	 * KTable<String, Integer> periodTotalSpentTable
	 */
	public void accept(String totalSpentStoreName, StreamsBuilderEx streamsBuilder) {
		KStream<String, Command> stream = commandsStream(streamsBuilder);
		// daily report
		stream
				.filter((k, v) -> v.getParameters().contains("daily"))
				.transformValues(
						new DailyValueTransformerSupp(topicsProperties.getDailyTotalSpent()),
						topicsProperties.getDailyTotalSpent())
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(DailyTotalSpent::getTime));
					log.debug("\n\tDaily totals:\n\t{}", list.stream().map(it ->
							it.getClientId() + ", " + format(it.getTime()) +
									": " + it.getAmount() + " " + appProperties.getCurrency())
							.collect(Collectors.joining("\n\t")));
				});
		// period report
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(totalSpentStoreName),
						totalSpentStoreName)
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(PeriodTotalSpent::getTime));
					log.debug("\n\t{} {} totals:\n\t{}", appProperties.getWindowSize(), appProperties.getWindowUnit(),
							list.stream().map(it -> it.getClientId() + ", " +
									format(it.getTime().minus(appProperties.getWindowSize(), appProperties.getWindowUnit()).plusDays(1))
									+ " - " + format(it.getTime()) + ": " +
									it.getAmount() + " " + appProperties.getCurrency())
									.collect(Collectors.joining("\n\t")));
				});
		// configuration report
		stream
				.filter((k, v) -> v.getParameters().contains("config"))
				.foreach((k, v) -> log.debug("\n\tConfiguration:\n\tspring profiles = {}\n\tapp version = {}" +
								"\n\twindowSize = {}\n\twindowUnit = {}\n\tKafka enhancements = {}",
						env.getActiveProfiles(), appProperties.getVersion(), appProperties.getWindowSize(),
						appProperties.getWindowUnit(), appProperties.isKafkaEnhanced()));
	}

	private KStreamEx<String, Command> commandsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.as(topicsProperties.getCommands()));
	}
}
