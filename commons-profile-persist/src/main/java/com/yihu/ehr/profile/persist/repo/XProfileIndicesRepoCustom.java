package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.profile.persist.ProfileIndices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * 健康档案读取器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.05 17:36
 */
public interface XProfileIndicesRepoCustom{
    Page<ProfileIndices> find(Query query, Pageable pageable);
}
