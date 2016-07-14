package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.MRResourcesDao;
import com.yihu.ehr.medicalRecord.model.MRRsResources;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Guo Yanshan on 2016/7/12.
 */
@Service
@Transactional
public class MRResourcesService extends BaseJpaService<MRRsResources, MRResourcesDao> {
    @Autowired
    private MRResourcesDao rsDao;

    /**
     * 资源创建
     *
     * @param resource 资源实体
     * @return RsResources 资源实体
     */
    public MRRsResources saveResource(MRRsResources resource)
    {
        return rsDao.save(resource);
    }

    /**
     * 资源创建
     *
     * @param id 资源实体
     * @return RsResources 资源实体
     */
    public MRRsResources getInfoById(String id)
    {
        return rsDao.findOne(id);
    }

}
