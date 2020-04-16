/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springkafkastreams.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class ClientProfile extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2217825179466078207L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ClientProfile\",\"namespace\":\"ro.go.adrhc.springkafkastreams.model\",\"fields\":[{\"name\":\"clientId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"dailyMaxAmount\",\"type\":\"int\"},{\"name\":\"periodMaxAmount\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ClientProfile> ENCODER =
      new BinaryMessageEncoder<ClientProfile>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ClientProfile> DECODER =
      new BinaryMessageDecoder<ClientProfile>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ClientProfile> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ClientProfile> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ClientProfile> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ClientProfile>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ClientProfile to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ClientProfile from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ClientProfile instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ClientProfile fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.String clientId;
  @Deprecated public int dailyMaxAmount;
  @Deprecated public int periodMaxAmount;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ClientProfile() {}

  /**
   * All-args constructor.
   * @param clientId The new value for clientId
   * @param dailyMaxAmount The new value for dailyMaxAmount
   * @param periodMaxAmount The new value for periodMaxAmount
   */
  public ClientProfile(java.lang.String clientId, java.lang.Integer dailyMaxAmount, java.lang.Integer periodMaxAmount) {
    this.clientId = clientId;
    this.dailyMaxAmount = dailyMaxAmount;
    this.periodMaxAmount = periodMaxAmount;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return clientId;
    case 1: return dailyMaxAmount;
    case 2: return periodMaxAmount;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: clientId = value$ != null ? value$.toString() : null; break;
    case 1: dailyMaxAmount = (java.lang.Integer)value$; break;
    case 2: periodMaxAmount = (java.lang.Integer)value$; break;
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
   * Gets the value of the 'dailyMaxAmount' field.
   * @return The value of the 'dailyMaxAmount' field.
   */
  public int getDailyMaxAmount() {
    return dailyMaxAmount;
  }


  /**
   * Sets the value of the 'dailyMaxAmount' field.
   * @param value the value to set.
   */
  public void setDailyMaxAmount(int value) {
    this.dailyMaxAmount = value;
  }

  /**
   * Gets the value of the 'periodMaxAmount' field.
   * @return The value of the 'periodMaxAmount' field.
   */
  public int getPeriodMaxAmount() {
    return periodMaxAmount;
  }


  /**
   * Sets the value of the 'periodMaxAmount' field.
   * @param value the value to set.
   */
  public void setPeriodMaxAmount(int value) {
    this.periodMaxAmount = value;
  }

  /**
   * Creates a new ClientProfile RecordBuilder.
   * @return A new ClientProfile RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder newBuilder() {
    return new ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder();
  }

  /**
   * Creates a new ClientProfile RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ClientProfile RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder newBuilder(ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder other) {
    if (other == null) {
      return new ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder();
    } else {
      return new ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder(other);
    }
  }

  /**
   * Creates a new ClientProfile RecordBuilder by copying an existing ClientProfile instance.
   * @param other The existing instance to copy.
   * @return A new ClientProfile RecordBuilder
   */
  public static ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder newBuilder(ro.go.adrhc.springkafkastreams.model.ClientProfile other) {
    if (other == null) {
      return new ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder();
    } else {
      return new ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder(other);
    }
  }

  /**
   * RecordBuilder for ClientProfile instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ClientProfile>
    implements org.apache.avro.data.RecordBuilder<ClientProfile> {

    private java.lang.String clientId;
    private int dailyMaxAmount;
    private int periodMaxAmount;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.clientId)) {
        this.clientId = data().deepCopy(fields()[0].schema(), other.clientId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.dailyMaxAmount)) {
        this.dailyMaxAmount = data().deepCopy(fields()[1].schema(), other.dailyMaxAmount);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.periodMaxAmount)) {
        this.periodMaxAmount = data().deepCopy(fields()[2].schema(), other.periodMaxAmount);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing ClientProfile instance
     * @param other The existing instance to copy.
     */
    private Builder(ro.go.adrhc.springkafkastreams.model.ClientProfile other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.clientId)) {
        this.clientId = data().deepCopy(fields()[0].schema(), other.clientId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.dailyMaxAmount)) {
        this.dailyMaxAmount = data().deepCopy(fields()[1].schema(), other.dailyMaxAmount);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.periodMaxAmount)) {
        this.periodMaxAmount = data().deepCopy(fields()[2].schema(), other.periodMaxAmount);
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
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder setClientId(java.lang.String value) {
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
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder clearClientId() {
      clientId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'dailyMaxAmount' field.
      * @return The value.
      */
    public int getDailyMaxAmount() {
      return dailyMaxAmount;
    }


    /**
      * Sets the value of the 'dailyMaxAmount' field.
      * @param value The value of 'dailyMaxAmount'.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder setDailyMaxAmount(int value) {
      validate(fields()[1], value);
      this.dailyMaxAmount = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'dailyMaxAmount' field has been set.
      * @return True if the 'dailyMaxAmount' field has been set, false otherwise.
      */
    public boolean hasDailyMaxAmount() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'dailyMaxAmount' field.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder clearDailyMaxAmount() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'periodMaxAmount' field.
      * @return The value.
      */
    public int getPeriodMaxAmount() {
      return periodMaxAmount;
    }


    /**
      * Sets the value of the 'periodMaxAmount' field.
      * @param value The value of 'periodMaxAmount'.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder setPeriodMaxAmount(int value) {
      validate(fields()[2], value);
      this.periodMaxAmount = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'periodMaxAmount' field has been set.
      * @return True if the 'periodMaxAmount' field has been set, false otherwise.
      */
    public boolean hasPeriodMaxAmount() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'periodMaxAmount' field.
      * @return This builder.
      */
    public ro.go.adrhc.springkafkastreams.model.ClientProfile.Builder clearPeriodMaxAmount() {
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientProfile build() {
      try {
        ClientProfile record = new ClientProfile();
        record.clientId = fieldSetFlags()[0] ? this.clientId : (java.lang.String) defaultValue(fields()[0]);
        record.dailyMaxAmount = fieldSetFlags()[1] ? this.dailyMaxAmount : (java.lang.Integer) defaultValue(fields()[1]);
        record.periodMaxAmount = fieldSetFlags()[2] ? this.periodMaxAmount : (java.lang.Integer) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ClientProfile>
    WRITER$ = (org.apache.avro.io.DatumWriter<ClientProfile>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ClientProfile>
    READER$ = (org.apache.avro.io.DatumReader<ClientProfile>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.clientId);

    out.writeInt(this.dailyMaxAmount);

    out.writeInt(this.periodMaxAmount);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.clientId = in.readString();

      this.dailyMaxAmount = in.readInt();

      this.periodMaxAmount = in.readInt();

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.clientId = in.readString();
          break;

        case 1:
          this.dailyMaxAmount = in.readInt();
          break;

        case 2:
          this.periodMaxAmount = in.readInt();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










