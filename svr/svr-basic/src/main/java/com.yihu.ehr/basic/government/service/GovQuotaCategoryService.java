package com.yihu.ehr.basic.government.service;

import com.yihu.ehr.basic.government.dao.GovQuotaCategoryDao;
import com.yihu.ehr.basic.government.entity.GovQuotaCategory;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 政府服务平台首页指标分类 Service
 *
 * @author 张进军
 * @date 2018/7/5 17:48
 */
@Service
public class GovQuotaCategoryService extends BaseJpaService<GovQuotaCategory, GovQuotaCategoryDao> {

    @Autowired
    private GovQuotaCategoryDao govQuotaCategoryDao;
    
    public GovQuotaCategory getById(Integer id) {
        return govQuotaCategoryDao.findOne(id);
    }

    @Transactional(readOnly = false)
    public GovQuotaCategory save(GovQuotaCategory govQuotaCategory) {
        return govQuotaCategoryDao.save(govQuotaCategory);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        govQuotaCategoryDao.delete(id);
    }

    public Boolean isUniqueCode(Integer id, String code) {
        GovQuotaCategory govQuotaCategory = govQuotaCategoryDao.isUniqueCode(id, code);
        if (govQuotaCategory == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueName(Integer id, String channelName) {
        GovQuotaCategory govQuotaCategory = govQuotaCategoryDao.isUniqueName(id, channelName);
        if (govQuotaCategory == null) {
            return true;
        } else {
            return false;
        }
    }

}
