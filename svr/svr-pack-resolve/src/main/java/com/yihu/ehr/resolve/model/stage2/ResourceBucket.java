package com.yihu.ehr.resolve.model.stage2;

import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.resolve.model.stage1.CdaDocument;
import com.yihu.ehr.resolve.model.stage1.LinkFile;

import java.util.*;

/**
 * 健康档案资源化临时存储工具。此阶段也是存在于内存中，资源化之后会存入hbase。
 *
 * @author Sand
 * @created 2016.05.16 13:52
 */
public class ResourceBucket {

    //档案ID
    private String id;
    //机构代码
    private String orgCode;
    //机构名称
    private String orgName;
    //机构区域
    private String orgArea;
    //cda版本
    private String cdaVersion;
    //应用代码
    private String clientId;
    //事件号
    private String eventNo;
    //事件时间，如挂号，住院，体检等时间
    private Date eventDate;
    //0门诊 1住院 2体检
    private EventType eventType;
    //就诊时用的就诊卡ID
    private String cardId;
    //就诊时用的就诊卡类型
    private String cardType;
    //人口学ID
    private String patientId;
    //患者姓名
    private String patientName;
    //身份证号
    private String demographicId;
    //1结构化档案，2文件档案，3链接档案
    //科室代码
    private String deptCode;
    //档案类型
    private ProfileType profileType;
    //ICD10诊断，分号分隔
    private String diagnosis;
    //ICD10诊断名称, 分号分隔
    private String diagnosisName;
    //健康问题，分号分隔
    private String healthProblem;
    //健康问题名称，分号分隔
    private String healthProblemName;
    //重传标识
    private boolean reUploadFlg;
    //身份识别标志
    private boolean identifyFlag;
    //主记录
    private MasterRecord masterRecord;
    //子记录
    private SubRecords subRecords;
    //质控数据
    private QcMetadataRecords qcMetadataRecords;
    private List<LinkFile> linkFiles = new ArrayList<>();
    // CDA文档列表，Key为HBase rowkey
    private Map<String, CdaDocument> cdaDocuments = new TreeMap<>();

    public ResourceBucket() {
        masterRecord = new MasterRecord();
        subRecords = new SubRecords();
        qcMetadataRecords = new QcMetadataRecords();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgArea() {
        return orgArea;
    }
    public void setOrgArea(String orgArea) {
        this.orgArea = orgArea;
    }

    public String getCdaVersion() {
        return cdaVersion;
    }
    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEventNo() {
        return eventNo;
    }
    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDemographicId() {
        return demographicId;
    }
    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getDeptCode() {
        return deptCode;
    }
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public ProfileType getProfileType() {
        return profileType;
    }
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }
    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getHealthProblem() {
        return healthProblem;
    }
    public void setHealthProblem(String healthProblem) {
        this.healthProblem = healthProblem;
    }

    public String getHealthProblemName() {
        return healthProblemName;
    }
    public void setHealthProblemName(String healthProblemName) {
        this.healthProblemName = healthProblemName;
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

    public MasterRecord getMasterRecord() {
        return masterRecord;
    }
    public void setMasterRecord(MasterRecord masterRecord) {
        this.masterRecord = masterRecord;
    }

    public SubRecords getSubRecords() {
        return subRecords;
    }
    public void setSubRecords(SubRecords subRecords) {
        this.subRecords = subRecords;
    }

    public QcMetadataRecords getQcMetadataRecords() {
        return qcMetadataRecords;
    }
    public void setQcMetadataRecords(QcMetadataRecords qcMetadataRecords) {
        this.qcMetadataRecords = qcMetadataRecords;
    }

    public Map<String, CdaDocument> getCdaDocuments() {
        return cdaDocuments;
    }
    public void setCdaDocuments(Map<String, CdaDocument> cdaDocuments) {
        this.cdaDocuments = cdaDocuments;
    }

    public List<LinkFile> getLinkFiles() {
        return linkFiles;
    }
    public void setLinkFiles(List<LinkFile> linkFiles) {
        this.linkFiles = linkFiles;
    }

}
