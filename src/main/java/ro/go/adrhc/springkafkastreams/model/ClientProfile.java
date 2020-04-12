package ro.go.adrhc.springkafkastreams.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ClientProfile implements Serializable {
	private String clientId;
	private int dailyMaxAmount;
}
