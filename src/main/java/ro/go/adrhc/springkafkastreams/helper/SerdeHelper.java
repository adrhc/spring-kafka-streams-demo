package ro.go.adrhc.springkafkastreams.helper;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.Person;
import ro.go.adrhc.springkafkastreams.model.PersonStars;
import ro.go.adrhc.springkafkastreams.model.Transaction;

@Component
public class SerdeHelper {
	@Autowired
	private TopicsProperties properties;
	@Autowired
	@Qualifier("personSerde")
	private JsonSerde<Person> personSerde;
	@Autowired
	@Qualifier("transactionSerde")
	private JsonSerde<Transaction> transactionSerde;
	@Autowired
	@Qualifier("personStarsSerde")
	private JsonSerde<PersonStars> personStarsSerde;

	public Produced<String, PersonStars> producedWithPersonStars(String name) {
		return Produced.with(Serdes.String(), personStarsSerde).withName(name);
	}

	public Produced<String, Person> producedWithPerson(String name) {
		return Produced.with(Serdes.String(), personSerde).withName(name);
	}

	public Produced<String, Integer> producedWithInteger(String name) {
		return Produced.with(Serdes.String(), Serdes.Integer()).withName(name);
	}

	public Consumed<String, Person> consumedWithPerson(String name) {
		return Consumed.with(Serdes.String(), personSerde).withName(name);
	}

	public Consumed<String, Transaction> consumedWithTransaction(String name) {
		return Consumed.with(Serdes.String(), transactionSerde).withName(name);
	}

	public Consumed<String, Integer> consumedWithInteger(String name) {
		return Consumed.with(Serdes.String(), Serdes.Integer()).withName(name);
	}

	public KStream<String, Person> personsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getPersons(),
				this.consumedWithPerson("stream-" + properties.getPersons()));
	}

	public KStream<String, Transaction> transactionsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getTransactions(),
				this.consumedWithTransaction("transactions-" + properties.getTransactions()));
	}

	public KStream<String, Integer> starsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(properties.getStars(),
				consumedWithInteger("table-" + properties.getStars()));
	}

	public KTable<String, Integer> starsTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(properties.getStars(),
				consumedWithInteger("table-" + properties.getStars()),
				Materialized.as("store-" + properties.getStars()));
	}
}
