package com.yihu.ehr.basic.quota.service;

import com.yihu.ehr.basic.quota.dao.XTjQuotaWarnRepository;
import com.yihu.ehr.entity.quota.TjQuotaWarn;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/6/30
 */
@Service
@Transactional
public class TjQuotaWarnService extends BaseJpaService<TjQuotaWarn, XTjQuotaWarnRepository> {
    @Autowired
    XTjQuotaWarnRepository xTjQuotaWarnRepository;

    public List<TjQuotaWarn> findByUserId(String userId) {
        return xTjQuotaWarnRepository.findByUserId(userId);
    }


}
