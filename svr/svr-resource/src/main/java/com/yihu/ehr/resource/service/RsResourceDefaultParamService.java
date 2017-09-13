package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceDefaultParamDao;
import com.yihu.ehr.resource.model.RsResourceDefaultParam;
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
public class RsResourceDefaultParamService extends BaseJpaService<RsResourceDefaultParam, RsResourceDefaultParamDao>  {

    @Autowired
    private RsResourceDefaultParamDao resourceDefaultParamDao;

    public RsResourceDefaultParam findById(String id) {
        return resourceDefaultParamDao.findById(id);
    }
    public List<RsResourceDefaultParam> findByResourcesIdOrResourcesCodeWithParamKey(String resourcesId, String resourcesCode, String paramKey){
        return resourceDefaultParamDao.findByResourcesIdOrResourcesCodeWithParamKey(resourcesId, resourcesCode, paramKey);
    }
    public List<RsResourceDefaultParam> findByResourcesIdOrResourcesCode(String resourcesId, String resourcesCode){
        return resourceDefaultParamDao.findByResourcesIdOrResourcesCode(resourcesId, resourcesCode);
    }

}
