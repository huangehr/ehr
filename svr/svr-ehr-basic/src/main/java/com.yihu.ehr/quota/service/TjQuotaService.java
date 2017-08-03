package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.entity.quota.TjQuotaDataSave;
import com.yihu.ehr.entity.quota.TjQuotaDataSource;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaRepository;
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
    @Autowired
    TjQuotaDataSourceService tjQuotaDataSourceService;
    @Autowired
    TjQuotaDataSaveService tjQuotaDataSaveService;

    public void saves(TjQuota quota, TjQuotaDataSource dataSource, TjQuotaDataSave dataSave) {
        tjQuotaDataSourceService.deleteByQuotaCode(dataSource.getQuotaCode());
        tjQuotaDataSaveService.deleteByQuotaCode(dataSave.getQuotaCode());
        tjQuotaDataSourceService.save(dataSource);
        tjQuotaDataSaveService.save(dataSave);
        save(quota);
    }

    public TjQuota getById(Long id) {
        TjQuota tjQuota = tjQuotaRepository.findOne(id);
        return tjQuota;
    }

    public TjQuota findByCode(String code) {
        TjQuota tjQuota = tjQuotaRepository.findByCode(code);
        return tjQuota;
    }

}
