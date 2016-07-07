package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * AppApi 操作接口。
 *
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:46
 */
public interface XAppApiRepository extends JpaRepository<AppApi, String> {

}
