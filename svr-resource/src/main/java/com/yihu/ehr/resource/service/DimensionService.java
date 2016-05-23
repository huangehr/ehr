package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.DimensionDao;
import com.yihu.ehr.resource.model.RsDimension;
import com.yihu.ehr.resource.service.intf.IDimensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lyr on 2016/4/25.
 */
@Service
@Transactional
public class DimensionService extends BaseJpaService<RsDimension,DimensionDao>  implements IDimensionService{
    @Autowired
    private DimensionDao dimensionDao;

    /**
     * 资源创建
     *
     * @param dimension RsDimension 资源实体
     * @return RsResource 资源实体
     */
    public RsDimension saveDimension(RsDimension dimension)
    {
        dimensionDao.save(dimension);
        return dimension;
    }

    /**
     * 资源删除
     *
     * @param id 资源ID
     */
    public void deleteDimension(String id)
    {
        dimensionDao.delete(id);
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResource> 资源
     */
    public Page<RsDimension> getDimensions(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return dimensionDao.findAll(pageable);
    }
}
