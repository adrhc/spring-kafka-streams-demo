/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages;

/** period exceeds protocol */
@org.apache.avro.specific.AvroGenerated
public interface PeriodExceedsProtocol {
	org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"PeriodExceedsProtocol\",\"namespace\":\"ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages\",\"doc\":\"period exceeds protocol\",\"types\":[{\"type\":\"record\",\"name\":\"PeriodTotalSpent\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]},{\"type\":\"record\",\"name\":\"PeriodExceeded\",\"fields\":[{\"name\":\"periodMaxAmount\",\"type\":\"int\"},{\"name\":\"periodTotalSpent\",\"type\":\"PeriodTotalSpent\"}]}],\"messages\":{}}");

	@SuppressWarnings("all")
	/** period exceeds protocol */
	public interface Callback extends PeriodExceedsProtocol {
		public static final org.apache.avro.Protocol PROTOCOL = ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.range.period.messages.PeriodExceedsProtocol.PROTOCOL;
	}
}
