package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageReply;
import com.yihu.ehr.archivrsecurity.dao.model.SmsMessageSend;
import com.yihu.ehr.archivrsecurity.dao.repository.XSmsMessageReplyRepository;
import com.yihu.ehr.archivrsecurity.dao.repository.XSmsMessageSendRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.07.11 14:13
 */
@Service
@Transactional
public class SmsMessageReplyService extends BaseJpaService<SmsMessageReply, XSmsMessageReplyRepository> {

}
