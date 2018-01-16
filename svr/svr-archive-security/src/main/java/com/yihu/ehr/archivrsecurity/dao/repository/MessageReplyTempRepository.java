package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageReplyTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by LYR-WORK on 2016/7/12.
 */
public interface MessageReplyTempRepository extends PagingAndSortingRepository<SmsMessageReplyTemplate,String> {

    void deleteByMessageReplyCode(String messageReplyCode);

    void deleteByMessageTempCode(String messageTempCode);
}
