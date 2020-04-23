package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.AppProperties;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomTransaction;

@EnabledIfSystemProperty(named = "enableIT", matches = "true")
@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class TransactionsProducerV2IT {
	@Autowired
	@Qualifier("avroKTemplate")
	private KafkaTemplate<Object, Object> avroKTemplate;
	@Autowired
	private AppProperties app;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@RepeatedTest(1)
	@DisabledIfSystemProperty(named = "transactionsCount", matches = "\\d+")
	void send() {
		log.debug("\n\tprofiles: {}", String.join(" + ", env.getActiveProfiles()));
		log.debug("\n\ttransactions topic: {}", properties.getTransactions());
		Transaction transaction = randomTransaction(app.getDailyGrace());
		log.debug("transaction:\n\t{}", transaction);
		avroKTemplate.send(properties.getTransactions(), transaction.getClientId(), transaction);
	}

	@Test
	@EnabledIfSystemProperty(named = "transactionsCount", matches = "\\d+")
	void sendMany() {
		int transactionsCount = transactionsCount();
		log.debug("\n\ttransactionsCount = {}", transactionsCount);
		IntStream.range(0, transactionsCount).forEach(i -> send());
	}

	private int transactionsCount() {
		String transactionsCount = System.getProperty("transactionsCount");
		if (transactionsCount == null) {
			return 1;
		}
		return parseInt(transactionsCount);
	}
}
