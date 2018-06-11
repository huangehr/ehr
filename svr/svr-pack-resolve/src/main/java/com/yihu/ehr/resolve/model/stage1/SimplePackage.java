package com.yihu.ehr.resolve.model.stage1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.ProfileId;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 数据集档案包
 *
 * @author 张进军
 * @created 2017.06.27 11:34
 */
public class SimplePackage extends OriginalPackage {

    private String pk; // 数据集主键（可能是联合主键）
    private List<String> sqlList; // 遍历数据集拼接的插入/更新SQL语句

    public SimplePackage(String packId, Date receiveDate){
        this.packId = packId;
        this.receiveDate = receiveDate;
        this.profileType = ProfileType.Simple;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @Override
    public String getId() {
        if (profileId == null) {
            if (org.apache.commons.lang3.StringUtils.isEmpty(orgCode)) {
                throw new IllegalJsonDataException("Build profile id failed, organization code is empty.");
            }
            if (org.apache.commons.lang3.StringUtils.isEmpty(eventNo)) {
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
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.getEventTime()));
        root.put("createTime", DateTimeUtil.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", StringUtils.isEmpty(this.getEventType()) ? "" : this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());
        return root.toString();
    }

}
