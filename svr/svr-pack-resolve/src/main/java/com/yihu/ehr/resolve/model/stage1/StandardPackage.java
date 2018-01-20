package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.annotation.Column;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.MasterResourceFamily;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.profile.util.ProfileId;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 在内存中构建一个标准档案包结构，将档案包的数据解析进去之后，再使用资源化工具入库。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
@Table(ResourceCore.MasterTable)
public class StandardPackage {

    protected ObjectMapper objectMapper =  SpringContext.getApplicationContext().getBean(ObjectMapper.class);

    //档案ID
    private ProfileId profileId;
    //1结构化档案，2文件档案，3链接档案，4数据集档案
    private ProfileType profileType;
    //事件号
    private String eventNo;
    //事件时间，如挂号，住院，体检等时间
    private Date eventDate;
    // 0门诊 1住院 2体检
    private EventType eventType;
    //就诊时用的就诊卡
    private String cardId;
    //就诊时的就诊卡类型
    private String cardType;
    //人口学ID
    private String patientId;
    //患者姓名
    private String patientName;
    //身份证号码
    private String demographicId;
    //机构代码
    private String orgCode;
    //CDA版本号
    private String cdaVersion;
    //入库创建时间
    private Date createDate;
    //应用代码
    private String clientId;
    //ICD10诊断列表
    private List<String> diagnosisList;
    //重传标识
    private boolean reUploadFlg;
    //数据集合
    protected Map<String, PackageDataSet> dataSets = new TreeMap<>();

    public StandardPackage() {
        cardId = "";
        orgCode = "";
        patientId = "";
        eventNo = "";
        setProfileType(ProfileType.Standard);
    }

    /**
     * ID
     * @return
     */
    public String getId() {
        if (profileId == null) {
            if (StringUtils.isEmpty(orgCode)) {
                throw new IllegalArgumentException("Build profile id failed, organization code is empty.");
            }
            if (StringUtils.isEmpty(eventNo)) {
                throw new IllegalArgumentException("Build profile id failed, eventNo is empty.");
            }
            if (eventDate == null) {
                throw new IllegalArgumentException("Build profile id failed, unable to get event date.");
            }
            this.profileId = ProfileId.get(orgCode, eventNo, eventDate);
        }
        return profileId.toString();
    }
    public void setId(String id) {
        this.profileId = new ProfileId(id);
    }


    /**
     * 1结构化档案，2文件档案，3链接档案，4数据集档案
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.ProfileType, family = MasterResourceFamily.Basic)
    public ProfileType getProfileType() {
        return profileType;
    }
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    /**
     * 事件号
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.EventNo, family = MasterResourceFamily.Basic)
    public String getEventNo() {
        return eventNo;
    }
    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    /**
     * 事件时间，如挂号，住院，体检等时间
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.EventDate, family = MasterResourceFamily.Basic)
    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date date) {
        this.eventDate = date;
    }

    /**
     * 0门诊 1住院 2体检
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.EventType, family = MasterResourceFamily.Basic)
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * 就诊时用的就诊卡
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.CardId, family = MasterResourceFamily.Basic)
    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    /**
     * 就诊时的就诊卡类型
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.CardType, family = MasterResourceFamily.Basic)
    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * 人口学ID
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.PatientId, family = MasterResourceFamily.Basic)
    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * 患者姓名
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.PatientName, family = MasterResourceFamily.Basic)
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * 身份证号码
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.DemographicId, family = MasterResourceFamily.Basic)
    public String getDemographicId() {
        return demographicId;
    }
    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    /**
     * 机构代码
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.OrgCode, family = MasterResourceFamily.Basic)
    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * CDA版本
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.CdaVersion, family = MasterResourceFamily.Basic)
    public String getCdaVersion() {
        return cdaVersion;
    }
    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    /**
     * 入库创建时间
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.CreateDate, family = MasterResourceFamily.Basic)
    public Date getCreateDate() {
        if (createDate == null){
            createDate = new Date();
        }
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 应用代码ID
     * @return
     */
    @Column(value = MasterResourceFamily.BasicColumns.ClientId, family = MasterResourceFamily.Basic)
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isReUploadFlg() {
        return reUploadFlg;
    }

    public void setReUploadFlg(boolean reUploadFlg) {
        this.reUploadFlg = reUploadFlg;
    }

    /**
     * ICD10诊断列表
     * @return
     */
    public List<String> getDiagnosisList() {
        return diagnosisList;
    }
    public void setDiagnosisList(List<String> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    /**
     * 数据集合
     *
     */
    public void insertDataSet(String dataSetCode, PackageDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }
    public PackageDataSet getDataSet(String dataSetCode) {
        return this.dataSets.get(dataSetCode);
    }
    public Collection<PackageDataSet> getDataSets() {
        return dataSets.values();
    }

    public String getDataSetIndices() {
        ObjectNode rootNode = objectMapper.createObjectNode();

        for (String dataSetCode : dataSets.keySet()) {
            Set<String> recordKeys = dataSets.get(dataSetCode).getRecordKeys();
            rootNode.put(dataSetCode, String.join(",", recordKeys));
        }

        return rootNode.toString();
    }

    public String toJson() {
        ObjectNode node = jsonFormat();
        return node.toString();
    }

    protected ObjectNode jsonFormat() {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("cardId", this.getCardId());
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("clientId", this.getClientId());
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.getEventDate()));
        root.put("createTime", DateTimeUtil.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", this.getEventType() == null? "":this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());
        root.put("cardType", this.getCardType());
        root.put("patientName", this.getPatientName());
        root.put("diagnosis", StringUtils.join(this.getDiagnosisList(),";"));
        root.put("reUploadFlg", this.isReUploadFlg());
        ObjectNode dataSetsNode = root.putObject("dataSets");
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }
        return root;
    }

    public void regularRowKey() {
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);
            int rowIndex = 0;
            String sortFormat = dataSet.getRecordCount() > 10 ? "%s$%03d" : "%s$%1d";
            String[] rowKeys = dataSet.getRecordKeys().toArray(new String[dataSet.getRecordCount()]);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, String.format(sortFormat, getId(), rowIndex ++));
            }
        }
    }

    //非档案类型 rowKey获取
    public String getNonArchiveProfileId(String dataSetCode) {
        if (profileId == null) {
            if (StringUtils.isEmpty(orgCode)) {
                throw new IllegalArgumentException("Build profile id failed, organization code is empty.");
            }

            if (StringUtils.isEmpty(eventNo) && !"HDSA00_01".equals(dataSetCode)) {
                throw new IllegalArgumentException("Build profile id failed, eventNo is empty.");
            }

            if (StringUtils.isEmpty(patientId) ) {
                throw new IllegalArgumentException("Build profile id failed, patientId is empty.\"");
            }

            this.profileId = ProfileId.get(orgCode, patientId, eventNo);
        }

        return profileId.toString();
    }

    //非档案类型rowKey更新
    public void regularNonArchiveRowKey() {
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);

            int rowIndex = 0;
            String sortFormat = dataSet.getRecordCount() > 10 ? "%s$%03d" : "%s$%1d";
            String[] rowKeys = dataSet.getRecordKeys().toArray(new String[dataSet.getRecordCount()]);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, String.format(sortFormat, getNonArchiveProfileId(dataSetCode), rowIndex++));
            }
        }
    }

}
