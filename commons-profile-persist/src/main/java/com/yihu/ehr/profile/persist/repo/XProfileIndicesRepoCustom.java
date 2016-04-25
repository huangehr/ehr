package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.Demographic;
import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.Date;
import java.util.List;

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
