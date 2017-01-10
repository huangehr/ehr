package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageTemplate;
import com.yihu.ehr.archivrsecurity.dao.repository.MessageReplyTempRepository;
import com.yihu.ehr.archivrsecurity.dao.repository.MessageTempRepository;
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
public class MessageTemplateService extends BaseJpaService<SmsMessageTemplate,MessageTempRepository> {

    @Autowired
    MessageTempRepository messageTempRepository;

    @Autowired
    MessageReplyTempRepository replyTempRepository;

    /**
     * 删除短信模板
     * @param messageTempCode
     */
    public void deleteByMessageTempCode(String messageTempCode)
    {
        String[] codes = messageTempCode.split(",");
        for(String code : codes)
        {
            replyTempRepository.deleteByMessageTempCode(code);
            messageTempRepository.deleteByMessageTempCode(code);
        }
    }

    /**
     * 医生授权查询
     * @param sorts
     * @param page
     * @param size
     * @return
     */
    public Page<SmsMessageTemplate> getMessageTemplates(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return messageTempRepository.findAll(pageable);
    }
}
