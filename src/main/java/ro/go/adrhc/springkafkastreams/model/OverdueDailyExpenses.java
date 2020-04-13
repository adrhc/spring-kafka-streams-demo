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
public class OverdueDailyExpenses implements Serializable {
	private int dailyMaxAmount;
	private DailyTotalSpent dailyTotalSpent;
}
