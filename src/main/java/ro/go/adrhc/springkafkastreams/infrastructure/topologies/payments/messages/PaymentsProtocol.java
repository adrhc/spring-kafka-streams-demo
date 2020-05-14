/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages;

/**
 * payments protocol
 */
@org.apache.avro.specific.AvroGenerated
public interface PaymentsProtocol {
	org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"PaymentsProtocol\",\"namespace\":\"ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages\",\"doc\":\"payments protocol\",\"types\":[{\"type\":\"record\",\"name\":\"Transaction\",\"fields\":[{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"merchantId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"amount\",\"type\":\"int\"}]}],\"messages\":{}}");

	@SuppressWarnings("all")
	/** payments protocol */
	public interface Callback extends PaymentsProtocol {
		public static final org.apache.avro.Protocol PROTOCOL = ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.messages.PaymentsProtocol.PROTOCOL;
	}
}
