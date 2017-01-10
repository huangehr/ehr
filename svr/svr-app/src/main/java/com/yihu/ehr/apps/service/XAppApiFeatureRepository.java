package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppFeature;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AppApiFeature 操作接口。
 *
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:13
 */
public interface XAppApiFeatureRepository extends JpaRepository<AppFeature, String> {

}
