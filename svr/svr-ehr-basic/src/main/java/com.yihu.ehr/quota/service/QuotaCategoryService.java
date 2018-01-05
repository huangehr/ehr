package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.report.QuotaCategory;
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
public class QuotaCategoryService extends BaseJpaService<QuotaCategory, XQuotaCategoryRepository> {
    @Autowired
    private XQuotaCategoryRepository quotaCategoryRepository;

    public List<QuotaCategory> getAllQuotaCategory(){
        return quotaCategoryRepository.getAllQuotaCategory();
    }

    public List<QuotaCategory> searchByParentId(Integer parentId) {
        return quotaCategoryRepository.searchByParentId(parentId);
    }

    public QuotaCategory getById(Integer id) {
        return quotaCategoryRepository.findOne(id);
    }

    public void deleteQuotaCategory(Integer id) {
        quotaCategoryRepository.delete(id);
    }

    public int getCountByName(String name) {
        List<QuotaCategory> list = quotaCategoryRepository.searchByName(name);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public int getCountByCode(String code) {
        List<QuotaCategory> list = quotaCategoryRepository.searchByCode(code);
        if (list != null && !list.isEmpty()){
            return list.size();
        }else {
            return 0;
        }
    }

    public QuotaCategory saveQuotaCategory(QuotaCategory quotaCategory) {
        quotaCategoryRepository.save(quotaCategory);
        return quotaCategory;
    }

    public QuotaCategory updateQuotaCategory(QuotaCategory quotaCategory) {
        quotaCategory = quotaCategoryRepository.save(quotaCategory);
        return quotaCategory;
    }

    public List<QuotaCategory> getQuotaCategoryOfChild(){
        return quotaCategoryRepository.getQuotaCategoryOfChild();
    }

    public List<QuotaCategory> getQuotaCategoryChild(){
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
