package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppVersionRepository;
import com.yihu.ehr.entity.app.version.AppVersion;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Version;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Trick on 2018/3/12.
 */
@Service
@Transactional
public class AppVersionService extends BaseJpaService<AppVersion, AppVersionRepository> {

    @Autowired
    private AppVersionRepository appVersionRepository;

    public AppVersion getAppVersion(String id){
        return appVersionRepository.findOne(id);
    }
}
