package com.yihu.ehr.quota.service;

import com.yihu.ehr.entity.quota.TjQuotaDataSource;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.quota.dao.XTjQuotaDataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@Service
@Transactional
public class TjQuotaDataSourceService extends BaseJpaService<TjQuotaDataSource,XTjQuotaDataSourceRepository> {
    @Autowired
    XTjQuotaDataSourceRepository tjQuotaDataSourceRepository;

    public TjQuotaDataSource getByQuotaCode(String quotaCode) {
        List<TjQuotaDataSource> tjQuotaDataSave = tjQuotaDataSourceRepository.getByQuotaCode(quotaCode);
        if (tjQuotaDataSave != null && tjQuotaDataSave.size() > 0) {
            return tjQuotaDataSave.get(0);
        }
        return null;
    }

    public void deleteByQuotaCode(String quotaCode) {
        tjQuotaDataSourceRepository.deleteByQuotaCode(quotaCode);
    }
}
