package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDefaultQueryDao;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.query.ResourcesQueryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by lyr on 2016/4/25.
 */
@Service
@Transactional
public class ResourcesService extends BaseJpaService<RsResources, ResourcesDao> {
    @Autowired
    private ResourcesDao rsDao;
    @Autowired
    private ResourceMetadataDao rsMetadataDao;
    @Autowired
    private ResourcesDefaultQueryDao resourcesDefaultQueryDao;

    /**
     * 资源创建
     *
     * @param resource 资源实体
     * @return RsResources 资源实体
     */
    public RsResources saveResource(RsResources resource)
    {
        return rsDao.save(resource);
    }

    /**
     * 资源删除
     *
     * @param id 资源ID
     */
    public void deleteResource(String id)
    {
        String[] ids = id.split(",");

        for(String id_ : ids)
        {
            resourcesDefaultQueryDao.deleteByResourcesId(id_);
            rsMetadataDao.deleteByResourcesId(id_);
            rsDao.delete(id_);
        }
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsResources> getResources(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return rsDao.findAll(pageable);
    }

    public List<RsResources> findByCategoryId(String CategoryId){
        return rsDao.findByCategoryId(CategoryId);
    }

    /**
     * 根据ID获取资源
     *
     * @param id String Id
     * @return RsResources
     */
    public RsResources getResourceById(String id)
    {
        return rsDao.findOne(id);
    }

    /**
     * 根据code获取资源
     * @param code
     * @return
     */
    public RsResources getResourceByCode(String code) {
        return rsDao.findByCode(code);
    }

}
