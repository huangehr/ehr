/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.yihu.ehr.pack;

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
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-01-18")
public class TJsonPackage implements org.apache.thrift.TBase<TJsonPackage, TJsonPackage._Fields>, java.io.Serializable, Cloneable, Comparable<TJsonPackage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TJsonPackage");

  private static final org.apache.thrift.protocol.TField ID_FIELD_DESC = new org.apache.thrift.protocol.TField("id", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PWD_FIELD_DESC = new org.apache.thrift.protocol.TField("pwd", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField REMOTE_PATH_FIELD_DESC = new org.apache.thrift.protocol.TField("remotePath", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField USER_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("userId", org.apache.thrift.protocol.TType.STRING, (short)4);
  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("message", org.apache.thrift.protocol.TType.STRING, (short)5);
  private static final org.apache.thrift.protocol.TField RECEIVE_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("receiveDate", org.apache.thrift.protocol.TType.STRING, (short)6);
  private static final org.apache.thrift.protocol.TField PARSE_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("parseDate", org.apache.thrift.protocol.TType.STRING, (short)7);
  private static final org.apache.thrift.protocol.TField FINISH_DATE_FIELD_DESC = new org.apache.thrift.protocol.TField("finishDate", org.apache.thrift.protocol.TType.STRING, (short)8);
  private static final org.apache.thrift.protocol.TField ARCHIVE_STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("archiveStatus", org.apache.thrift.protocol.TType.I32, (short)9);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TJsonPackageStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TJsonPackageTupleSchemeFactory());
  }

  public String id; // required
  public String pwd; // required
  public String remotePath; // required
  public String userId; // required
  public String message; // required
  public String receiveDate; // required
  public String parseDate; // required
  public String finishDate; // required
  public int archiveStatus; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    ID((short)1, "id"),
    PWD((short)2, "pwd"),
    REMOTE_PATH((short)3, "remotePath"),
    USER_ID((short)4, "userId"),
    MESSAGE((short)5, "message"),
    RECEIVE_DATE((short)6, "receiveDate"),
    PARSE_DATE((short)7, "parseDate"),
    FINISH_DATE((short)8, "finishDate"),
    ARCHIVE_STATUS((short)9, "archiveStatus");

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
        case 1: // ID
          return ID;
        case 2: // PWD
          return PWD;
        case 3: // REMOTE_PATH
          return REMOTE_PATH;
        case 4: // USER_ID
          return USER_ID;
        case 5: // MESSAGE
          return MESSAGE;
        case 6: // RECEIVE_DATE
          return RECEIVE_DATE;
        case 7: // PARSE_DATE
          return PARSE_DATE;
        case 8: // FINISH_DATE
          return FINISH_DATE;
        case 9: // ARCHIVE_STATUS
          return ARCHIVE_STATUS;
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
  private static final int __ARCHIVESTATUS_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ID, new org.apache.thrift.meta_data.FieldMetaData("id", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PWD, new org.apache.thrift.meta_data.FieldMetaData("pwd", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.REMOTE_PATH, new org.apache.thrift.meta_data.FieldMetaData("remotePath", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.USER_ID, new org.apache.thrift.meta_data.FieldMetaData("userId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("message", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.RECEIVE_DATE, new org.apache.thrift.meta_data.FieldMetaData("receiveDate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PARSE_DATE, new org.apache.thrift.meta_data.FieldMetaData("parseDate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.FINISH_DATE, new org.apache.thrift.meta_data.FieldMetaData("finishDate", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.ARCHIVE_STATUS, new org.apache.thrift.meta_data.FieldMetaData("archiveStatus", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TJsonPackage.class, metaDataMap);
  }

  public TJsonPackage() {
  }

  public TJsonPackage(
    String id,
    String pwd,
    String remotePath,
    String userId,
    String message,
    String receiveDate,
    String parseDate,
    String finishDate,
    int archiveStatus)
  {
    this();
    this.id = id;
    this.pwd = pwd;
    this.remotePath = remotePath;
    this.userId = userId;
    this.message = message;
    this.receiveDate = receiveDate;
    this.parseDate = parseDate;
    this.finishDate = finishDate;
    this.archiveStatus = archiveStatus;
    setArchiveStatusIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TJsonPackage(TJsonPackage other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetId()) {
      this.id = other.id;
    }
    if (other.isSetPwd()) {
      this.pwd = other.pwd;
    }
    if (other.isSetRemotePath()) {
      this.remotePath = other.remotePath;
    }
    if (other.isSetUserId()) {
      this.userId = other.userId;
    }
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    if (other.isSetReceiveDate()) {
      this.receiveDate = other.receiveDate;
    }
    if (other.isSetParseDate()) {
      this.parseDate = other.parseDate;
    }
    if (other.isSetFinishDate()) {
      this.finishDate = other.finishDate;
    }
    this.archiveStatus = other.archiveStatus;
  }

  public TJsonPackage deepCopy() {
    return new TJsonPackage(this);
  }

  @Override
  public void clear() {
    this.id = null;
    this.pwd = null;
    this.remotePath = null;
    this.userId = null;
    this.message = null;
    this.receiveDate = null;
    this.parseDate = null;
    this.finishDate = null;
    setArchiveStatusIsSet(false);
    this.archiveStatus = 0;
  }

  public String getId() {
    return this.id;
  }

  public TJsonPackage setId(String id) {
    this.id = id;
    return this;
  }

  public void unsetId() {
    this.id = null;
  }

  /** Returns true if field id is set (has been assigned a value) and false otherwise */
  public boolean isSetId() {
    return this.id != null;
  }

  public void setIdIsSet(boolean value) {
    if (!value) {
      this.id = null;
    }
  }

  public String getPwd() {
    return this.pwd;
  }

  public TJsonPackage setPwd(String pwd) {
    this.pwd = pwd;
    return this;
  }

  public void unsetPwd() {
    this.pwd = null;
  }

  /** Returns true if field pwd is set (has been assigned a value) and false otherwise */
  public boolean isSetPwd() {
    return this.pwd != null;
  }

  public void setPwdIsSet(boolean value) {
    if (!value) {
      this.pwd = null;
    }
  }

  public String getRemotePath() {
    return this.remotePath;
  }

  public TJsonPackage setRemotePath(String remotePath) {
    this.remotePath = remotePath;
    return this;
  }

  public void unsetRemotePath() {
    this.remotePath = null;
  }

  /** Returns true if field remotePath is set (has been assigned a value) and false otherwise */
  public boolean isSetRemotePath() {
    return this.remotePath != null;
  }

  public void setRemotePathIsSet(boolean value) {
    if (!value) {
      this.remotePath = null;
    }
  }

  public String getUserId() {
    return this.userId;
  }

  public TJsonPackage setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  public void unsetUserId() {
    this.userId = null;
  }

  /** Returns true if field userId is set (has been assigned a value) and false otherwise */
  public boolean isSetUserId() {
    return this.userId != null;
  }

  public void setUserIdIsSet(boolean value) {
    if (!value) {
      this.userId = null;
    }
  }

  public String getMessage() {
    return this.message;
  }

  public TJsonPackage setMessage(String message) {
    this.message = message;
    return this;
  }

  public void unsetMessage() {
    this.message = null;
  }

  /** Returns true if field message is set (has been assigned a value) and false otherwise */
  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public String getReceiveDate() {
    return this.receiveDate;
  }

  public TJsonPackage setReceiveDate(String receiveDate) {
    this.receiveDate = receiveDate;
    return this;
  }

  public void unsetReceiveDate() {
    this.receiveDate = null;
  }

  /** Returns true if field receiveDate is set (has been assigned a value) and false otherwise */
  public boolean isSetReceiveDate() {
    return this.receiveDate != null;
  }

  public void setReceiveDateIsSet(boolean value) {
    if (!value) {
      this.receiveDate = null;
    }
  }

  public String getParseDate() {
    return this.parseDate;
  }

  public TJsonPackage setParseDate(String parseDate) {
    this.parseDate = parseDate;
    return this;
  }

  public void unsetParseDate() {
    this.parseDate = null;
  }

  /** Returns true if field parseDate is set (has been assigned a value) and false otherwise */
  public boolean isSetParseDate() {
    return this.parseDate != null;
  }

  public void setParseDateIsSet(boolean value) {
    if (!value) {
      this.parseDate = null;
    }
  }

  public String getFinishDate() {
    return this.finishDate;
  }

  public TJsonPackage setFinishDate(String finishDate) {
    this.finishDate = finishDate;
    return this;
  }

  public void unsetFinishDate() {
    this.finishDate = null;
  }

  /** Returns true if field finishDate is set (has been assigned a value) and false otherwise */
  public boolean isSetFinishDate() {
    return this.finishDate != null;
  }

  public void setFinishDateIsSet(boolean value) {
    if (!value) {
      this.finishDate = null;
    }
  }

  public int getArchiveStatus() {
    return this.archiveStatus;
  }

  public TJsonPackage setArchiveStatus(int archiveStatus) {
    this.archiveStatus = archiveStatus;
    setArchiveStatusIsSet(true);
    return this;
  }

  public void unsetArchiveStatus() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __ARCHIVESTATUS_ISSET_ID);
  }

  /** Returns true if field archiveStatus is set (has been assigned a value) and false otherwise */
  public boolean isSetArchiveStatus() {
    return EncodingUtils.testBit(__isset_bitfield, __ARCHIVESTATUS_ISSET_ID);
  }

  public void setArchiveStatusIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __ARCHIVESTATUS_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case ID:
      if (value == null) {
        unsetId();
      } else {
        setId((String)value);
      }
      break;

    case PWD:
      if (value == null) {
        unsetPwd();
      } else {
        setPwd((String)value);
      }
      break;

    case REMOTE_PATH:
      if (value == null) {
        unsetRemotePath();
      } else {
        setRemotePath((String)value);
      }
      break;

    case USER_ID:
      if (value == null) {
        unsetUserId();
      } else {
        setUserId((String)value);
      }
      break;

    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((String)value);
      }
      break;

    case RECEIVE_DATE:
      if (value == null) {
        unsetReceiveDate();
      } else {
        setReceiveDate((String)value);
      }
      break;

    case PARSE_DATE:
      if (value == null) {
        unsetParseDate();
      } else {
        setParseDate((String)value);
      }
      break;

    case FINISH_DATE:
      if (value == null) {
        unsetFinishDate();
      } else {
        setFinishDate((String)value);
      }
      break;

    case ARCHIVE_STATUS:
      if (value == null) {
        unsetArchiveStatus();
      } else {
        setArchiveStatus((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case ID:
      return getId();

    case PWD:
      return getPwd();

    case REMOTE_PATH:
      return getRemotePath();

    case USER_ID:
      return getUserId();

    case MESSAGE:
      return getMessage();

    case RECEIVE_DATE:
      return getReceiveDate();

    case PARSE_DATE:
      return getParseDate();

    case FINISH_DATE:
      return getFinishDate();

    case ARCHIVE_STATUS:
      return getArchiveStatus();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case ID:
      return isSetId();
    case PWD:
      return isSetPwd();
    case REMOTE_PATH:
      return isSetRemotePath();
    case USER_ID:
      return isSetUserId();
    case MESSAGE:
      return isSetMessage();
    case RECEIVE_DATE:
      return isSetReceiveDate();
    case PARSE_DATE:
      return isSetParseDate();
    case FINISH_DATE:
      return isSetFinishDate();
    case ARCHIVE_STATUS:
      return isSetArchiveStatus();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TJsonPackage)
      return this.equals((TJsonPackage)that);
    return false;
  }

  public boolean equals(TJsonPackage that) {
    if (that == null)
      return false;

    boolean this_present_id = true && this.isSetId();
    boolean that_present_id = true && that.isSetId();
    if (this_present_id || that_present_id) {
      if (!(this_present_id && that_present_id))
        return false;
      if (!this.id.equals(that.id))
        return false;
    }

    boolean this_present_pwd = true && this.isSetPwd();
    boolean that_present_pwd = true && that.isSetPwd();
    if (this_present_pwd || that_present_pwd) {
      if (!(this_present_pwd && that_present_pwd))
        return false;
      if (!this.pwd.equals(that.pwd))
        return false;
    }

    boolean this_present_remotePath = true && this.isSetRemotePath();
    boolean that_present_remotePath = true && that.isSetRemotePath();
    if (this_present_remotePath || that_present_remotePath) {
      if (!(this_present_remotePath && that_present_remotePath))
        return false;
      if (!this.remotePath.equals(that.remotePath))
        return false;
    }

    boolean this_present_userId = true && this.isSetUserId();
    boolean that_present_userId = true && that.isSetUserId();
    if (this_present_userId || that_present_userId) {
      if (!(this_present_userId && that_present_userId))
        return false;
      if (!this.userId.equals(that.userId))
        return false;
    }

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_receiveDate = true && this.isSetReceiveDate();
    boolean that_present_receiveDate = true && that.isSetReceiveDate();
    if (this_present_receiveDate || that_present_receiveDate) {
      if (!(this_present_receiveDate && that_present_receiveDate))
        return false;
      if (!this.receiveDate.equals(that.receiveDate))
        return false;
    }

    boolean this_present_parseDate = true && this.isSetParseDate();
    boolean that_present_parseDate = true && that.isSetParseDate();
    if (this_present_parseDate || that_present_parseDate) {
      if (!(this_present_parseDate && that_present_parseDate))
        return false;
      if (!this.parseDate.equals(that.parseDate))
        return false;
    }

    boolean this_present_finishDate = true && this.isSetFinishDate();
    boolean that_present_finishDate = true && that.isSetFinishDate();
    if (this_present_finishDate || that_present_finishDate) {
      if (!(this_present_finishDate && that_present_finishDate))
        return false;
      if (!this.finishDate.equals(that.finishDate))
        return false;
    }

    boolean this_present_archiveStatus = true;
    boolean that_present_archiveStatus = true;
    if (this_present_archiveStatus || that_present_archiveStatus) {
      if (!(this_present_archiveStatus && that_present_archiveStatus))
        return false;
      if (this.archiveStatus != that.archiveStatus)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_id = true && (isSetId());
    list.add(present_id);
    if (present_id)
      list.add(id);

    boolean present_pwd = true && (isSetPwd());
    list.add(present_pwd);
    if (present_pwd)
      list.add(pwd);

    boolean present_remotePath = true && (isSetRemotePath());
    list.add(present_remotePath);
    if (present_remotePath)
      list.add(remotePath);

    boolean present_userId = true && (isSetUserId());
    list.add(present_userId);
    if (present_userId)
      list.add(userId);

    boolean present_message = true && (isSetMessage());
    list.add(present_message);
    if (present_message)
      list.add(message);

    boolean present_receiveDate = true && (isSetReceiveDate());
    list.add(present_receiveDate);
    if (present_receiveDate)
      list.add(receiveDate);

    boolean present_parseDate = true && (isSetParseDate());
    list.add(present_parseDate);
    if (present_parseDate)
      list.add(parseDate);

    boolean present_finishDate = true && (isSetFinishDate());
    list.add(present_finishDate);
    if (present_finishDate)
      list.add(finishDate);

    boolean present_archiveStatus = true;
    list.add(present_archiveStatus);
    if (present_archiveStatus)
      list.add(archiveStatus);

    return list.hashCode();
  }

  @Override
  public int compareTo(TJsonPackage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetId()).compareTo(other.isSetId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.id, other.id);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPwd()).compareTo(other.isSetPwd());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPwd()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.pwd, other.pwd);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRemotePath()).compareTo(other.isSetRemotePath());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRemotePath()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.remotePath, other.remotePath);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUserId()).compareTo(other.isSetUserId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUserId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.userId, other.userId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMessage()).compareTo(other.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message, other.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetReceiveDate()).compareTo(other.isSetReceiveDate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetReceiveDate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.receiveDate, other.receiveDate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetParseDate()).compareTo(other.isSetParseDate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParseDate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.parseDate, other.parseDate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFinishDate()).compareTo(other.isSetFinishDate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFinishDate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.finishDate, other.finishDate);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetArchiveStatus()).compareTo(other.isSetArchiveStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetArchiveStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.archiveStatus, other.archiveStatus);
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
    StringBuilder sb = new StringBuilder("TJsonPackage(");
    boolean first = true;

    sb.append("id:");
    if (this.id == null) {
      sb.append("null");
    } else {
      sb.append(this.id);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("pwd:");
    if (this.pwd == null) {
      sb.append("null");
    } else {
      sb.append(this.pwd);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("remotePath:");
    if (this.remotePath == null) {
      sb.append("null");
    } else {
      sb.append(this.remotePath);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("userId:");
    if (this.userId == null) {
      sb.append("null");
    } else {
      sb.append(this.userId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("receiveDate:");
    if (this.receiveDate == null) {
      sb.append("null");
    } else {
      sb.append(this.receiveDate);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("parseDate:");
    if (this.parseDate == null) {
      sb.append("null");
    } else {
      sb.append(this.parseDate);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("finishDate:");
    if (this.finishDate == null) {
      sb.append("null");
    } else {
      sb.append(this.finishDate);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("archiveStatus:");
    sb.append(this.archiveStatus);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
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

  private static class TJsonPackageStandardSchemeFactory implements SchemeFactory {
    public TJsonPackageStandardScheme getScheme() {
      return new TJsonPackageStandardScheme();
    }
  }

  private static class TJsonPackageStandardScheme extends StandardScheme<TJsonPackage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TJsonPackage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.id = iprot.readString();
              struct.setIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PWD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.pwd = iprot.readString();
              struct.setPwdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // REMOTE_PATH
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.remotePath = iprot.readString();
              struct.setRemotePathIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // USER_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.userId = iprot.readString();
              struct.setUserIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.message = iprot.readString();
              struct.setMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // RECEIVE_DATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.receiveDate = iprot.readString();
              struct.setReceiveDateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // PARSE_DATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.parseDate = iprot.readString();
              struct.setParseDateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // FINISH_DATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.finishDate = iprot.readString();
              struct.setFinishDateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 9: // ARCHIVE_STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.archiveStatus = iprot.readI32();
              struct.setArchiveStatusIsSet(true);
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
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TJsonPackage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.id != null) {
        oprot.writeFieldBegin(ID_FIELD_DESC);
        oprot.writeString(struct.id);
        oprot.writeFieldEnd();
      }
      if (struct.pwd != null) {
        oprot.writeFieldBegin(PWD_FIELD_DESC);
        oprot.writeString(struct.pwd);
        oprot.writeFieldEnd();
      }
      if (struct.remotePath != null) {
        oprot.writeFieldBegin(REMOTE_PATH_FIELD_DESC);
        oprot.writeString(struct.remotePath);
        oprot.writeFieldEnd();
      }
      if (struct.userId != null) {
        oprot.writeFieldBegin(USER_ID_FIELD_DESC);
        oprot.writeString(struct.userId);
        oprot.writeFieldEnd();
      }
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      if (struct.receiveDate != null) {
        oprot.writeFieldBegin(RECEIVE_DATE_FIELD_DESC);
        oprot.writeString(struct.receiveDate);
        oprot.writeFieldEnd();
      }
      if (struct.parseDate != null) {
        oprot.writeFieldBegin(PARSE_DATE_FIELD_DESC);
        oprot.writeString(struct.parseDate);
        oprot.writeFieldEnd();
      }
      if (struct.finishDate != null) {
        oprot.writeFieldBegin(FINISH_DATE_FIELD_DESC);
        oprot.writeString(struct.finishDate);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(ARCHIVE_STATUS_FIELD_DESC);
      oprot.writeI32(struct.archiveStatus);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TJsonPackageTupleSchemeFactory implements SchemeFactory {
    public TJsonPackageTupleScheme getScheme() {
      return new TJsonPackageTupleScheme();
    }
  }

  private static class TJsonPackageTupleScheme extends TupleScheme<TJsonPackage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TJsonPackage struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetId()) {
        optionals.set(0);
      }
      if (struct.isSetPwd()) {
        optionals.set(1);
      }
      if (struct.isSetRemotePath()) {
        optionals.set(2);
      }
      if (struct.isSetUserId()) {
        optionals.set(3);
      }
      if (struct.isSetMessage()) {
        optionals.set(4);
      }
      if (struct.isSetReceiveDate()) {
        optionals.set(5);
      }
      if (struct.isSetParseDate()) {
        optionals.set(6);
      }
      if (struct.isSetFinishDate()) {
        optionals.set(7);
      }
      if (struct.isSetArchiveStatus()) {
        optionals.set(8);
      }
      oprot.writeBitSet(optionals, 9);
      if (struct.isSetId()) {
        oprot.writeString(struct.id);
      }
      if (struct.isSetPwd()) {
        oprot.writeString(struct.pwd);
      }
      if (struct.isSetRemotePath()) {
        oprot.writeString(struct.remotePath);
      }
      if (struct.isSetUserId()) {
        oprot.writeString(struct.userId);
      }
      if (struct.isSetMessage()) {
        oprot.writeString(struct.message);
      }
      if (struct.isSetReceiveDate()) {
        oprot.writeString(struct.receiveDate);
      }
      if (struct.isSetParseDate()) {
        oprot.writeString(struct.parseDate);
      }
      if (struct.isSetFinishDate()) {
        oprot.writeString(struct.finishDate);
      }
      if (struct.isSetArchiveStatus()) {
        oprot.writeI32(struct.archiveStatus);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TJsonPackage struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(9);
      if (incoming.get(0)) {
        struct.id = iprot.readString();
        struct.setIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.pwd = iprot.readString();
        struct.setPwdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.remotePath = iprot.readString();
        struct.setRemotePathIsSet(true);
      }
      if (incoming.get(3)) {
        struct.userId = iprot.readString();
        struct.setUserIdIsSet(true);
      }
      if (incoming.get(4)) {
        struct.message = iprot.readString();
        struct.setMessageIsSet(true);
      }
      if (incoming.get(5)) {
        struct.receiveDate = iprot.readString();
        struct.setReceiveDateIsSet(true);
      }
      if (incoming.get(6)) {
        struct.parseDate = iprot.readString();
        struct.setParseDateIsSet(true);
      }
      if (incoming.get(7)) {
        struct.finishDate = iprot.readString();
        struct.setFinishDateIsSet(true);
      }
      if (incoming.get(8)) {
        struct.archiveStatus = iprot.readI32();
        struct.setArchiveStatusIsSet(true);
      }
    }
  }

}

