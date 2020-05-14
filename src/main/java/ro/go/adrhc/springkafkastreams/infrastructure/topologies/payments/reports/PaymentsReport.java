package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.ConfigReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.DailyTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.PeriodTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.messages.Command;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers.PeriodValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.reports.transformers.QueryAllSupp;

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
	public KStream<String, Command> apply(String totalSpentStoreName, StreamsBuilder streamsBuilder) {
		KStream<String, Command> stream = commandsStream(streamsBuilder);
		// daily report
		stream
				.filter((k, cmd) -> cmd.getParameters().contains("daily"))
				.transformValues(
						new DailyValueTransformerSupp(topicsProperties.getDailyTotalSpent()),
						topicsProperties.getDailyTotalSpent())
				.foreach((k, list) -> dailyTotalSpentReport.report(list));
		// period report
		stream
				.filter((k, cmd) -> cmd.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(totalSpentStoreName),
						totalSpentStoreName)
				.foreach((k, list) -> periodTotalSpentReport.report(list));
		// configuration report
		stream
				.filter((k, cmd) -> cmd.getParameters().contains("config"))
				.foreach((k, v) -> configReport.report());
		// clients profiles
		stream
				.filter((k, cmd) -> cmd.getParameters().contains("profiles"))
				.transformValues(
						new QueryAllSupp<ClientProfile>(topicsProperties.getClientProfiles()),
						topicsProperties.getClientProfiles())
				.foreach((k, profiles) -> profiles.forEach(profile -> log.debug("\n\t{}", profile)));
		return stream;
	}

	private KStream<String, Command> commandsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.as(topicsProperties.getCommands()));
	}
}
