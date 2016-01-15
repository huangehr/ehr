/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.yihu.ehr.data.domain;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
/**
 * 排序方向及排序属性包装. 为TSort提供排序源。
 * 
 */
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-01-15")
public class TOrder implements org.apache.thrift.TBase<TOrder, TOrder._Fields>, java.io.Serializable, Cloneable, Comparable<TOrder> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TOrder");

  private static final org.apache.thrift.protocol.TField DIRECTION_FIELD_DESC = new org.apache.thrift.protocol.TField("direction", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField PROPERTY_FIELD_DESC = new org.apache.thrift.protocol.TField("property", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField IGNORE_CASE_FIELD_DESC = new org.apache.thrift.protocol.TField("ignoreCase", org.apache.thrift.protocol.TType.BOOL, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TOrderStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TOrderTupleSchemeFactory());
  }

  /**
   * 
   * @see TDirection
   */
  public TDirection direction; // required
  public String property; // required
  public boolean ignoreCase; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see TDirection
     */
    DIRECTION((short)1, "direction"),
    PROPERTY((short)2, "property"),
    IGNORE_CASE((short)3, "ignoreCase");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // DIRECTION
          return DIRECTION;
        case 2: // PROPERTY
          return PROPERTY;
        case 3: // IGNORE_CASE
          return IGNORE_CASE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __IGNORECASE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.DIRECTION, new org.apache.thrift.meta_data.FieldMetaData("direction", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TDirection.class)));
    tmpMap.put(_Fields.PROPERTY, new org.apache.thrift.meta_data.FieldMetaData("property", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.IGNORE_CASE, new org.apache.thrift.meta_data.FieldMetaData("ignoreCase", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TOrder.class, metaDataMap);
  }

  public TOrder() {
  }

  public TOrder(
    TDirection direction,
    String property,
    boolean ignoreCase)
  {
    this();
    this.direction = direction;
    this.property = property;
    this.ignoreCase = ignoreCase;
    setIgnoreCaseIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TOrder(TOrder other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetDirection()) {
      this.direction = other.direction;
    }
    if (other.isSetProperty()) {
      this.property = other.property;
    }
    this.ignoreCase = other.ignoreCase;
  }

  public TOrder deepCopy() {
    return new TOrder(this);
  }

  @Override
  public void clear() {
    this.direction = null;
    this.property = null;
    setIgnoreCaseIsSet(false);
    this.ignoreCase = false;
  }

  /**
   * 
   * @see TDirection
   */
  public TDirection getDirection() {
    return this.direction;
  }

  /**
   * 
   * @see TDirection
   */
  public TOrder setDirection(TDirection direction) {
    this.direction = direction;
    return this;
  }

  public void unsetDirection() {
    this.direction = null;
  }

  /** Returns true if field direction is set (has been assigned a value) and false otherwise */
  public boolean isSetDirection() {
    return this.direction != null;
  }

  public void setDirectionIsSet(boolean value) {
    if (!value) {
      this.direction = null;
    }
  }

  public String getProperty() {
    return this.property;
  }

  public TOrder setProperty(String property) {
    this.property = property;
    return this;
  }

  public void unsetProperty() {
    this.property = null;
  }

  /** Returns true if field property is set (has been assigned a value) and false otherwise */
  public boolean isSetProperty() {
    return this.property != null;
  }

  public void setPropertyIsSet(boolean value) {
    if (!value) {
      this.property = null;
    }
  }

  public boolean isIgnoreCase() {
    return this.ignoreCase;
  }

  public TOrder setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
    setIgnoreCaseIsSet(true);
    return this;
  }

  public void unsetIgnoreCase() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __IGNORECASE_ISSET_ID);
  }

  /** Returns true if field ignoreCase is set (has been assigned a value) and false otherwise */
  public boolean isSetIgnoreCase() {
    return EncodingUtils.testBit(__isset_bitfield, __IGNORECASE_ISSET_ID);
  }

  public void setIgnoreCaseIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __IGNORECASE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case DIRECTION:
      if (value == null) {
        unsetDirection();
      } else {
        setDirection((TDirection)value);
      }
      break;

    case PROPERTY:
      if (value == null) {
        unsetProperty();
      } else {
        setProperty((String)value);
      }
      break;

    case IGNORE_CASE:
      if (value == null) {
        unsetIgnoreCase();
      } else {
        setIgnoreCase((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case DIRECTION:
      return getDirection();

    case PROPERTY:
      return getProperty();

    case IGNORE_CASE:
      return isIgnoreCase();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case DIRECTION:
      return isSetDirection();
    case PROPERTY:
      return isSetProperty();
    case IGNORE_CASE:
      return isSetIgnoreCase();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TOrder)
      return this.equals((TOrder)that);
    return false;
  }

  public boolean equals(TOrder that) {
    if (that == null)
      return false;

    boolean this_present_direction = true && this.isSetDirection();
    boolean that_present_direction = true && that.isSetDirection();
    if (this_present_direction || that_present_direction) {
      if (!(this_present_direction && that_present_direction))
        return false;
      if (!this.direction.equals(that.direction))
        return false;
    }

    boolean this_present_property = true && this.isSetProperty();
    boolean that_present_property = true && that.isSetProperty();
    if (this_present_property || that_present_property) {
      if (!(this_present_property && that_present_property))
        return false;
      if (!this.property.equals(that.property))
        return false;
    }

    boolean this_present_ignoreCase = true;
    boolean that_present_ignoreCase = true;
    if (this_present_ignoreCase || that_present_ignoreCase) {
      if (!(this_present_ignoreCase && that_present_ignoreCase))
        return false;
      if (this.ignoreCase != that.ignoreCase)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_direction = true && (isSetDirection());
    list.add(present_direction);
    if (present_direction)
      list.add(direction.getValue());

    boolean present_property = true && (isSetProperty());
    list.add(present_property);
    if (present_property)
      list.add(property);

    boolean present_ignoreCase = true;
    list.add(present_ignoreCase);
    if (present_ignoreCase)
      list.add(ignoreCase);

    return list.hashCode();
  }

  @Override
  public int compareTo(TOrder other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetDirection()).compareTo(other.isSetDirection());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDirection()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.direction, other.direction);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetProperty()).compareTo(other.isSetProperty());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetProperty()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.property, other.property);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetIgnoreCase()).compareTo(other.isSetIgnoreCase());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIgnoreCase()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ignoreCase, other.ignoreCase);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TOrder(");
    boolean first = true;

    sb.append("direction:");
    if (this.direction == null) {
      sb.append("null");
    } else {
      sb.append(this.direction);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("property:");
    if (this.property == null) {
      sb.append("null");
    } else {
      sb.append(this.property);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("ignoreCase:");
    sb.append(this.ignoreCase);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (direction == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'direction' was not present! Struct: " + toString());
    }
    if (property == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'property' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'ignoreCase' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TOrderStandardSchemeFactory implements SchemeFactory {
    public TOrderStandardScheme getScheme() {
      return new TOrderStandardScheme();
    }
  }

  private static class TOrderStandardScheme extends StandardScheme<TOrder> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TOrder struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // DIRECTION
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.direction = com.yihu.ehr.data.domain.TDirection.findByValue(iprot.readI32());
              struct.setDirectionIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PROPERTY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.property = iprot.readString();
              struct.setPropertyIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // IGNORE_CASE
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.ignoreCase = iprot.readBool();
              struct.setIgnoreCaseIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetIgnoreCase()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'ignoreCase' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TOrder struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.direction != null) {
        oprot.writeFieldBegin(DIRECTION_FIELD_DESC);
        oprot.writeI32(struct.direction.getValue());
        oprot.writeFieldEnd();
      }
      if (struct.property != null) {
        oprot.writeFieldBegin(PROPERTY_FIELD_DESC);
        oprot.writeString(struct.property);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(IGNORE_CASE_FIELD_DESC);
      oprot.writeBool(struct.ignoreCase);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TOrderTupleSchemeFactory implements SchemeFactory {
    public TOrderTupleScheme getScheme() {
      return new TOrderTupleScheme();
    }
  }

  private static class TOrderTupleScheme extends TupleScheme<TOrder> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TOrder struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.direction.getValue());
      oprot.writeString(struct.property);
      oprot.writeBool(struct.ignoreCase);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TOrder struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.direction = com.yihu.ehr.data.domain.TDirection.findByValue(iprot.readI32());
      struct.setDirectionIsSet(true);
      struct.property = iprot.readString();
      struct.setPropertyIsSet(true);
      struct.ignoreCase = iprot.readBool();
      struct.setIgnoreCaseIsSet(true);
    }
  }

}

