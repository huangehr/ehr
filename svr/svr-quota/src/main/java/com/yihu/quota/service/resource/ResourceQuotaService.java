package com.yihu.quota.service.resource;

import com.yihu.quota.dao.jpa.RsResourceQuotaDao;
import com.yihu.quota.model.jpa.RsResourceQuota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */
@Service
public class ResourceQuotaService {
    @Autowired
    private RsResourceQuotaDao rsResourceQuotaDao;

    public List<String> getQuotaRelation(String resourceId, Integer pid) {
        List<String> list = rsResourceQuotaDao.fingQuotaRelation(resourceId, pid);
        return list;
    }

    public List<RsResourceQuota> getChildrenByPid(Integer pid, String resourceId) {
        List<RsResourceQuota> children = new ArrayList<>();
        if (null == pid || pid == -1) {
            children = rsResourceQuotaDao.getTopParents(resourceId);
        } else {
            children = rsResourceQuotaDao.findChildByPidAndResourceId(resourceId, pid);
        }
        return children;
    }

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
}
