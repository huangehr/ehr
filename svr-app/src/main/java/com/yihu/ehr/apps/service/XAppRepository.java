package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * App 操作接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.16 15:10
 */
public interface XAppRepository extends JpaRepository<App, String> {

}
