package ro.go.adrhc.springkafkastreams.util;

import org.apache.commons.lang3.RandomUtils;
import ro.go.adrhc.springkafkastreams.messages.ClientProfile;
import ro.go.adrhc.springkafkastreams.messages.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.messages.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.springkafkastreams.helper.StreamsHelper.DELAY;

public class AbstractTestDTOFactory {
	private static final Supplier<String> CLIENT_ID_SUPP =
			() -> "client" + randomInt(1, 1);
	private static final Supplier<String> MERCHANT_ID_SUPP =
			() -> "merchant" + randomInt(1, 10);
	private static final IntSupplier MAX_DAILY_AMOUNT_SUPP =
			() -> randomInt(50, 100);
	private static final IntSupplier MAX_PERIOD_AMOUNT_SUPP =
			() -> randomInt(150, 300);
	private static final IntSupplier AMOUNT_SUPP =
			() -> randomInt(1, 10);

	public static ClientProfile randomClientProfile() {
		return new ClientProfile(CLIENT_ID_SUPP.get(),
				MAX_DAILY_AMOUNT_SUPP.getAsInt(), MAX_PERIOD_AMOUNT_SUPP.getAsInt());
	}

	public static ClientProfile randomClientProfile(int dailyMaxAmount, int periodMaxAmount) {
		return new ClientProfile(CLIENT_ID_SUPP.get(), dailyMaxAmount, periodMaxAmount);
	}

	public static DailyTotalSpent randomDailyTotalSpent() {
		return new DailyTotalSpent(CLIENT_ID_SUPP.get(), LocalDate.now(), AMOUNT_SUPP.getAsInt());
	}

	public static Transaction randomTransaction() {
		Instant randomInstant = Instant.now().minus(randomInt(1, DELAY), DAYS);
		LocalDate ldt = LocalDate.ofInstant(randomInstant, ZoneOffset.UTC);
		return new Transaction(ldt,
				MERCHANT_ID_SUPP.get(),
				CLIENT_ID_SUPP.get(),
				AMOUNT_SUPP.getAsInt());
	}

	private static int randomInt(int origin, int includingBound) {
		return RandomUtils.nextInt(origin, includingBound + 1);
	}
}
