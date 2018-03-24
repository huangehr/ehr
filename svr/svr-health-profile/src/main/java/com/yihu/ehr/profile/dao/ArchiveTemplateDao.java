package com.yihu.ehr.profile.dao;

import com.yihu.ehr.profile.model.ArchiveTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:56
 */
public interface ArchiveTemplateDao extends PagingAndSortingRepository<ArchiveTemplate, Integer> {

    ArchiveTemplate findByOrganizationCodeAndCdaDocumentIdAndCdaVersion(String orgCode, String cdaDocumentId, String cdaVersion);

    List<ArchiveTemplate> findByOrganizationCodeAndCdaVersion(String orgCode, String cdaVersion);

    ArchiveTemplate findByOrganizationCodeAndCdaVersionAndCdaDocumentId(String orgCode, String cdaVersion, String cdaDocumentId);

    @Query("SELECT count(e) FROM ArchiveTemplate e WHERE e.title = ?1 AND e.cdaVersion = ?2 AND e.organizationCode = ?3")
    int countByTitleAndCdaVersionAndOrgCode(String title, String cdaVersion, String orgCode);

    ArchiveTemplate findByOrganizationCodeAndCdaVersionAndCdaCode(String orgCode, String cdaVersion, String cdaCode);
}
