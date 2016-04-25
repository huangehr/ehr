package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.DimensionCategoryDao;
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
    public void deleteDimensionCategory(String id)
    {
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
