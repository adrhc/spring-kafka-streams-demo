/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class PeriodTotalSpent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1866160509967786904L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PeriodTotalSpent\",\"namespace\":\"ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"time\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"amount\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
  }

  private static final BinaryMessageEncoder<PeriodTotalSpent> ENCODER =
      new BinaryMessageEncoder<PeriodTotalSpent>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<PeriodTotalSpent> DECODER =
      new BinaryMessageDecoder<PeriodTotalSpent>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<PeriodTotalSpent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<PeriodTotalSpent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<PeriodTotalSpent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<PeriodTotalSpent>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this PeriodTotalSpent to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a PeriodTotalSpent from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a PeriodTotalSpent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static PeriodTotalSpent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String clientId;
   private java.time.LocalDate time;
   private int amount;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public PeriodTotalSpent() {}

  /**
   * All-args constructor.
   * @param clientId The new value for clientId
   * @param time The new value for time
   * @param amount The new value for amount
   */
  public PeriodTotalSpent(java.lang.String clientId, java.time.LocalDate time, java.lang.Integer amount) {
    this.clientId = clientId;
    this.time = time;
    this.amount = amount;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return clientId;
    case 1: return time;
    case 2: return amount;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  private static final org.apache.avro.Conversion<?>[] conversions =
      new org.apache.avro.Conversion<?>[] {
      null,
      new org.apache.avro.data.TimeConversions.DateConversion(),
      null,
      null
  };

  @Override
  public org.apache.avro.Conversion<?> getConversion(int field) {
    return conversions[field];
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: clientId = value$ != null ? value$.toString() : null; break;
    case 1: time = (java.time.LocalDate)value$; break;
    case 2: amount = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'clientId' field.
   * @return The value of the 'clientId' field.
   */
  public java.lang.String getClientId() {
    return clientId;
  }


  /**
   * Sets the value of the 'clientId' field.
   * @param value the value to set.
   */
  public void setClientId(java.lang.String value) {
    this.clientId = value;
  }

  /**
   * Gets the value of the 'time' field.
   * @return The value of the 'time' field.
   */
  public java.time.LocalDate getTime() {
    return time;
  }


  /**
   * Sets the value of the 'time' field.
   * @param value the value to set.
   */
  public void setTime(java.time.LocalDate value) {
    this.time = value;
  }

  /**
   * Gets the value of the 'amount' field.
   * @return The value of the 'amount' field.
   */
  public int getAmount() {
    return amount;
  }


  /**
   * Sets the value of the 'amount' field.
   * @param value the value to set.
   */
  public void setAmount(int value) {
    this.amount = value;
  }

  /**
   * Creates a new PeriodTotalSpent RecordBuilder.
   * @return A new PeriodTotalSpent RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder newBuilder() {
    return new ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder();
  }

  /**
   * Creates a new PeriodTotalSpent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new PeriodTotalSpent RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder newBuilder(ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder other) {
    if (other == null) {
      return new ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder();
    } else {
      return new ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder(other);
    }
  }

  /**
   * Creates a new PeriodTotalSpent RecordBuilder by copying an existing PeriodTotalSpent instance.
   * @param other The existing instance to copy.
   * @return A new PeriodTotalSpent RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder newBuilder(ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent other) {
    if (other == null) {
      return new ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder();
    } else {
      return new ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder(other);
    }
  }

  /**
   * RecordBuilder for PeriodTotalSpent instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PeriodTotalSpent>
    implements org.apache.avro.data.RecordBuilder<PeriodTotalSpent> {

    private java.lang.String clientId;
    private java.time.LocalDate time;
    private int amount;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.clientId)) {
        this.clientId = data().deepCopy(fields()[0].schema(), other.clientId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.time)) {
        this.time = data().deepCopy(fields()[1].schema(), other.time);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.amount)) {
        this.amount = data().deepCopy(fields()[2].schema(), other.amount);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing PeriodTotalSpent instance
     * @param other The existing instance to copy.
     */
    private Builder(ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.clientId)) {
        this.clientId = data().deepCopy(fields()[0].schema(), other.clientId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.time)) {
        this.time = data().deepCopy(fields()[1].schema(), other.time);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.amount)) {
        this.amount = data().deepCopy(fields()[2].schema(), other.amount);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'clientId' field.
      * @return The value.
      */
    public java.lang.String getClientId() {
      return clientId;
    }


    /**
      * Sets the value of the 'clientId' field.
      * @param value The value of 'clientId'.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder setClientId(java.lang.String value) {
      validate(fields()[0], value);
      this.clientId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'clientId' field has been set.
      * @return True if the 'clientId' field has been set, false otherwise.
      */
    public boolean hasClientId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'clientId' field.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder clearClientId() {
      clientId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'time' field.
      * @return The value.
      */
    public java.time.LocalDate getTime() {
      return time;
    }


    /**
      * Sets the value of the 'time' field.
      * @param value The value of 'time'.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder setTime(java.time.LocalDate value) {
      validate(fields()[1], value);
      this.time = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'time' field has been set.
      * @return True if the 'time' field has been set, false otherwise.
      */
    public boolean hasTime() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'time' field.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder clearTime() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'amount' field.
      * @return The value.
      */
    public int getAmount() {
      return amount;
    }


    /**
      * Sets the value of the 'amount' field.
      * @param value The value of 'amount'.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder setAmount(int value) {
      validate(fields()[2], value);
      this.amount = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'amount' field has been set.
      * @return True if the 'amount' field has been set, false otherwise.
      */
    public boolean hasAmount() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'amount' field.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.infrastructure.topologies.payments.exceeds.period.messages.PeriodTotalSpent.Builder clearAmount() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PeriodTotalSpent build() {
      try {
        PeriodTotalSpent record = new PeriodTotalSpent();
        record.clientId = fieldSetFlags()[0] ? this.clientId : (java.lang.String) defaultValue(fields()[0]);
        record.time = fieldSetFlags()[1] ? this.time : (java.time.LocalDate) defaultValue(fields()[1]);
        record.amount = fieldSetFlags()[2] ? this.amount : (java.lang.Integer) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<PeriodTotalSpent>
    WRITER$ = (org.apache.avro.io.DatumWriter<PeriodTotalSpent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<PeriodTotalSpent>
    READER$ = (org.apache.avro.io.DatumReader<PeriodTotalSpent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










