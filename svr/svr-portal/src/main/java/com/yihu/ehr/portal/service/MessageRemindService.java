package com.yihu.ehr.portal.service;

import com.yihu.ehr.portal.dao.XMessageRemindRepository;
import com.yihu.ehr.portal.model.MessageRemind;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class MessageRemindService extends BaseJpaService<MessageRemind,XMessageRemindRepository> {

    @Autowired
    private XMessageRemindRepository messageRemindRepository;

    public MessageRemind readMessage(Integer messageId){
        MessageRemind remind = messageRemindRepository.findOne(messageId);
        remind.setReaded(1);
        messageRemindRepository.save(remind);
        return remind;
    }

}
