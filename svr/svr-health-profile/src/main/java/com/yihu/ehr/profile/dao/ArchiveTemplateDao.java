package com.yihu.ehr.profile.dao;

import com.yihu.ehr.profile.model.ArchiveTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:56
 */
public interface ArchiveTemplateDao extends PagingAndSortingRepository<ArchiveTemplate, Integer> {

    List<ArchiveTemplate> findByTitleAndCdaVersion (String title, String cdaVersion);

    List<ArchiveTemplate> findByCdaVersionAndAndCdaCode (String version, String code);

    List<ArchiveTemplate> findByCdaDocumentId(List<String> docIds);

}
