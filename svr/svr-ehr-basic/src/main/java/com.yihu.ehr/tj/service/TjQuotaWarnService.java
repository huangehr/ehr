package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjQuotaWarn;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaWarnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
