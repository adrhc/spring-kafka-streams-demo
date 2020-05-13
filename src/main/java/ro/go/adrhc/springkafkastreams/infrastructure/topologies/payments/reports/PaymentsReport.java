package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.ConfigReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.DailyTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.PeriodTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.KStreamEx;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.messages.Command;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers.PeriodValueTransformerSupp;

@Component
@Slf4j
public class PaymentsReport {
	private final TopicsProperties topicsProperties;
	private final DailyTotalSpentReport dailyTotalSpentReport;
	private final PeriodTotalSpentReport periodTotalSpentReport;
	private final ConfigReport configReport;

	public PaymentsReport(TopicsProperties topicsProperties, DailyTotalSpentReport dailyTotalSpentReport, PeriodTotalSpentReport periodTotalSpentReport, ConfigReport configReport) {
		this.topicsProperties = topicsProperties;
		this.dailyTotalSpentReport = dailyTotalSpentReport;
		this.periodTotalSpentReport = periodTotalSpentReport;
		this.configReport = configReport;
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
				.foreach((k, list) -> dailyTotalSpentReport.report(list));
		// period report
		stream
				.filter((k, v) -> v.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(totalSpentStoreName),
						totalSpentStoreName)
				.foreach((k, list) -> periodTotalSpentReport.report(list));
		// configuration report
		stream
				.filter((k, v) -> v.getParameters().contains("config"))
				.foreach((k, v) -> configReport.report());
	}

	private KStreamEx<String, Command> commandsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.as(topicsProperties.getCommands()));
	}
}
