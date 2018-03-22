package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.entity.app.version.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Trick on 2018/3/12.
 */
public interface AppVersionRepository extends JpaRepository<AppVersion, String> {
    public AppVersion findBycode(String code);
}
