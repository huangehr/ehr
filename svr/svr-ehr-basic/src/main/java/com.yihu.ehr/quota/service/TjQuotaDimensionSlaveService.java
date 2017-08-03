package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuotaDimensionSlave;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaDimensionSlaveRepository;
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
public class TjQuotaDimensionSlaveService extends BaseJpaService<TjQuotaDimensionSlave, XTjQuotaDimensionSlaveRepository> {

    @Autowired
    XTjQuotaDimensionSlaveRepository tjQuotaDimensionSlaveRepository;

    public void deleteByQuotaCode(String quotaCode) {
        tjQuotaDimensionSlaveRepository.deleteByQuotaCode(quotaCode);
    }

}
