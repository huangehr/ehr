package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.report.QuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XQuotaCategoryRepository;
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
}
