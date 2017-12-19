package com.yihu.ehr.resource.service;

import com.yihu.ehr.entity.quota.TjQuota;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsResourceQuotaDao;
import com.yihu.ehr.resource.model.ResourceQuotaJson;
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

    public List<TjQuota> findQuotaById(Long quotaId) {
        Session session = currentSession();
        String hql = "select quota from TjQuota quota where quota.id = :quotaId";
        Query query = session.createQuery(hql);
        query.setLong("quotaId", quotaId);
        List<TjQuota> quota = query.list();
        return quota;
    }

    /**
     * 根据父级ID，获取其子节点；父级ID为 -1 时，返回顶级节点。
     *
     * @param pid 父级ID。
     * @return 子节点集合
     */
    public List<RsResourceQuota> getChildrenByPid(Integer pid, String resourceId) {
        List<RsResourceQuota> children = new ArrayList<>();
        if (null == pid || pid == -1) {
            children = resourceQuotaDao.getTopParents(resourceId);
        } else {
            children = resourceQuotaDao.findChildByPidAndResourceId(resourceId, pid);
        }
        return children;
    }

    /**
     * 根据父级集合，递归获取父级及其自子级集合，形成树形结构
     *
     * @param parentList 父级集合
     * @return 父级及其子集的树形结构数据
     */
    public List<RsResourceQuota> getTreeByParents(List<RsResourceQuota> parentList, String resourceId) {
        List<RsResourceQuota> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            RsResourceQuota parent = parentList.get(i);
            List<RsResourceQuota> childList = this.getChildrenByPid(parent.getQuotaId(), resourceId);
            List<RsResourceQuota> childTreeList = getTreeByParents(childList, resourceId);
            parent.setChildren(childTreeList);
            resultList.add(parent);
        }
        return resultList;
    }

    public List<TjQuota> getQuotaTreeByParents(List<RsResourceQuota> parentList) {
        List<TjQuota> resultList = new ArrayList<>();
        for (int i = 0; i < parentList.size(); i++) {
            TjQuota parent = findQuotaById((long)parentList.get(i).getQuotaId()).get(0);
            List<RsResourceQuota> children = parentList.get(i).getChildren();
            List<TjQuota> childList = new ArrayList<>();
            if (null != children && children.size() > 0) {
                for (RsResourceQuota rq : children) {
                    TjQuota quota = findQuotaById((long)rq.getQuotaId()).get(0);
                    if (null != rq.getChildren() && rq.getChildren().size() > 0) {
                        List<TjQuota> childTreeList = this.getQuotaTreeByParents(rq.getChildren());
                        quota.setChildren(childTreeList);
                    }
                    childList.add(quota);
                }
            }
            parent.setChildren(childList);
            resultList.add(parent);
        }
        return resultList;
    }

    public void updateResourceQuota(List<ResourceQuotaJson> list) {
        for (ResourceQuotaJson json : list) {
            RsResourceQuota resourceQuota = resourceQuotaDao.findByResourceIdAndQuotaId(json.getResourceId(), json.getQuotaId());
            Integer pid = json.getPid();
            if (null != pid && pid != 0) {
                resourceQuota.setPid(pid);
                resourceQuotaDao.save(resourceQuota);
            };
        }
    }
}
