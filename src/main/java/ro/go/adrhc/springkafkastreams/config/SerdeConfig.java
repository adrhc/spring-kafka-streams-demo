package ro.go.adrhc.springkafkastreams.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.model.OverdueDailyExpenses;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.util.Map;

import static org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES;
import static org.springframework.kafka.support.serializer.JsonSerializer.TYPE_MAPPINGS;

@Configuration
public class SerdeConfig {
	@Value("${type-mapping:}")
	private String typeMapping;

	@Bean
	public JsonSerde<Transaction> transactionSerde() {
		return jsonSerdeImpl(Transaction.class);
	}

	@Bean
	public JsonSerde<ClientProfile> clientProfileSerde() {
		return jsonSerdeImpl(ClientProfile.class);
	}

	@Bean
	public JsonSerde<DailyTotalSpent> dailyExpensesSerde() {
		return jsonSerdeImpl(DailyTotalSpent.class);
	}

	@Bean
	public JsonSerde<OverdueDailyExpenses> overdueDailyExpensesSerde() {
		return jsonSerdeImpl(OverdueDailyExpenses.class);
	}

	@Bean
	public JsonSerde<?> jsonSerde() {
		return jsonSerdeImpl(null);
	}

	private <T> JsonSerde<T> jsonSerdeImpl(Class<T> tClass) {
		JsonSerde<T> serde = new JsonSerde<>(tClass);
		if (typeMapping.isEmpty()) {
			serde.configure(Map.of(TRUSTED_PACKAGES, "*"), false);
			return serde;
		}
		serde.configure(Map.of(TRUSTED_PACKAGES, "*", TYPE_MAPPINGS, typeMapping), false);
		return serde;
	}
}
