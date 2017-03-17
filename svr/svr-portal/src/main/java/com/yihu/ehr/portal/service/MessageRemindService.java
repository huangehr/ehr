package com.yihu.ehr.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.portal.dao.XMessageRemindRepository;
import com.yihu.ehr.portal.model.MessageRemind;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 提醒消息接口实现类.
 * 2017-02-04 add by ysj
 */
@Service
@Transactional
public class MessageRemindService extends BaseJpaService<MessageRemind, XMessageRemindRepository> {

    @Autowired
    XMessageRemindRepository messageRemindRepository;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据ID获取提醒消息接口.
     * @param messageRemindId
     */
    public MessageRemind getMessageRemind(Long messageRemindId) {
        MessageRemind messageRemind = messageRemindRepository.findOne(messageRemindId);
        return messageRemind;
    }

    /**
     * 删除提醒消息
     * @param messageRemindId
     */
    public void deleteMessageRemind(Long messageRemindId) {
        messageRemindRepository.delete(messageRemindId);
    }

    public List<MessageRemind> getMessageRemindTop10(){
        return messageRemindRepository.getMessageRemindTop10();
    }

}