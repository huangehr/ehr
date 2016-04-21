package com.yihu.ehr.resource.dao;


import com.yihu.ehr.resource.model.RsResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by hzp on 2016/4/19.
 */
@Service("resourcesDao")
public class ResourcesDao extends BaseDao {


    /**
     * 根据资源分类获取资源列表
     */
    public List<RsResources> getResourcesByCategory(String category) throws Exception
    {
        String sql = "select * from RS_RESOURCES where CATEGORY_ID=?";
        return this.getDB().query(RsResources.class,sql,category);
    }
}
