package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjQuotaDimensionMain;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaDimensionMainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
