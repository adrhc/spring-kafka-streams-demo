package ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.StreamsBuilderEx;
import ro.go.adrhc.springkafkastreams.infrastructure.kextensions.kstream.KStreamEx;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.ConfigReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.DailyTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.reporting.PeriodTotalSpentReport;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.PeriodExceedsWithExtensions;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.profiles.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports.messages.Command;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports.transformers.DailyValueTransformerSupp;
import ro.go.adrhc.springkafkastreams.infrastructure.topologies.reports.transformers.PeriodValueTransformerSupp;

import static ro.go.adrhc.springkafkastreams.infrastructure.kextensions.util.KafkaEx.enhance;

@Configuration
@Profile("!test")
@Slf4j
public class ReportsConfig {
	private final AppProperties app;
	private final TopicsProperties topicsProperties;
	private final DailyTotalSpentReport dailyTotalSpentReport;
	private final PeriodTotalSpentReport periodTotalSpentReport;
	private final PeriodExceedsWithExtensions periodExceedsWithExtensions;
	private final ConfigReport configReport;

	public ReportsConfig(AppProperties app, TopicsProperties topicsProperties, DailyTotalSpentReport dailyTotalSpentReport, PeriodTotalSpentReport periodTotalSpentReport, PeriodExceedsWithExtensions periodExceedsWithExtensions, ConfigReport configReport) {
		this.app = app;
		this.topicsProperties = topicsProperties;
		this.dailyTotalSpentReport = dailyTotalSpentReport;
		this.periodTotalSpentReport = periodTotalSpentReport;
		this.periodExceedsWithExtensions = periodExceedsWithExtensions;
		this.configReport = configReport;
	}

	/**
	 * It needs the KTable stores of:
	 * KTable<String, Integer> dailyTotalSpentTable
	 * KTable<String, Integer> periodTotalSpentTable
	 */
	@Bean
	public KStream<String, Command> reportingCommands(StreamsBuilder pStreamsBuilder) {
		String totalSpentStoreName = app.isKafkaEnhanced() ?
				periodExceedsWithExtensions.periodTotalSpentByClientIdStoreName() :
				topicsProperties.getPeriodTotalSpent();

		StreamsBuilderEx streamsBuilder = enhance(pStreamsBuilder);
		KStreamEx<String, Command> commands = commandsStream(streamsBuilder);

		// daily report
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("daily"))
				.transformValues(
						new DailyValueTransformerSupp(topicsProperties.getDailyTotalSpent()),
						topicsProperties.getDailyTotalSpent())
				.foreach((k, list) -> dailyTotalSpentReport.report(list));

		// period report
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("period"))
				.transformValues(
						new PeriodValueTransformerSupp(totalSpentStoreName),
						totalSpentStoreName)
				.foreach((k, list) -> periodTotalSpentReport.report(list));

		// configuration report
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("config"))
				.foreach((k, v) -> configReport.report());

		// clients profiles
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("profiles"))
				.<ClientProfile>allOf(topicsProperties.getClientProfiles())
				.foreach((k, profiles) -> profiles.forEach(profile -> log.debug("\n\t{}", profile)));

		return commands.getDelegate();
	}

	private KStreamEx<String, Command> commandsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.as(topicsProperties.getCommands()));
	}
}
