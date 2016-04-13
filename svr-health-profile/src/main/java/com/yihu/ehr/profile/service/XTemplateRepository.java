package com.yihu.ehr.profile.service;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:56
 */
public interface XTemplateRepository extends PagingAndSortingRepository<Template, Integer> {
    Template findByOrganizationCodeAndCdaDocumentIdAndCdaVersion(String orgCode, String cdaDocumentId, String cdaVersion);

    List<Template> findByOrganizationCodeAndCdaVersion(String orgCode, String cdaVersion);
}
