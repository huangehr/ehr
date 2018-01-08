package com.yihu.ehr.basic.quota.service;

import com.yihu.ehr.basic.quota.dao.XTjQuotaCategoryRepository;
import com.yihu.ehr.entity.quota.TjQuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XQuotaCategoryRepository;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxw on 2017/8/31.
 */
@Service
@Transactional
public class TjQuotaCategoryService extends BaseJpaService<TjQuotaCategory, XTjQuotaCategoryRepository> {
    @Autowired
    private XTjQuotaCategoryRepository quotaCategoryRepository;

    public List<TjQuotaCategory> getAllQuotaCategory(){
        return quotaCategoryRepository.getAllQuotaCategory();
    }

    public List<TjQuotaCategory> searchByParentId(Integer parentId) {
        return quotaCategoryRepository.searchByParentId(parentId);
    }

    public TjQuotaCategory getById(Integer id) {
        return quotaCategoryRepository.findOne(id);
    }

    public void deleteQuotaCategory(Integer id) {
        quotaCategoryRepository.delete(id);
    }

    public int getCountByName(String name) {
        List<TjQuotaCategory> list = quotaCategoryRepository.searchByName(name);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public int getCountByCode(String code) {
        List<TjQuotaCategory> list = quotaCategoryRepository.searchByCode(code);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public TjQuotaCategory saveQuotaCategory(TjQuotaCategory quotaCategory) {
        quotaCategoryRepository.save(quotaCategory);
        return quotaCategory;
    }

    public TjQuotaCategory updateQuotaCategory(TjQuotaCategory quotaCategory) {
        quotaCategory = quotaCategoryRepository.save(quotaCategory);
        return quotaCategory;
    }

    public List<TjQuotaCategory> getQuotaCategoryOfChild(){
        return quotaCategoryRepository.getQuotaCategoryOfChild();
    }

    public List<TjQuotaCategory> getQuotaCategoryChild(){
        return quotaCategoryRepository.getQuotaCategoryChild();
    }

    /**
     * 查询指标分类是否已存在， 返回已存在指标分类id、name
     */
    public List getQuotaCategoryByName(String[] names)
    {
        String sql = "SELECT name, id FROM tj_quota_category WHERE name in(:names)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("names", names);
        return sqlQuery.list();
    }
}
