package com.yihu.ehr.basic.apps.dao;

import com.yihu.ehr.basic.apps.model.AppApi;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AppApi 操作接口。
 *
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:46
 */
public interface AppApiRepository extends JpaRepository<AppApi, String> {

}
