package com.yihu.ehr.resource.service;


import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceCategoryDao;
import com.yihu.ehr.resource.dao.RsResourceDao;
import com.yihu.ehr.resource.model.RsResourceCategory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * Created by lyr on 2016/5/4.
 */
@Service
@Transactional
public class RsResourceCategoryService extends BaseJpaService<RsResourceCategory, RsResourceCategoryDao> {

    @Autowired
    private RsResourceCategoryDao rsResourcesCategoryDao;
    @Autowired
    private RsResourceDao resourcesDao;

    /**
     * 删除资源类别
     *
     * @param id 资源类别id
     */
    public void deleteRsCategory(String id) throws Exception
    {
        if(rsResourcesCategoryDao.countByPid(id) > 0)
        {
            throw new Exception("此类别包含子类别!");
        }

        if(resourcesDao.countByCategoryId(id) > 0)
        {
            throw new Exception("此类别包含资源!");
        }

        rsResourcesCategoryDao.delete(id);
    }

    /**
     * 创建或更新资源类别
     *
     * @param rsCategory 资源类别
     * @return RsCategory 资源类别
     */
    public RsResourceCategory createOrUpdRsCategory(RsResourceCategory rsCategory)
    {
        rsResourcesCategoryDao.save(rsCategory);

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
    public Page<RsResourceCategory> getRsCategories(String sorts, int page, int size)
    {
        Pageable pageable = new PageRequest(page,size,parseSorts(sorts));

        return rsResourcesCategoryDao.findAll(pageable);
    }

    /**
     * 获取资源类别
     *
     * @param id String Id
     * @return RsCategory
     */
    public RsResourceCategory getRsCategoryById(String id)
    {
        return rsResourcesCategoryDao.findOne(id);
    }

    public List<RsResourceCategory> findAll() {
        return (List<RsResourceCategory>) rsResourcesCategoryDao.findAll();
    }
    /**
     * 根据父级ID获取下级类别
     */
    public List<RsResourceCategory> getRsCategoryByPid(String pid) {
        Session session = currentSession();
        String strSql="";
        if(StringUtils.isEmpty(pid)){
            strSql += "from RsResourceCategory a where 1=1 and (a.pid is null or a.pid='')";
        }else{
            strSql += "from RsResourceCategory a where 1=1 and a.pid =:pid";
        }
        Query query = session.createQuery(strSql);
        if(!StringUtils.isEmpty(pid)){
            query.setString("pid", pid);
        }
        return query.list();
    }

    public List<RsResourceCategory> getCateTypeExcludeSelfAndChildren(String ids) {
        Session session = currentSession();
        String strSql="";
        if(!StringUtils.isEmpty(ids)){
            strSql += "from RsResourceCategory a where 1=1 and a.id not in(:ids)";
        }
        Query query = session.createQuery(strSql);
        if(!StringUtils.isEmpty(ids)){
            query.setParameterList("ids", ids.split(","));
        }
        return query.list();
    }

    public RsResourceCategory FindById(String id) {
        return rsResourcesCategoryDao.findOne(id);
    }

}
