package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceDefaultQueryDao;
import com.yihu.ehr.resource.model.RsResourceDefaultQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Sxy on 2017/08/04.
 */
@Service
@Transactional
public class RsResourceDefaultQueryService extends BaseJpaService<RsResourceDefaultQuery, RsResourceDefaultQueryDao> {

    @Autowired
    private RsResourceDefaultQueryDao rsQueryDao;

    /**
     * 资源查询条件创建
     * @param resourcesQuery 资源查询实体
     * @return RsResourcesQuery 资源查询实体
     */
    public RsResourceDefaultQuery saveResourceQuery(RsResourceDefaultQuery resourcesQuery) {
        return rsQueryDao.save(resourcesQuery);
    }

    /**
     * 资源查询条件删除
     *
     * @param id 资源ID
     */
    public void deleteResourceQuery(String id) {
        String[] ids = id.split(",");
        for(String id_ : ids) {
            rsQueryDao.delete(id_);
        }
    }

    /**
     * 根据ID获取自定义资源查询条件
     *
     * @param id String Id
     * @return RsResources
     */
    public RsResourceDefaultQuery getResourceQueryById(String id)
    {
        return rsQueryDao.findOne(id);
    }

    /**
     * 通过资源ID获取查询条件
     * @param resourcesId
     * @return
     */
    public RsResourceDefaultQuery findByResourcesId(String resourcesId){
        return rsQueryDao.findByResourcesId(resourcesId);
    }

    /**
     * 资源查询条件获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResourcesQuery> 资源
     */
    public Page<RsResourceDefaultQuery> getResourcesQuery(String sorts, int page, int size) {
        Pageable pageable =  new PageRequest(page, size, parseSorts(sorts));
        return rsQueryDao.findAll(pageable);
    }

    /**
     * 根据资源ID删除查询条件
     * @param resourceId
     */
    public void deleteByResourcesId(String resourceId) {
        rsQueryDao.deleteByResourcesId(resourceId);
    }

}
