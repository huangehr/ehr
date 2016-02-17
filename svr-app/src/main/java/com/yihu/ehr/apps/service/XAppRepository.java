package com.yihu.ehr.apps.service;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * App 操作接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppRepository extends JpaRepository<App, String> {
    App findByName(String name);
}
