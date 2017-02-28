package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApi;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yeshijie
 * @version 1.0
 * @created 2017年2月16日18:04:13
 */
public interface XUserAppRepository extends JpaRepository<AppApi, String> {

}
