package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.profile.model.ProfileId;
import com.yihu.ehr.resolve.model.stage1.details.LinkFile;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 轻量级健康档案。其数据集保存的是机构健康档案中的链接。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
@Table(ResourceCore.MasterTable)
public class LinkPackage extends OriginalPackage {

    private String patientName; //居民姓名
    private String demographicId; //身份证号码
    private String visitType;
    private Date expireDate;
    private List<LinkFile> linkFiles = new ArrayList<>();

    public LinkPackage(String packId, Date receiveDate){
        this.packId = packId;
        this.receiveDate = receiveDate;
        this.profileType = ProfileType.Link;
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
        root.put("id", getId());
        root.put("orgCode", this.orgCode);
        root.put("patientId", this.patientId);
        root.put("eventNo", this.eventNo);
        root.put("cdaVersion", this.cdaVersion);
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.eventTime));
        root.put("eventType", this.eventType == null ?  "" : this.eventType.toString());
        root.put("profileType", this.profileType.toString());
        root.put("patientName", this.patientName);
        root.put("demographicId", this.demographicId);
        root.put("reUploadFlg", this.reUploadFlg);
        root.put("identifyFlag", this.identifyFlag);
        root.put("visitType", this.visitType);
        root.put("expireDate", DateTimeUtil.utcDateTimeFormat(this.expireDate));
        ObjectNode dataSetsNode = root.putObject("dataSets");
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }
        ArrayNode files = root.putArray("files");
        for (LinkFile linkFile : linkFiles) {
            files.add(linkFile.toJson());
        }
        return root.toString();
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

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<LinkFile> getLinkFiles() {
        return linkFiles;
    }

    public void setLinkFiles(List<LinkFile> linkFiles) {
        this.linkFiles = linkFiles;
    }
}
