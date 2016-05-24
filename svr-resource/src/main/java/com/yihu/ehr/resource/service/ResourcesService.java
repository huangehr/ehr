package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceMetadataDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.RsResource;
import com.yihu.ehr.resource.service.intf.IResourcesService;
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
public class ResourcesService extends BaseJpaService<RsResource, ResourcesDao> implements IResourcesService {
    @Autowired
    private ResourcesDao rsDao;
    @Autowired
    private ResourceMetadataDao rsMetadataDao;

    /**
     * 资源创建
     *
     * @param resource 资源实体
     * @return RsResource 资源实体
     */
    public RsResource saveResource(RsResource resource)
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
            rsMetadataDao.deleteByResourcesId(id_);
            rsDao.delete(id_);
        }

        rsDao.delete(id);
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResource> 资源
     */
    public Page<RsResource> getResources(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return rsDao.findAll(pageable);
    }

    public List<RsResource> findByCategoryId(String CategoryId){
        return rsDao.findByCategoryId(CategoryId);
    }

    /**
     * 根据ID获取资源
     *
     * @param id String Id
     * @return RsResource
     */
    public RsResource getResourceById(String id)
    {
        return rsDao.findOne(id);
    }

}
