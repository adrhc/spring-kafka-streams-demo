package ro.go.adrhc.springkafkastreams.streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.Command;

@Configuration
@Profile("!test")
@Slf4j
public class ReportingConfig {
	private final TopicsProperties properties;

	public ReportingConfig(TopicsProperties properties) {this.properties = properties;}

	//	@Bean
//	@DependsOn("transactions")
	public KStream<String, ?> reports(StreamsBuilder streamsBuilder) {
		KStream<String, Command> stream = streamsBuilder.stream(properties.getCommand());
		stream.transformValues(new CmdValueTransformerSupp(properties),
				properties.getDailyTotalSpent(), properties.getPeriodTotalSpent());
		return stream;
	}
}
