package com.yihu.ehr.resource.service;

import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceQuotaDao;
import com.yihu.ehr.resource.model.RsResourceQuota;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/10.
 */
@Service
@Transactional
public class RsResourceQuotaService extends BaseJpaService<RsResourceQuota, RsResourceQuotaDao> {

    @Autowired
    private RsResourceQuotaDao resourceQuotaDao;

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

    /**
     * 根据resourceId获取该资源下的指标列表
     * @param resourceId
     * @return
     */
    public List<TjQuota> getQuotaByResourceId(String resourceId) {
        List<TjQuota> quotaList = new ArrayList<>();
        List<Integer> quotaIdList = resourceQuotaDao.findQuotaIdByResourceId(resourceId);
        if (null != quotaIdList && quotaIdList.size() > 0) {
            List<Long> longList = new ArrayList<>();
            for (Integer i : quotaIdList) {
                longList.add(i.longValue());
            }
            quotaList = this.findQuotaByQuotaId(longList);
        }
        return quotaList;
    }

    public List<TjQuota> findQuotaByQuotaId(List<Long> quotaIdList) {
        Session session = currentSession();
        String hql = "select quota from TjQuota quota where quota.id in (:quotaIdList)";
        Query query = session.createQuery(hql);
        query.setParameterList("quotaIdList", quotaIdList);
        List<TjQuota> list = query.list();
        return list;
    }
}
