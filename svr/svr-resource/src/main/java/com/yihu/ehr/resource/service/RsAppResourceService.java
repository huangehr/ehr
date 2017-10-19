package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsAppResourceDao;
import com.yihu.ehr.resource.model.RsAppResource;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

/**
 * Created by wxw on 2017/10/18.
 */
@Service
@Transactional
public class RsAppResourceService extends BaseJpaService<RsAppResource, RsAppResourceDao> {
    @Autowired
    private RsAppResourceDao rsAppResourceDao;

    public String getResourceIdByAppId(String appId) {
        List<String> list = rsAppResourceDao.findResourceIdListByAppId(appId);
        HashSet<String> hash = new HashSet<>(list);
        list.clear();
        list.addAll(hash);
        String resourceIds = StringUtils.join(list, ",");
        return resourceIds;
    }
}
