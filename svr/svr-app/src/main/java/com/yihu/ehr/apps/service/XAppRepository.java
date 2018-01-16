package com.yihu.ehr.apps.service;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yihu.ehr.apps.model.App;
import java.util.List;

/**
 * App 操作接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppRepository extends JpaRepository<App, String> {
    App findByName(String name);

    List<App> findByIdAndSecret(String appId,String secret);
}
