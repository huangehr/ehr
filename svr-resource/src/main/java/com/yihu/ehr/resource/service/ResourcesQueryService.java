package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.resource.dao.ResourcesDao;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.model.ResourcesType;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.intf.IResourcesQueryService;
import jdk.management.resource.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by hzp on 2016/4/13.
 */
@Service("resourcesQueryService")
public class ResourcesQueryService implements IResourcesQueryService {

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private ResourcesQueryDao resourcesQueryDao;

    /**
     * 获取资源
     * @param resourcesCode
     * @param appId
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    public DataList getResources(String resourcesCode,String appId,String queryParams,Integer page,Integer size) throws Exception {
        //获取资源信息
        RsResources rs = resourcesDao.findByCode(resourcesCode);
        if(true)
        {
            //资源结构/权限


            //资源类型
            String type= "1";
            if(type.equals(ResourcesType.HBASE_SINGLE_CORE)) //habse单core
            {
                return resourcesQueryDao.getHbaseSingleCore("core", "metadata", queryParams, page, size);
            }
            else if(type.equals(ResourcesType.HBASE_MULTI_CORE)) //habse多core
            {
                return resourcesQueryDao.getHbaseSingleCore("core", "metadata", queryParams, page, size);
            }
            else if(type.equals(ResourcesType.HBASE_SOLR)) //habse的solr分组统计
            {
                return resourcesQueryDao.getHbaseSolr("core", "metadata", queryParams, page, size);
            }
            else if(type.equals(ResourcesType.RS_ETL)) //ETL数据
            {
                return resourcesQueryDao.getEtlData("datasource", "core", "metadata", queryParams, page, size);
            }
            else if(type.equals(ResourcesType.RS_CONFIG)) //配置数据
            {
                return resourcesQueryDao.getConfigData("datasource", "core", "metadata", queryParams, page, size);
            }
            else if(type.equals(ResourcesType.RS_DICT)) //字典数据
            {
                return resourcesQueryDao.getDictData("dictName");
            }
        }

        return null;
    }



}
