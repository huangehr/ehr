package com.yihu.ehr.service.resource.stage1;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.util.datetime.DateTimeUtil;

import java.util.List;

/**
 * 数据集档案包
 *
 * @author 张进军
 * @created 2017.06.27 11:34
 */
@Table(ResourceCore.MasterTable)
public class DatasetPackage extends StandardPackage {

    private List<String> sqlList; // 遍历数据集拼接的插入/更新SQL语句

    public DatasetPackage(){
        setProfileType(ProfileType.Dataset);
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    @Override
    public String toJson() {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("cardId", this.getCardId());
        root.put("orgCode", this.getOrgCode());
        root.put("patientId", this.getPatientId());
        root.put("eventNo", this.getEventNo());
        root.put("cdaVersion", this.getCdaVersion());
        root.put("clientId", this.getClientId());
        root.put("eventTime", DateTimeUtil.utcDateTimeFormat(this.getEventDate()));
        root.put("createTime", DateTimeUtil.utcDateTimeFormat(this.getCreateDate()));
        root.put("eventType", this.getEventType().toString());
        root.put("profileType", this.getProfileType().toString());
        root.put("cardType", this.getCardType());
        root.put("patientName", this.getPatientName());
        return root.toString();
    }

}
