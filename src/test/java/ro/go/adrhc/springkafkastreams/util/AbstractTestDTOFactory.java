package ro.go.adrhc.springkafkastreams.util;

import org.apache.commons.lang3.RandomUtils;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;

public class AbstractTestDTOFactory {
	private static final Supplier<String> CLIENT_ID_SUPP =
			() -> "client" + randomInt(1, 1);
	private static final Supplier<String> MERCHANT_ID_SUPP =
			() -> "merchant" + randomInt(1, 10);

	public static ClientProfile randomClientProfile() {
		return new ClientProfile(CLIENT_ID_SUPP.get(), 500);
	}

	public static ClientProfile randomClientProfile(int dailyMaxAmount) {
		return new ClientProfile(CLIENT_ID_SUPP.get(), dailyMaxAmount);
	}

	public static Transaction randomTransaction() {
		Instant randomInstant = Instant.now().minus(randomInt(1, DELAY), DAYS);
		LocalDate ldt = LocalDate.ofInstant(randomInstant, ZoneOffset.UTC);
		return new Transaction(ldt,
				MERCHANT_ID_SUPP.get(),
				CLIENT_ID_SUPP.get(),
				randomInt(1, 100));
	}

	private static int randomInt(int origin, int includingBound) {
		return RandomUtils.nextInt(origin, includingBound + 1);
	}
}
