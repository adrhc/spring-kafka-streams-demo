package ro.go.adrhc.springkafkastreams.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import ro.go.adrhc.springkafkastreams.model.ClientProfile;
import ro.go.adrhc.springkafkastreams.model.DailyExceeded;
import ro.go.adrhc.springkafkastreams.model.DailyTotalSpent;
import ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey;

import java.util.Optional;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.format;
import static ro.go.adrhc.springkafkastreams.util.LocalDateBasedKey.parseWithStringData;

@Slf4j
public class PaymentsUtils {

	public static DailyExceeded joinDailyTotalSpentWithClientProfileOnClientId(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			// computing DailyExceeded
			return new DailyExceeded(cp.getDailyMaxAmount(), dts);
		}
		log.trace("\n\tskipping daily total spent under {} GBP\n\t{}\n\t{}", cp.getDailyMaxAmount(), dts, cp);
		return null;
	}

	public static KeyValue<String, DailyTotalSpent> clientIdDailyTotalSpentOf(String clientIdDay, Integer amount) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdDay);
		return winBasedKeyOptional
				.map(it -> {
					String clientId = it.getData();
					log.debug("\n\t{} spent a total of {} GBP on {}", clientId, amount, format(it.getTime()));
					return KeyValue.pair(clientId, new DailyTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}

	public static void printPeriodTotalExpenses(String clientIdPeriod, Integer amount, int totalPeriod) {
		Optional<LocalDateBasedKey<String>> winBasedKeyOptional = parseWithStringData(clientIdPeriod);
		winBasedKeyOptional.ifPresent(it -> {
			String clientId = it.getData();
			log.debug("\n\t{} spent a total of {} GBP for a period of {} days till {} (including)",
					clientId, amount, totalPeriod, format(it.getTime()));
		});
	}
}
