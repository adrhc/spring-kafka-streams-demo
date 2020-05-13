package ro.go.adrhc.springkafkastreams.payments.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.core.services.reports.ConfigService;
import ro.go.adrhc.springkafkastreams.core.services.reports.DailyTotalSpentService;
import ro.go.adrhc.springkafkastreams.core.services.reports.PeriodTotalSpentService;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.KStreamEx;
import ro.go.adrhc.springkafkastreams.payments.reports.messages.Command;
import ro.go.adrhc.springkafkastreams.payments.reports.transformers.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.payments.reports.transformers.PeriodValueTransformerSupp;

@Component
@Slf4j
public class PaymentsReport {
	private final TopicsProperties topicsProperties;
	private final DailyTotalSpentService dailyTotalSpentService;
	private final PeriodTotalSpentService periodTotalSpentService;
	private final ConfigService configService;

	public PaymentsReport(TopicsProperties topicsProperties, DailyTotalSpentService dailyTotalSpentService, PeriodTotalSpentService periodTotalSpentService, ConfigService configService) {
		this.topicsProperties = topicsProperties;
		this.dailyTotalSpentService = dailyTotalSpentService;
		this.periodTotalSpentService = periodTotalSpentService;
		this.configService = configService;
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
				.foreach((k, list) -> dailyTotalSpentService.report(list));
		// period report
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(totalSpentStoreName),
						totalSpentStoreName)
				.foreach((k, list) -> periodTotalSpentService.report(list));
		// configuration report
		stream
				.filter((k, v) -> v.getParameters().contains("config"))
				.foreach((k, v) -> configService.report());
	}

	private KStreamEx<String, Command> commandsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.as(topicsProperties.getCommands()));
	}
}
