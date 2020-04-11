package ro.go.adrhc.springkafkastreams.json;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springkafkastreams.config.SerdeConfig;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static ro.go.adrhc.springkafkastreams.util.AbstractTestDTOFactory.randomTransaction;

@ActiveProfiles({"v2", "test"})
@JsonTest
@Import(SerdeConfig.class)
@Slf4j
class TransactionSerdeTest {
	@Autowired
	private JacksonTester<Transaction> json;
	@Autowired
	@Qualifier("transactionSerde")
	private JsonSerde<Transaction> transactionSerde;

	@Test
	void transactions() throws IOException {
		log.debug(this.json.write(randomTransaction()).getJson());
		log.debug(new String(transactionSerde.serializer()
				.serialize("", randomTransaction())), UTF_8);
		assertThat(this.json.write(randomTransaction()))
				.extractingJsonPathStringValue("@.merchantId")
				.startsWith("merchant-");
	}
}
