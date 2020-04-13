package ro.go.adrhc.springkafkastreams.producers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.TopicsProperties;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomTransaction;

@ActiveProfiles({"v2", "test"})
@SpringBootTest
@Slf4j
public class TransactionsProducerV2IT {
	@Autowired
	@Qualifier("jsonTemplate")
	private KafkaTemplate<Object, Object> jsonTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	@RepeatedTest(15)
	void send() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("transactions topic: {}", properties.getTransactions());
		Transaction transaction = randomTransaction();
		log.debug("transaction:\n{}", transaction);
		jsonTemplate.send(properties.getTransactions(), transaction.getClientId(), transaction);
	}
}
