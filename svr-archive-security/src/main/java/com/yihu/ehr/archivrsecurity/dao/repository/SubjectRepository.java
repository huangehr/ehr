package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubject;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/7/11.
 */
public interface SubjectRepository extends PagingAndSortingRepository<RsAuthorizeSubject,String>{

    void deleteBySubjectId(String subjectId);

    long countBySubjectId(String subjectId);
}
