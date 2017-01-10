package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.AuthorizeAppSubject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public interface XAuthorizeAppSubjectRepository extends JpaRepository<AuthorizeAppSubject, Long> {

}

