package com.yihu.ehr.service.resource.stage1;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.annotation.Table;
import com.yihu.ehr.profile.core.ResourceCore;

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
}
