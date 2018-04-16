package com.yihu.ehr.basic.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.portal.dao.PortalMessageRemindRepository;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.services.DBQuery;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public DataList listMessageRemind(String appId, String toUserId, String typeId, int page, int size) throws Exception {
        String sql = "select p.* from portal_message_remind p where p.type_id='" +typeId+"'" +
                " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' " +"order by p.create_date desc ";
        DataList list= dbQuery.queryBySql(sql, page, size);
        return list;
    }

    /**
     * 获取就诊信息列表
     * @param appId
     * @param toUserId
     * @param typeId
     * @param page
     * @param size
     * @return
     */
    public DataList listMessageRemindValue(String appId, String toUserId, String typeId,String type,int page, int size) throws Exception {
        String date = DateUtil.getNowDate(DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
        String sql ="";
        //我的就诊-列表，获取就诊时间前的数据
        if(type.equals("101")){
            sql = "select p.* from portal_message_remind p " +
                    "JOIN portal_message_template pt " +
                    "on p.message_template_id =pt.id " +
                    "where pt.type='"+type+"'"+" AND pt.classification='0'"+
                    "AND p.type_id='" +typeId+"'" +
                    " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' "+
                    "AND p.visit_time >  '"+ date +"' order by p.create_date desc ";
        }else{
            //满意度调查，获取待评价消息
            sql = "select p.* from portal_message_remind p " +
                    "JOIN portal_message_template pt " +
                    "on p.message_template_id =pt.id " +
                    "where pt.type='"+type+"'"+" AND pt.classification='0'"+
                    "AND p.type_id='" +typeId+"'" +
                    " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' "+
                     " order by p.create_date desc ";
        }
        DataList list= dbQuery.queryBySql(sql,page,size);
        return list;
    }


}