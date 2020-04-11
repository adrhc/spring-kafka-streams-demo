package ro.go.adrhc.springkafkastreams.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

import static ro.go.adrhc.springkafkastreams.util.DateUtils.localDateTimeToLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction implements Serializable {
	private LocalDateTime time;
	private String merchantId;
	private String clientId;
	private int amount;

	public long ofEpochSecond() {
		return localDateTimeToLong(time);
	}
}
