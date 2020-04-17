/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.messages;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

@org.apache.avro.specific.AvroGenerated
public class PeriodExceeded extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
	public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PeriodExceeded\",\"namespace\":\"ro.go.adrhc.springkafkastreams.messages\",\"fields\":[{\"name\":\"periodMaxAmount\",\"type\":\"int\"},{\"name\":\"periodTotalSpent\",\"type\":{\"type\":\"record\",\"name\":\"PeriodTotalSpent\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]}}]}");
	private static final long serialVersionUID = 4630824213979691033L;
	private static final SpecificData MODEL$ = new SpecificData();
	private static final BinaryMessageEncoder<PeriodExceeded> ENCODER =
			new BinaryMessageEncoder<PeriodExceeded>(MODEL$, SCHEMA$);
	private static final BinaryMessageDecoder<PeriodExceeded> DECODER =
			new BinaryMessageDecoder<PeriodExceeded>(MODEL$, SCHEMA$);
	@SuppressWarnings("unchecked")
	private static final org.apache.avro.io.DatumWriter<PeriodExceeded>
			WRITER$ = (org.apache.avro.io.DatumWriter<PeriodExceeded>) MODEL$.createDatumWriter(SCHEMA$);
	@SuppressWarnings("unchecked")
	private static final org.apache.avro.io.DatumReader<PeriodExceeded>
			READER$ = (org.apache.avro.io.DatumReader<PeriodExceeded>) MODEL$.createDatumReader(SCHEMA$);

	static {
		MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
	}

	private int periodMaxAmount;
	private ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent periodTotalSpent;

	/**
	 * Default constructor.  Note that this does not initialize fields
	 * to their default values from the schema.  If that is desired then
	 * one should use <code>newBuilder()</code>.
	 */
	public PeriodExceeded() {}

	/**
	 * All-args constructor.
	 *
	 * @param periodMaxAmount  The new value for periodMaxAmount
	 * @param periodTotalSpent The new value for periodTotalSpent
	 */
	public PeriodExceeded(java.lang.Integer periodMaxAmount, ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent periodTotalSpent) {
		this.periodMaxAmount = periodMaxAmount;
		this.periodTotalSpent = periodTotalSpent;
	}

	public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

	/**
	 * Return the BinaryMessageEncoder instance used by this class.
	 *
	 * @return the message encoder used by this class
	 */
	public static BinaryMessageEncoder<PeriodExceeded> getEncoder() {
		return ENCODER;
	}

	/**
	 * Return the BinaryMessageDecoder instance used by this class.
	 *
	 * @return the message decoder used by this class
	 */
	public static BinaryMessageDecoder<PeriodExceeded> getDecoder() {
		return DECODER;
	}

	/**
	 * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
	 *
	 * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
	 * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
	 */
	public static BinaryMessageDecoder<PeriodExceeded> createDecoder(SchemaStore resolver) {
		return new BinaryMessageDecoder<PeriodExceeded>(MODEL$, SCHEMA$, resolver);
	}

	/**
	 * Deserializes a PeriodExceeded from a ByteBuffer.
	 *
	 * @param b a byte buffer holding serialized data for an instance of this class
	 * @return a PeriodExceeded instance decoded from the given buffer
	 * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
	 */
	public static PeriodExceeded fromByteBuffer(
			java.nio.ByteBuffer b) throws java.io.IOException {
		return DECODER.decode(b);
	}

	/**
	 * Creates a new PeriodExceeded RecordBuilder.
	 *
	 * @return A new PeriodExceeded RecordBuilder
	 */
	public static ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder newBuilder() {
		return new ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder();
	}

	/**
	 * Creates a new PeriodExceeded RecordBuilder by copying an existing Builder.
	 *
	 * @param other The existing builder to copy.
	 * @return A new PeriodExceeded RecordBuilder
	 */
	public static ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder newBuilder(ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder other) {
		if (other == null) {
			return new ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder();
		} else {
			return new ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder(other);
		}
	}

	/**
	 * Creates a new PeriodExceeded RecordBuilder by copying an existing PeriodExceeded instance.
	 *
	 * @param other The existing instance to copy.
	 * @return A new PeriodExceeded RecordBuilder
	 */
	public static ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder newBuilder(ro.go.adrhc.springkafkastreams.messages.PeriodExceeded other) {
		if (other == null) {
			return new ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder();
		} else {
			return new ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder(other);
		}
	}

	/**
	 * Serializes this PeriodExceeded to a ByteBuffer.
	 *
	 * @return a buffer holding the serialized data for this instance
	 * @throws java.io.IOException if this instance could not be serialized
	 */
	public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
		return ENCODER.encode(this);
	}

	public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

	public org.apache.avro.Schema getSchema() { return SCHEMA$; }

	// Used by DatumWriter.  Applications should not call.
	public java.lang.Object get(int field$) {
		switch (field$) {
			case 0:
				return periodMaxAmount;
			case 1:
				return periodTotalSpent;
			default:
				throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	// Used by DatumReader.  Applications should not call.
	@SuppressWarnings(value = "unchecked")
	public void put(int field$, java.lang.Object value$) {
		switch (field$) {
			case 0:
				periodMaxAmount = (java.lang.Integer) value$;
				break;
			case 1:
				periodTotalSpent = (ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent) value$;
				break;
			default:
				throw new org.apache.avro.AvroRuntimeException("Bad index");
		}
	}

	/**
	 * Gets the value of the 'periodMaxAmount' field.
	 *
	 * @return The value of the 'periodMaxAmount' field.
	 */
	public int getPeriodMaxAmount() {
		return periodMaxAmount;
	}

	/**
	 * Sets the value of the 'periodMaxAmount' field.
	 *
	 * @param value the value to set.
	 */
	public void setPeriodMaxAmount(int value) {
		this.periodMaxAmount = value;
	}

	/**
	 * Gets the value of the 'periodTotalSpent' field.
	 *
	 * @return The value of the 'periodTotalSpent' field.
	 */
	public ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent getPeriodTotalSpent() {
		return periodTotalSpent;
	}

	/**
	 * Sets the value of the 'periodTotalSpent' field.
	 *
	 * @param value the value to set.
	 */
	public void setPeriodTotalSpent(ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent value) {
		this.periodTotalSpent = value;
	}

	@Override
	public void writeExternal(java.io.ObjectOutput out)
			throws java.io.IOException {
		WRITER$.write(this, SpecificData.getEncoder(out));
	}

	@Override
	public void readExternal(java.io.ObjectInput in)
			throws java.io.IOException {
		READER$.read(this, SpecificData.getDecoder(in));
	}

	/**
	 * RecordBuilder for PeriodExceeded instances.
	 */
	@org.apache.avro.specific.AvroGenerated
	public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PeriodExceeded>
			implements org.apache.avro.data.RecordBuilder<PeriodExceeded> {

		private int periodMaxAmount;
		private ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent periodTotalSpent;
		private ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.Builder periodTotalSpentBuilder;

		/**
		 * Creates a new Builder
		 */
		private Builder() {
			super(SCHEMA$);
		}

		/**
		 * Creates a Builder by copying an existing Builder.
		 *
		 * @param other The existing Builder to copy.
		 */
		private Builder(ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder other) {
			super(other);
			if (isValidValue(fields()[0], other.periodMaxAmount)) {
				this.periodMaxAmount = data().deepCopy(fields()[0].schema(), other.periodMaxAmount);
				fieldSetFlags()[0] = other.fieldSetFlags()[0];
			}
			if (isValidValue(fields()[1], other.periodTotalSpent)) {
				this.periodTotalSpent = data().deepCopy(fields()[1].schema(), other.periodTotalSpent);
				fieldSetFlags()[1] = other.fieldSetFlags()[1];
			}
			if (other.hasPeriodTotalSpentBuilder()) {
				this.periodTotalSpentBuilder = ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.newBuilder(other.getPeriodTotalSpentBuilder());
			}
		}

		/**
		 * Creates a Builder by copying an existing PeriodExceeded instance
		 *
		 * @param other The existing instance to copy.
		 */
		private Builder(ro.go.adrhc.springkafkastreams.messages.PeriodExceeded other) {
			super(SCHEMA$);
			if (isValidValue(fields()[0], other.periodMaxAmount)) {
				this.periodMaxAmount = data().deepCopy(fields()[0].schema(), other.periodMaxAmount);
				fieldSetFlags()[0] = true;
			}
			if (isValidValue(fields()[1], other.periodTotalSpent)) {
				this.periodTotalSpent = data().deepCopy(fields()[1].schema(), other.periodTotalSpent);
				fieldSetFlags()[1] = true;
			}
			this.periodTotalSpentBuilder = null;
		}

		/**
		 * Gets the value of the 'periodMaxAmount' field.
		 *
		 * @return The value.
		 */
		public int getPeriodMaxAmount() {
			return periodMaxAmount;
		}


		/**
		 * Sets the value of the 'periodMaxAmount' field.
		 *
		 * @param value The value of 'periodMaxAmount'.
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder setPeriodMaxAmount(int value) {
			validate(fields()[0], value);
			this.periodMaxAmount = value;
			fieldSetFlags()[0] = true;
			return this;
		}

		/**
		 * Checks whether the 'periodMaxAmount' field has been set.
		 *
		 * @return True if the 'periodMaxAmount' field has been set, false otherwise.
		 */
		public boolean hasPeriodMaxAmount() {
			return fieldSetFlags()[0];
		}


		/**
		 * Clears the value of the 'periodMaxAmount' field.
		 *
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder clearPeriodMaxAmount() {
			fieldSetFlags()[0] = false;
			return this;
		}

		/**
		 * Gets the value of the 'periodTotalSpent' field.
		 *
		 * @return The value.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent getPeriodTotalSpent() {
			return periodTotalSpent;
		}


		/**
		 * Sets the value of the 'periodTotalSpent' field.
		 *
		 * @param value The value of 'periodTotalSpent'.
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder setPeriodTotalSpent(ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent value) {
			validate(fields()[1], value);
			this.periodTotalSpentBuilder = null;
			this.periodTotalSpent = value;
			fieldSetFlags()[1] = true;
			return this;
		}

		/**
		 * Checks whether the 'periodTotalSpent' field has been set.
		 *
		 * @return True if the 'periodTotalSpent' field has been set, false otherwise.
		 */
		public boolean hasPeriodTotalSpent() {
			return fieldSetFlags()[1];
		}

		/**
		 * Gets the Builder instance for the 'periodTotalSpent' field and creates one if it doesn't exist yet.
		 *
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.Builder getPeriodTotalSpentBuilder() {
			if (periodTotalSpentBuilder == null) {
				if (hasPeriodTotalSpent()) {
					setPeriodTotalSpentBuilder(ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.newBuilder(periodTotalSpent));
				} else {
					setPeriodTotalSpentBuilder(ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.newBuilder());
				}
			}
			return periodTotalSpentBuilder;
		}

		/**
		 * Sets the Builder instance for the 'periodTotalSpent' field
		 *
		 * @param value The builder instance that must be set.
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder setPeriodTotalSpentBuilder(ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent.Builder value) {
			clearPeriodTotalSpent();
			periodTotalSpentBuilder = value;
			return this;
		}

		/**
		 * Checks whether the 'periodTotalSpent' field has an active Builder instance
		 *
		 * @return True if the 'periodTotalSpent' field has an active Builder instance
		 */
		public boolean hasPeriodTotalSpentBuilder() {
			return periodTotalSpentBuilder != null;
		}

		/**
		 * Clears the value of the 'periodTotalSpent' field.
		 *
		 * @return This builder.
		 */
		public ro.go.adrhc.springkafkastreams.messages.PeriodExceeded.Builder clearPeriodTotalSpent() {
			periodTotalSpent = null;
			periodTotalSpentBuilder = null;
			fieldSetFlags()[1] = false;
			return this;
		}

		@Override
		@SuppressWarnings("unchecked")
		public PeriodExceeded build() {
			try {
				PeriodExceeded record = new PeriodExceeded();
				record.periodMaxAmount = fieldSetFlags()[0] ? this.periodMaxAmount : (java.lang.Integer) defaultValue(fields()[0]);
				if (periodTotalSpentBuilder != null) {
					try {
						record.periodTotalSpent = this.periodTotalSpentBuilder.build();
					} catch (org.apache.avro.AvroMissingFieldException e) {
						e.addParentField(record.getSchema().getField("periodTotalSpent"));
						throw e;
					}
				} else {
					record.periodTotalSpent = fieldSetFlags()[1] ? this.periodTotalSpent : (ro.go.adrhc.springkafkastreams.messages.PeriodTotalSpent) defaultValue(fields()[1]);
				}
				return record;
			} catch (org.apache.avro.AvroMissingFieldException e) {
				throw e;
			} catch (java.lang.Exception e) {
				throw new org.apache.avro.AvroRuntimeException(e);
			}
		}
	}

}










