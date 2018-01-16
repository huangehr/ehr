package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubjectResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by LYR-WORK on 2016/7/11.
 */
public interface SubjectResourceRepository extends PagingAndSortingRepository<RsAuthorizeSubjectResource,String> {

    void deleteBySubjectId(String subjectId);

    void deleteBySubjectIdAndResourceId(String subjectId, String resourceId);

    List<RsAuthorizeSubjectResource> findBySubjectId(String subjectId);
}
