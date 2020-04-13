package ro.go.adrhc.springkafkastreams.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.helper.StreamsHelper;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyExpenses;
import ro.go.adrhc.springkafkastreams.model.OverdueDailyExpenses;
import ro.go.adrhc.springkafkastreams.util.WindowUtils;

import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;

/**
 * see https://issues.apache.org/jira/browse/KAFKA-6817
 * see https://stackoverflow.com/questions/49872827/unknownproduceridexception-in-kafka-streams-when-enabling-exactly-once
 * transactional.id.expiration.ms set by default to 7 days
 */
@Configuration
@EnableKafka
@EnableKafkaStreams
@Profile("!test")
@Slf4j
public class KafkaStreamsConfig {
	private final TopicsProperties properties;
	private final StreamsHelper helper;
	private final JsonSerde<ClientProfile> clientProfileSerde;
	private final JsonSerde<DailyExpenses> dailyExpensesSerde;

	public KafkaStreamsConfig(TopicsProperties properties, StreamsHelper helper, JsonSerde<ClientProfile> clientProfileSerde, JsonSerde<DailyExpenses> dailyExpensesSerde) {
		this.properties = properties;
		this.helper = helper;
		this.clientProfileSerde = clientProfileSerde;
		this.dailyExpensesSerde = dailyExpensesSerde;
	}

	@Bean
	public KStream<String, ?> kstream(StreamsBuilder streamsBuilder) {
		// Hopping time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(30)).advanceBy(Duration.ofDays(1));
		// Tumbling time windows
//		TimeWindows period = TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY));

/*
		KStream<String, Transaction> transactions = helper.transactionsStream(streamsBuilder);

		KTable<Windowed<String>, Integer> windowedDailyExpensesTable = transactions
//				.transform(new TransformerDebugger<>())
//				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.groupByKey(helper.transactionsByClientId())
				.windowedBy(TimeWindows.of(Duration.ofDays(1)).grace(Duration.ofDays(DELAY)))
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						helper.dailyTransactionsByClientId("aggregate"));

		*/
		/*
		 * TimeWindowedKStream: "windowed" implies that the KTable key is a combined key of the original record key and a window ID
		 * CachingWindowStore.putAndMaybeForward: binaryWindowKey = cacheFunction.key(entry.key()).get()
		 * WindowKeySchema.toStoreKeyBinary(key, windowStartTimestamp, 0)
		 * WindowKeySchema.extractWindow(byte[] binaryKey, long windowSize)
		 *//*

		KStream<Windowed<String>, Integer> windowedDailyExpenses =
				windowedDailyExpensesTable.toStream(Named.as("windowedDailyExpenses_stream"));

		windowedDailyExpenses
//				.peek((windowedClientId, amount) -> log.debug("\n\tkey = {}, [{} to {}), amount = {}",
//						windowedClientId.key(),
//						localDateOf(windowedClientId.window().start()),
//						localDateOf(windowedClientId.window().end()), amount))
				.map((w, amount) -> KeyValue.pair(keyOf(w), amount), Named.as("clientId_and_amount_mapper"))
				.to(properties.getDailyExpenses(),
						helper.producedWithInteger("dailyExpenses_stream"));
*/

/*
		KStream<String, ClientProfile> clientProfiles = helper.clientProfiles(streamsBuilder);
		clientProfiles.foreach((k, de) -> log.debug("\n\tkey = {}, value = {}", k, de));
		return clientProfiles;
*/

/*
		KTable<String, ClientProfile> clientProfilesTable = helper.clientProfilesTable(streamsBuilder);
		KStream<String, ClientProfile> clientProfiles = clientProfilesTable.toStream();
		clientProfiles.foreach((k, de) -> log.debug("\n\tkey = {}, value = {}", k, de));
		return clientProfiles;
*/

/*
		// start from dailyExpensesDetails
		KTable<String, ClientProfile> clientProfilesTable = helper.clientProfilesTable(streamsBuilder);
		KStream<String, DailyExpenses> dailyExpensesDetails = helper.dailyExpensesDetails(streamsBuilder);
		dailyExpensesDetails
				.join(clientProfilesTable, (de, cp) -> de,
						Joined.<String, DailyExpenses, ClientProfile>
								as("dailyExpenses_join_clientProfiles")
								.withKeySerde(Serdes.String())
								.withValueSerde(dailyExpensesSerde)
								.withOtherValueSerde(clientProfileSerde))
				.foreach((k, de) -> log.debug("\n\tkey = {}, value = {}", k, de));
		return dailyExpensesDetails;
*/

		// start from dailyExpenses
		KTable<String, ClientProfile> clientProfilesTable = helper.clientProfilesTable(streamsBuilder);
		KStream<String, Integer> dailyExpenses = helper.dailyExpenses(streamsBuilder);
		dailyExpenses
				.peek((k, amount) -> log.debug("\n\tclientId/day = {}, total spent = {}", k, amount))
				.map((k, amount) -> {
					Optional<WindowUtils.WindowBasedKey<String>> winBasedKeyOptional = WindowUtils.parse(k);
					return winBasedKeyOptional
							.map(it -> {
								String key = it.getData();
								return KeyValue.pair(key, new DailyExpenses(key, it.getTime(), amount));
							})
							.orElse(null);
				})
				.join(clientProfilesTable, (de, cp) -> {
							if (cp.getDailyMaxAmount() < de.getAmount()) {
								return new OverdueDailyExpenses(cp.getDailyMaxAmount(), de);
							}
							log.debug("\n\t{}\n\t{}", de, cp);
							return null;
						},
						Joined.<String, DailyExpenses, ClientProfile>
								as("dailyExpenses_join_clientProfiles")
								.withKeySerde(Serdes.String())
								.withValueSerde(dailyExpensesSerde)
								.withOtherValueSerde(clientProfileSerde))
				.filter((k, v) -> v != null)
				.foreach((clientId, ode) -> {
					DailyExpenses de = ode.getDailyExpenses();
					log.debug("\n\tMAIL: {} spent {} GBP on {} (alert set for more than {})",
							de.getClientId(), de.getAmount(), format(de.getTime()), ode.getDailyMaxAmount());
				});
		return dailyExpenses;

/*
		windowedDailyExpenses
				.map((w, amount) -> {
					LocalDate time = localDateOf(w.window().start());
					return KeyValue.pair(w.key(), new DailyExpenses(w.key(), time, amount));
				}, Named.as("clientId_and_DailyExpenses_mapper"))
				.peek((k, de) -> log.debug("\n\tkey = {}, value = {}", k, de))
				.join(clientProfilesTable, (de, cp) -> de, helper.dailyExpensesByClientIdJoin())
				.foreach((k, de) -> log.debug("\n\tkey = {}, value = {}", k, de));
*/
/*
				.foreach((k, amount) -> {
					Optional<WindowUtils.WindowBasedKey<String>> wkOptional = WindowUtils.parse(k);
					wkOptional.ifPresent(wk -> log.debug("\n\tMAIL: {} spent {} GBP on {}",
							wk.getData(), amount, format(wkOptional.get().getTime())));
				}, Named.as("mail_sender"));
*/

//		return transactions;
	}

/*
	@Bean
	public KStream<String, PersonStars> personJoinStars(StreamsBuilder streamsBuilder) {
		KStream<String, Person> persons = serde.personStream(properties.getPersons(), streamsBuilder);
		KTable<String, Integer> stars = serde.integerTable(properties.getStars(), streamsBuilder);

		KStream<String, PersonStars> stream = persons
				.join(stars, PersonStars::new,
						Joined.with(Serdes.String(), personSerde, Serdes.Integer(),
								joinName(properties.getPersonsUpper(), properties.getStars())));
		stream
				.transformValues(new ValueTransformerWithKeyDebugger<>())
				.to(properties.getPersonsStars(), serde.producedWithPersonStars("to-personsStarsTopic"));

		return stream;
	}

	@Bean
	public StreamsBuilderFactoryBean defaultKafkaStreamsBuilder(KafkaStreamsConfiguration configuration) {
		return new StreamsBuilderFactoryBean(configuration, new CleanupConfig(true, true));
	}
*/
}
