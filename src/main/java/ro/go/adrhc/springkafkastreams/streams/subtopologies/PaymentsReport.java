package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.Command;
import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent;
import ro.go.adrhc.springkafkastreams.streams.processors.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.streams.processors.PeriodValueTransformerSupp;

import java.util.Comparator;
import java.util.stream.Collectors;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

@Component
@Slf4j
public class PaymentsReport {
	private final Environment env;
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper helper;

	public PaymentsReport(Environment env, TopicsProperties properties, AppProperties app, StreamsHelper helper) {
		this.env = env;
		this.properties = properties;
		this.app = app;
		this.helper = helper;
	}

	/**
	 * It needs the ktable stores of:
	 * KTable<String, Integer> dailyTotalSpentTable
	 * KTable<String, Integer> periodTotalSpentTable
	 */
	public void accept(StreamsBuilderEnh streamsBuilder) {
		accept(properties.getPeriodTotalSpent(), streamsBuilder);
	}

	public void accept(String periodTotalSpentStoreName, StreamsBuilderEnh streamsBuilder) {
		KStream<String, Command> stream = helper.commandsStream(streamsBuilder);
		// daily report
		stream
				.filter((k, v) -> v.getParameters().contains("daily"))
				.transformValues(
						new DailyValueTransformerSupp(properties.getDailyTotalSpent()),
						properties.getDailyTotalSpent())
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(DailyTotalSpent::getTime));
					log.debug("\n\tDaily totals:\n\t{}", list.stream().map(it ->
							it.getClientId() + ", " + format(it.getTime()) +
									": " + it.getAmount() + " " + app.getCurrency())
							.collect(Collectors.joining("\n\t")));
				});
		// period report
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(periodTotalSpentStoreName),
						periodTotalSpentStoreName)
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(PeriodTotalSpent::getTime));
					log.debug("\n\t{} {} totals:\n\t{}", app.getWindowSize(), app.getWindowUnit(),
							list.stream().map(it -> it.getClientId() + ", " +
									format(it.getTime().minus(app.getWindowSize(), app.getWindowUnit()).plusDays(1))
									+ " - " + format(it.getTime()) + ": " +
									it.getAmount() + " " + app.getCurrency())
									.collect(Collectors.joining("\n\t")));
				});
		// configuration report
		stream
				.filter((k, v) -> v.getParameters().contains("config"))
				.foreach((k, v) -> log.debug("\n\tConfiguration:\n\tspring profiles = {}\n\tapp version = {}" +
								"\n\twindowSize = {}\n\twindowUnit = {}\n\tKafka enhancements = {}",
						env.getActiveProfiles(), app.getVersion(), app.getWindowSize(),
						app.getWindowUnit(), app.isKafkaEnhanced()));
	}
}
