package com.yihu.ehr.basic.patient.dao;

import com.yihu.ehr.entity.report.JsonArchives;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface XJsonArchiveDao extends PagingAndSortingRepository<JsonArchives, Long> {
}
