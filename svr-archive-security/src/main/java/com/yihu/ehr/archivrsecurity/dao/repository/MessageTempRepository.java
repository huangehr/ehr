package com.yihu.ehr.archivrsecurity.dao.repository;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by lyr on 2016/7/12.
 */
public interface MessageTempRepository extends PagingAndSortingRepository<SmsMessageTemplate,String>{

    void deleteByMessageTempCode(String messageTempCode);
}
