package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceMetadataDao;
import com.yihu.ehr.resource.dao.RsResourceDao;
import com.yihu.ehr.resource.dao.RsResourceDefaultQueryDao;
import com.yihu.ehr.resource.model.RsResource;
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
public class RsResourceService extends BaseJpaService<RsResource, RsResourceDao> {

    @Autowired
    private RsResourceDao rsDao;
    @Autowired
    private RsResourceMetadataDao rsMetadataDao;
    @Autowired
    private RsResourceDefaultQueryDao resourcesDefaultQueryDao;

    /**
     * 资源创建
     * @param resource 资源实体
     * @return RsResources 资源实体
     */
    public RsResource saveResource(RsResource resource) {
        return rsDao.save(resource);
    }

    /**
     * 资源删除
     *
     * @param id 资源ID
     */
    public void deleteResource(String id) {
        String[] ids = id.split(",");
        for(String id_ : ids) {
            resourcesDefaultQueryDao.deleteByResourcesId(id_);
            rsMetadataDao.deleteByResourcesId(id_);
            rsDao.delete(id_);
        }
    }

    /**
     * 资源获取
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsResource> getResources(String sorts, int page, int size) {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));
        return rsDao.findAll(pageable);
    }

    /**
     * 根据资源分类获取资源
     * @param CategoryId
     * @return
     */
    public List<RsResource> findByCategoryId(String CategoryId){
        return rsDao.findByCategoryId(CategoryId);
    }

    /**
     * 根据ID获取资源
     *
     * @param id String Id
     * @return RsResources
     */
    public RsResource getResourceById(String id)
    {
        return rsDao.findOne(id);
    }

    /**
     * 根据code获取资源
     * @param code
     * @return
     */
    public RsResource getResourceByCode(String code) {
        return rsDao.findByCode(code);
    }

}
