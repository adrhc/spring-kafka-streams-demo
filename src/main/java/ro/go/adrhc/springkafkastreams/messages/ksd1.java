/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.messages;

/** kafka streams demo 1 */
@org.apache.avro.specific.AvroGenerated
public interface ksd1 {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"ksd1\",\"namespace\":\"ro.go.adrhc.springkafkastreams.messages\",\"doc\":\"kafka streams demo 1\",\"types\":[{\"type\":\"record\",\"name\":\"ClientProfile\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"dailyMaxAmount\",\"type\":\"int\"},{\"name\":\"periodMaxAmount\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"Transaction\",\"fields\":[{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"merchantId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"amount\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"DailyTotalSpent\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"PeriodTotalSpent\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"DailyExceeded\",\"fields\":[{\"name\":\"dailyMaxAmount\",\"type\":\"int\"},{\"name\":\"dailyTotalSpent\",\"type\":\"DailyTotalSpent\"}]},{\"type\":\"record\",\"name\":\"PeriodExceeded\",\"fields\":[{\"name\":\"periodMaxAmount\",\"type\":\"int\"},{\"name\":\"periodTotalSpent\",\"type\":\"PeriodTotalSpent\"}]},{\"type\":\"record\",\"name\":\"Command\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"parameters\",\"type\":[\"null\",{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  /** kafka streams demo 1 */
  public interface Callback extends ksd1 {
    public static final org.apache.avro.Protocol PROTOCOL = ro.go.adrhc.springkafkastreams.messages.ksd1.PROTOCOL;
  }
}