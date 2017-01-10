package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
public interface XSmsMessageSendRepository extends JpaRepository<SmsMessageSend, Long> {

    @Modifying
    @Query("update SmsMessageSend ss set ss.status = :status where ss.id = :id")
    void updateStatus(@Param("id") long id, @Param ("status") int status);
}

