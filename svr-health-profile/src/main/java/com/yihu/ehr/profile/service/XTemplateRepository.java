package com.yihu.ehr.profile.service;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:56
 */
public interface XTemplateRepository extends PagingAndSortingRepository<Template, Integer> {
    Template findByOrgCodeAndCdaDocumentIdAndCdaVersion(String orgCode, String cdaDocumentId, String cdaVersion);
}
