package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApiParameter;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AppApiParameter 操作接口。
 *
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:41
 */
public interface XAppApiParameterRepository extends JpaRepository<AppApiParameter, String> {

}
