package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AppApiResponse 操作接口。
 *
 * @author linz
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppApiResponseRepository extends JpaRepository<AppApiResponse, String> {

}
