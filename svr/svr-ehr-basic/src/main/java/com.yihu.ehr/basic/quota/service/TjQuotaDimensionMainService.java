package com.yihu.ehr.basic.quota.service;

import com.yihu.ehr.basic.quota.dao.XTjQuotaDimensionMainRepository;
import com.yihu.ehr.entity.quota.TjQuotaDimensionMain;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class TjQuotaDimensionMainService extends BaseJpaService<TjQuotaDimensionMain, XTjQuotaDimensionMainRepository> {

    @Autowired
    XTjQuotaDimensionMainRepository tjQuotaDimensionMainRepository;

    public void deleteByQuotaCode(String quotaCode) {
        tjQuotaDimensionMainRepository.deleteByQuotaCode(quotaCode);
    }

    public List<TjQuotaDimensionMain> getTjQuotaDimensionMainByCode(String code){
       return tjQuotaDimensionMainRepository.findByQuotaCode(code);
    }

}
