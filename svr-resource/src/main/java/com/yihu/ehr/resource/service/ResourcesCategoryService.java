package com.yihu.ehr.resource.service;


import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourcesCategoryDao;
import com.yihu.ehr.resource.dao.intf.ResourcesDao;
import com.yihu.ehr.resource.model.RsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by lyr on 2016/5/4.
 */
@Service
@Transactional
public class ResourcesCategoryService extends BaseJpaService<RsCategory,ResourcesCategoryDao> {

    @Autowired
    private ResourcesCategoryDao rsCategoryDao;
    @Autowired
    private ResourcesDao rsDao;

    /**
     * 删除资源类别
     *
     * @param id 资源类别id
     */
    public void deleteRsCategory(String id) throws Exception
    {
        if(rsCategoryDao.countByPid(id) > 0)
        {
            throw new Exception("此类别包含子类别!");
        }

        if(rsDao.countByCategoryId(id) > 0)
        {
            throw new Exception("此类别包含资源!");
        }

        rsCategoryDao.delete(id);
    }

    /**
     * 创建或更新资源类别
     *
     * @param rsCategory 资源类别
     * @return RsCategory 资源类别
     */
    public RsCategory createOrUpdRsCategory(RsCategory rsCategory)
    {
        rsCategoryDao.save(rsCategory);

        return rsCategory;
    }

    /**
     * 获取资源类别
     *
     * @param sorts String 排序
     * @param page int 分页
     * @param size int 分页大小
     * @return Page<RsCategory>
     */
    public Page<RsCategory> getRsCategories(String sorts, int page, int size)
    {
        Pageable pageable = new PageRequest(page,size,parseSorts(sorts));

        return rsCategoryDao.findAll(pageable);
    }

    /**
     * 获取资源类别
     *
     * @param id String Id
     * @return RsCategory
     */
    public RsCategory getRsCategoryById(String id)
    {
        return rsCategoryDao.findOne(id);
    }

    public List<RsCategory> getRsCategoryByPid(String pid) {
        return rsCategoryDao.findByPid(pid);
    }

    public List<RsCategory> findAll() {
        return (List<RsCategory>) rsCategoryDao.findAll();
    }
}
