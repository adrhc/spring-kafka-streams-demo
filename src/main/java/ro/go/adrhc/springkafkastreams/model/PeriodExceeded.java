package ro.go.adrhc.springkafkastreams.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PeriodExceeded implements Serializable {
	private int periodMaxAmount;
	private PeriodTotalSpent periodTotalSpent;
}
