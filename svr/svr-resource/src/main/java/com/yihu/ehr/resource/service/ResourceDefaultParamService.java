package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceDefaultParamDao;
import com.yihu.ehr.resource.model.ResourceDefaultParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class ResourceDefaultParamService extends BaseJpaService<ResourceDefaultParam, ResourceDefaultParamDao>  {

    @Autowired
    private ResourceDefaultParamDao resourceDefaultParamDao;

    public ResourceDefaultParam findById(String id) {
        return resourceDefaultParamDao.findById(id);
    }
    public List<ResourceDefaultParam> findByResourcesIdOrResourcesCodeWithParamKey(String resourcesId,String resourcesCode,String paramKey){
        return resourceDefaultParamDao.findByResourcesIdOrResourcesCodeWithParamKey(resourcesId, resourcesCode, paramKey);
    }
    public List<ResourceDefaultParam> findByResourcesIdOrResourcesCode(String resourcesId,String resourcesCode){
        return resourceDefaultParamDao.findByResourcesIdOrResourcesCode(resourcesId, resourcesCode);
    }

}
