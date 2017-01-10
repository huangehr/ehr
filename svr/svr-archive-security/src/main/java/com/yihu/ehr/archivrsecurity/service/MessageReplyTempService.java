package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageReplyTemplate;
import com.yihu.ehr.archivrsecurity.dao.repository.MessageReplyTempRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lyr on 2016/7/12.
 */
@Service
@Transactional
public class MessageReplyTempService extends BaseJpaService<SmsMessageReplyTemplate,MessageReplyTempRepository>{

    @Autowired
    MessageReplyTempRepository messageReplyTempRepository;

    /**
     * 删除回复模板
     * @param messageReplyCode
     */
    public void delteByMessageReplyCode(String messageReplyCode)
    {
        String[] codes = messageReplyCode.split(",");

        for(String code : codes)
        {
            messageReplyTempRepository.deleteByMessageReplyCode(code);
        }
    }

    /**
     * 短信回复模板查询
     * @param sorts
     * @param page
     * @param size
     * @return
     */
    public Page<SmsMessageReplyTemplate> getMessageReplyTemplates(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return messageReplyTempRepository.findAll(pageable);
    }
}
