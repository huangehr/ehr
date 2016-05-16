package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.DimensionCategoryDao;
import com.yihu.ehr.resource.dao.intf.DimensionDao;
import com.yihu.ehr.resource.model.RsDimensionCategory;
import com.yihu.ehr.resource.service.intf.IDimensionCategoryService;
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
public class DimensionCategoryService extends BaseJpaService<RsDimensionCategory,DimensionCategoryDao> implements IDimensionCategoryService{
    @Autowired
    private DimensionCategoryDao dmcDao;
    @Autowired
    private DimensionDao dimensionDao;

    /*
     *维度类别创建
     *
     * @param resource 维度类别实体
     * @return RsResources 维度类别实体
     */
    public RsDimensionCategory createDimensionCategory(RsDimensionCategory dimensionCategory)
    {
        dmcDao.save(dimensionCategory);
        return dimensionCategory;
    }

    /*
     *维度类别更新
     *
     * @param resource 维度类别实体
     */
    public void updateDimensionCategory(RsDimensionCategory dimensionCategory)
    {
        dmcDao.save(dimensionCategory);
    }

    /*
     *维度类别删除
     *
     * @param id 维度类别ID
     */
    public void deleteDimensionCategory(String id) throws Exception
    {
        if(dimensionDao.countByCategoryId(id) > 0)
        {
            throw new Exception("该维度类别包含维度数据！");
        }

        if(dmcDao.countByPid(id) > 0)
        {
            throw new Exception("该维度类别包含子类别！");
        }

        dmcDao.delete(id);
    }

    /*
     *维度类别获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 维度类别
     */
    public Page<RsDimensionCategory> getDimensionCategories(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return dmcDao.findAll(pageable);
    }
}
