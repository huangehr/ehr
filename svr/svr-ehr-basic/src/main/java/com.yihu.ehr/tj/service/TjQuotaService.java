package com.yihu.ehr.tj.service;

import com.yihu.ehr.entity.tj.TjQuota;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.tj.dao.XTjQuotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjQuotaService extends BaseJpaService<TjQuota, XTjQuotaRepository> {
    @Autowired
    XTjQuotaRepository tjQuotaRepository;

    public TjQuota getById(Long id) {
        TjQuota tjQuota = tjQuotaRepository.findOne(id);
        return tjQuota;
    }
}
