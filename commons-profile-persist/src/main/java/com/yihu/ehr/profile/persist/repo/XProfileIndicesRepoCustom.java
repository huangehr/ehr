package com.yihu.ehr.profile.persist.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.Criteria;

/**
 * 健康档案读取器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.05 17:36
 */
public interface XProfileIndicesRepoCustom{
    <T> Page<T> query(String core, Criteria criteria, Class<T> beanCls);
}
