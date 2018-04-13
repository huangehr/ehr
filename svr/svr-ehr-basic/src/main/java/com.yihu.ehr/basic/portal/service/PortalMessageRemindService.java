package com.yihu.ehr.basic.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.portal.dao.PortalMessageRemindRepository;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.services.DBQuery;
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
public class PortalMessageRemindService extends BaseJpaService<ProtalMessageRemind, PortalMessageRemindRepository> {

    @Autowired
    PortalMessageRemindRepository messageRemindRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DBQuery dbQuery;

    /**
     * 根据ID获取提醒消息接口.
     * @param messageRemindId
     */
    public ProtalMessageRemind getMessageRemind(Long messageRemindId) {
        ProtalMessageRemind messageRemind = messageRemindRepository.findOne(messageRemindId);
        return messageRemind;
    }

    /**
     * 删除提醒消息
     * @param messageRemindId
     */
    public void deleteMessageRemind(Long messageRemindId) {
        messageRemindRepository.delete(messageRemindId);
    }

    public List<ProtalMessageRemind> getMessageRemindTop10(){
        return messageRemindRepository.getMessageRemindTop10();
    }

    /**
     * 根据ID,更新待办事项的阅读状态.
     * @param remindId
     */
    public ProtalMessageRemind updateRemindReaded(Long remindId) {
        ProtalMessageRemind messageRemind = messageRemindRepository.findOne(remindId);
        messageRemind.setReaded(1);
        messageRemindRepository.save(messageRemind);
        return messageRemind;
    }

    public DataList listMessageRemind(String appId,String toUserId,String typeId,int page,int size){
        String sql = "select p.* from portal_message_remind p where p.type_id='" +typeId+"'" +
                " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' " +"order by p.create_date desc ";
        DataList list= null;
        try{
            list= dbQuery.queryBySql(sql,page,size);
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException("Query error!");
        }
        return list;
    }

}