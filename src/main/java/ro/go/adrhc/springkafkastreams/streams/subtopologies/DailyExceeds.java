package ro.go.adrhc.springkafkastreams.streams.subtopologies;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.enhancer.StreamsBuilderEnh;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.Transaction;
import ro.go.adrhc.springkafkastreams.streams.PaymentsUtils;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.keyOf;

@Component
@Slf4j
public class DailyExceeds {
	private final TopicsProperties properties;
	private final AppProperties app;
	private final StreamsHelper helper;

	public DailyExceeds(TopicsProperties properties, AppProperties app, StreamsHelper helper) {
		this.properties = properties;
		this.app = app;
		this.helper = helper;
	}

	/**
	 * calculating total expenses per day
	 * using Tumbling time window
	 */
	public void accept(KGroupedStream<String, Transaction> groupedTransactions,
			KTable<String, ClientProfile> clientProfileTable, StreamsBuilderEnh streamsBuilder) {
		groupedTransactions
				// group by 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1))
						.grace(Duration.ofDays(app.getDailyGrace())))
				// aggregate amount per clientId-day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTotalSpentByClientId(app.getDailyGrace() + 1, 1, DAYS))
				// clientIdDay:amount
				.toStream((win, amount) -> keyOf(win))
				// save clientIdDay:amount into a compact stream (aka table)
				.to(properties.getDailyTotalSpent(),
						helper.produceInteger("to-" + properties.getDailyTotalSpent()));
//				.through(properties.getDailyTotalSpent(),
//						helper.produceInteger("to-" + properties.getDailyTotalSpent() + "-stream"))

		// not using through(properties.getDailyTotalSpent() because we later need the related store
		KTable<String, Integer> dailyTotalSpentTable = helper.dailyTotalSpentTable(streamsBuilder);

		dailyTotalSpentTable
				.toStream()
				// clientIdDay:amount -> clientId:DailyTotalSpent
				.map(PaymentsUtils::clientIdDailyTotalSpentOf)
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						PaymentsUtils::joinDailyTotalSpentWithClientProfileOnClientId,
						helper.dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// clientId:DailyExceeded streams
//				.foreach((clientId, dailyExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, dailyExceeded));
				.to(properties.getDailyExceeds(), helper.produceDailyExceeded());
	}
}