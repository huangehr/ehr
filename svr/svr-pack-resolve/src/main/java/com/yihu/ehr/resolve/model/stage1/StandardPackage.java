package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.profile.model.ProfileId;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by progr1mmer on 2018/6/8.
 */
public class StandardPackage extends OriginalPackage {

    private String cardId; //就诊卡
    private String cardType; //就诊卡类型
    private String demographicId; //身份证号码
    private String patientName; //居民姓名
    private String deptCode; //入院科室编码
    private Set<String> diagnosisCode = new HashSet<>(); //icd10 诊断代码
    private Set<String> diagnosisName = new HashSet<>(); //诊断名称

    public StandardPackage(String packId, Date receiveDate) {
        this.packId = packId;
        this.receiveDate = receiveDate;
        this.profileType = ProfileType.Standard;
    }

    @Override
    public String getId() {
        if (profileId == null) {
            if (StringUtils.isEmpty(orgCode)) {
                throw new IllegalJsonDataException("Build profile id failed, organization code is empty.");
            }
            if (StringUtils.isEmpty(eventNo)) {
                throw new IllegalJsonDataException("Build profile id failed, eventNo is empty.");
            }
            if (eventTime == null) {
                throw new IllegalJsonDataException("Build profile id failed, unable to get event date.");
            }
            if (profileType == null ){
                throw new IllegalJsonDataException("Build profileType id failed, profileType is empty.");
            }
            this.profileId = ProfileId.get(orgCode, eventNo, eventTime, profileType.getType());
        }
        return profileId.toString();
    }

    @Override
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("id", getId().toString());
        root.put("cardId", this.getCardId());
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.getEventTime()));
        root.put("createTime", DateTimeUtil.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", this.getEventType() == null ?  "" : this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());
        root.put("cardType", this.getCardType());
        root.put("patientName", this.getPatientName());
        root.put("diagnosis", StringUtils.join(this.getDiagnosisCode(),";"));
        root.put("diagnosisName", StringUtils.join(this.getDiagnosisName(),";"));
        root.put("reUploadFlg", this.isReUploadFlg());
        root.put("identifyFlag", this.isIdentifyFlag());
        root.put("deptCode", this.deptCode);
        ObjectNode dataSetsNode = root.putObject("dataSets");
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }
        return root.toString();
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

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Set<String> getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(Set<String> diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Set<String> getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(Set<String> diagnosisName) {
        this.diagnosisName = diagnosisName;
    }
}
