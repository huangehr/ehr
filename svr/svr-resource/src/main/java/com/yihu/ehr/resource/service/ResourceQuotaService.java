package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.ResourceQuotaDao;
import com.yihu.ehr.resource.model.ResourceQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
@Service
@Transactional
public class ResourceQuotaService extends BaseJpaService<ResourceQuota, ResourceQuotaDao> {

    @Autowired
    private ResourceQuotaDao resourceQuotaDao;

    public void deleteByResourceId(String resourceId) {
        resourceQuotaDao.deleteByResourceId(resourceId);
    }

    public String getQuotaChartByQuotaId(Integer quotaId, String resourceId) {
        List<Integer> quotaChartList = resourceQuotaDao.findQuotaChartByQuotaId(quotaId, resourceId);
        if (null != quotaChartList && quotaChartList.size() > 0) {
            return quotaChartList.get(0) + "";
        }
        return "";
    }
}
