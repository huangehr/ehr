package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.basic.government.dao.GovQuotaCategoryDao;
import com.yihu.ehr.basic.government.dao.GovQuotaVisibilityDao;
import com.yihu.ehr.basic.government.entity.GovQuotaCategory;
import com.yihu.ehr.basic.government.entity.GovQuotaVisibility;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 政府服务平台首页指标展示管理 Service
 *
 * @author 张进军
 * @date 2018/7/5 17:48
 */
@Service
public class GovQuotaVisibilityService extends BaseJpaService<GovQuotaVisibility, GovQuotaVisibilityDao> {

    @Autowired
    private GovQuotaVisibilityDao govQuotaVisibilityDao;
    @Autowired
    private GovQuotaCategoryDao govQuotaCategoryDao;

    public GovQuotaVisibility getById(Integer id) {
        return govQuotaVisibilityDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public GovQuotaVisibility save(GovQuotaVisibility govQuotaVisibility) {
        return govQuotaVisibilityDao.save(govQuotaVisibility);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        govQuotaVisibilityDao.delete(id);
    }

    public Boolean isUniqueCode(Integer id, String code) {
        GovQuotaVisibility govQuotaVisibility = govQuotaVisibilityDao.isUniqueCode(id, code);
        if (govQuotaVisibility == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueName(Integer id, String channelName) {
        GovQuotaVisibility govQuotaVisibility = govQuotaVisibilityDao.isUniqueName(id, channelName);
        if (govQuotaVisibility == null) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void updateStatus(String code, String isShow) {
        govQuotaVisibilityDao.updateStatus(code, isShow);
    }

    /**
     * 获取首页指标分类下的指标展示配置
     */
    public List<Map<String, Object>> getCategoryQuotaList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Iterator<GovQuotaCategory> categoryIter = govQuotaCategoryDao.findAll().iterator();
        while (categoryIter.hasNext()) {
            Map<String, Object> catItem = new HashMap<>();
            GovQuotaCategory category = categoryIter.next();
            catItem.put("categoryName", category.getName());
            catItem.put("categoryCode", category.getCode());
            catItem.put("quotaList", govQuotaVisibilityDao.findByType(category.getCode()));
            resultList.add(catItem);
        }
        return resultList;
    }

}
