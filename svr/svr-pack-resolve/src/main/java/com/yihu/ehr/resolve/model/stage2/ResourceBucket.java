package com.yihu.ehr.resolve.model.stage2;

import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.family.ResourceCells;

import java.util.*;

/**
 * 健康档案资源化临时存储工具。此阶段也是存在于内存中，资源化之后会存入hbase。
 *
 * @author Sand
 * @created 2016.05.16 13:52
 */
public class ResourceBucket {

    //档案包ID
    private final String packId;
    //档案包接收时间
    private final Date receiveDate;

    //主键
    private final String id;
    //主表
    private final String master;
    //细表
    private final String slave;
    //基础列族
    private final String basicFamily;
    //数据列族
    private final String dFamily;
    //基础索引字段
    private Map<String, String> basicRecord = new HashMap<>();
    //主记录
    private MasterRecord masterRecord = new MasterRecord();
    //子记录
    private List<SubRecord> subRecords = new ArrayList<>();
    //质控数据
    private QcMetadataRecords qcMetadataRecords = new QcMetadataRecords();

    public ResourceBucket(
            String id,
            String packId,
            Date receiveDate,
            String master,
            String slave,
            String basicFamily,
            String dFamily) {
        this.id = id;
        this.packId = packId;
        this.receiveDate = receiveDate;
        this.master = master;
        this.slave = slave;
        this.basicFamily = basicFamily;
        this.dFamily = dFamily;
    }

    public String getId() {
        return id;
    }

    public String getPackId() {
        return packId;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public String getMaster() {
        return master;
    }

    public String getSlave() {
        return slave;
    }

    public String getBasicFamily() {
        return basicFamily;
    }

    public String getdFamily() {
        return dFamily;
    }

    public void insertBasicRecord(String key, String val) {
        basicRecord.put(key, val);
    }

    public String getBasicRecord(String key) {
        return basicRecord.get(key);
    }

    public Map<String, String> getMasterBasicRecords(ProfileType profileType) {
        Map<String, String> _basicRecord = new HashMap<>();
        List<String> cells = ResourceCells.getMasterBasicCell(profileType);
        cells.forEach(item -> {
            _basicRecord.put(item, basicRecord.get(item));
        });
        return _basicRecord;
    }

    public Map<String, String> getSubBasicRecords(ProfileType profileType) {
        Map<String, String> _basicRecord = new HashMap<>();
        List<String> cells = ResourceCells.getSubBasicCell(profileType);
        cells.forEach(item -> {
            _basicRecord.put(item, basicRecord.get(item));
        });
        _basicRecord.put(ResourceCells.PROFILE_ID, this.id);
        return _basicRecord;
    }

    public MasterRecord getMasterRecord() {
        return masterRecord;
    }
    public void setMasterRecord(MasterRecord masterRecord) {
        this.masterRecord = masterRecord;
    }

    public List<SubRecord> getSubRecords() {
        return subRecords;
    }

    public void setSubRecords(List<SubRecord> subRecords) {
        this.subRecords = subRecords;
    }

    public QcMetadataRecords getQcMetadataRecords() {
        return qcMetadataRecords;
    }
    public void setQcMetadataRecords(QcMetadataRecords qcMetadataRecords) {
        this.qcMetadataRecords = qcMetadataRecords;
    }

}
