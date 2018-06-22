package com.yihu.ehr.resolve.model.stage1;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.profile.model.ProfileId;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by progr1mmer on 2018/6/8.
 */
public abstract class OriginalPackage {

    protected String packId;
    protected Date receiveDate;
    protected ProfileType profileType;
    protected ProfileId profileId;
    protected String cdaVersion;
    protected String patientId;
    protected String eventNo;
    protected Date eventTime;
    protected EventType eventType;
    protected String orgCode;
    protected Date createDate;
    protected boolean reUploadFlg;
    //身份识别标志
    protected boolean identifyFlag;
    //原始数据集
    protected Map<String, PackageDataSet> dataSets = new TreeMap<>();

    public String getPackId() {
        return packId;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public void setProfileId(ProfileId profileId) {
        this.profileId = profileId;
    }

    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isReUploadFlg() {
        return reUploadFlg;
    }

    public void setReUploadFlg(boolean reUploadFlg) {
        this.reUploadFlg = reUploadFlg;
    }

    public boolean isIdentifyFlag() {
        return identifyFlag;
    }

    public void setIdentifyFlag(boolean identifyFlag) {
        this.identifyFlag = identifyFlag;
    }

    public void insertDataSet(String dataSetCode, PackageDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public PackageDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }

    public Map<String, PackageDataSet> getDataSets() {
        return dataSets;
    }

    public abstract String getId();
    public abstract String toJson();

    public void regularRowKey() {
        dataSets.forEach((key, val) -> {
            int rowIndex = 0;
            String sortFormat = val.getRecordCount() > 10 ? "%s$%03d" : "%s$%1d";
            String[] rowKeys = val.getRecordKeys().toArray(new String[val.getRecordCount()]);
            for (String rowKey : rowKeys) {
                val.updateRecordKey(rowKey, String.format(sortFormat, getId(), rowIndex ++));
            }
        });
    }
}
