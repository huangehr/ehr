package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.profile.model.ProfileId;
import com.yihu.ehr.resolve.model.stage1.details.CdaDocument;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 文件型健康档案（非结构化）。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class FilePackage extends OriginalPackage {

    private String demographicId; //身份证号码

    public FilePackage(String packId, Date receiveDate) {
        this.packId = packId;
        this.receiveDate = receiveDate;
        this.profileType = ProfileType.File;
    }

    // 文档列表，Key为数据库主键
    private Map<String, CdaDocument> cdaDocuments = new TreeMap<>();

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }

    public Map<String, CdaDocument> getCdaDocuments() {
        return cdaDocuments;
    }

    public String getFileIndices(){
        return String.join(";", cdaDocuments.keySet());
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
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.getEventTime()));
        root.put("createTime", DateTimeUtil.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", this.getEventType() == null ? "" : this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());
        root.put("reUploadFlg", this.isReUploadFlg());
        root.put("identifyFlag", this.isIdentifyFlag());
        ObjectNode dataSetsNode = root.putObject("dataSets");
        for (String dataSetCode : dataSets.keySet()) {
            PackageDataSet dataSet = dataSets.get(dataSetCode);
            dataSetsNode.putPOJO(dataSetCode, dataSet.toJson());
        }
        if (cdaDocuments != null) {
            ArrayNode files = root.putArray("documents");
            for (String key : cdaDocuments.keySet()) {
                files.add(cdaDocuments.get(key).toJson());
            }
        }
        return root.toString();
    }

    @Override
    public void regularRowKey() {
        super.regularRowKey();
        int i = 0;
        Set<String> rowkeys = new HashSet<>(cdaDocuments.keySet());
        for (String rowkey : rowkeys){
            CdaDocument document = cdaDocuments.remove(rowkey);
            cdaDocuments.put(getId() + "$" + i++, document);
        }
    }

}
