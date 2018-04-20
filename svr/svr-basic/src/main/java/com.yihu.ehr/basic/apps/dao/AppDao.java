package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.basic.apps.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * App 操作接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface AppDao extends JpaRepository<App, String> {
    App findByName(String name);

    List<App> findByIdAndSecret(String appId, String secret);
}
