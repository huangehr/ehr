package com.yihu.ehr.basic.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.portal.dao.PortalMessageRemindRepository;
import com.yihu.ehr.basic.portal.model.ProtalMessageRemind;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.services.DBQuery;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
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
     * 根据挂号单ID获取消息内容
     * @param orderId 医疗云挂号单ID
     * @return
     */
    public List<ProtalMessageRemind> getByOrderId(String orderId) {
        return messageRemindRepository.getByOrderIdOrderByCreateDateDesc(orderId);
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
        String sql = "select p.id AS id, p.app_id AS appId, p.app_name AS appName, p.from_user_id AS fromUserId, p.type_id AS typeId, p.content AS content, p.work_uri AS workUri, p.readed AS readed, p.create_date AS createDate, p.to_user_id AS toUserId, p.message_template_id AS messageTemplateId, p.received_messages AS receivedMessages, p.order_id AS orderId, p.visit_time AS visitTime, p.notifie_flag AS notifieFlag, p.portal_messager_template_type AS  portalMessagerTemplateType from portal_message_remind p where p.type_id='" +typeId+"'" +
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
     *  @param notifie 是否通知：0为通知，1为不再通知 ,满意度调查：0为未平价、1为已评价
     * @return
     */
    public DataList listMessageRemindValue(String appId, String toUserId, String typeId,String type,int page, int size,String notifie) throws Exception {
        String date = DateUtil.getNowDate(DateUtil.DEFAULT_DATE_YMD_FORMAT);
        String notifieSql= "";
        if(StringUtils.isNotEmpty(notifie)){
            notifieSql = " AND p.notifie_flag = '"+ notifie +"' ";
        }
        String sql ="";
        //我的就诊-列表，获取就诊时间前的数据
        if(StringUtils.isNotEmpty(type) && type.equals("101")){
            sql = "SELECT re.order_id AS orderId,re.order_create_time AS orderCreateTime,re.patient_name AS patientName,re.hospital_name AS hospitalName,re.dept_name AS deptName,re.doctor_name AS doctorName,re.state AS state,re.state_desc AS stateDesc,re.register_date AS registerDate,re.visit_clinic_result AS visitClinicResult,re.visit_clinic_result_desc AS visitClinicResultDesc,re.time_id AS timeId,re.time_id_desc AS timeIdDesc,re.invalid_date AS invalidDate,re.create_date AS createDate,re.register_type AS registerType,re.register_type_desc AS registerTypeDesc,re.serial_no  AS serialNo  " +
                    "FROM registration re JOIN portal_message_remind p ON re.order_id = p.order_id " +
                    "JOIN portal_message_template pt ON p.message_template_id = pt.id " +
                  " where  pt.state='0' and pt.type='"+type+"'"+" AND pt.classification='0'"+
                    " AND p.type_id='" +typeId+"'" +
                    " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' " +" AND re.state='2'"+
                    " AND re.register_date >= '"+ date +"' "+ notifieSql+" order by p.create_date desc ";
        }else if(StringUtils.isNotEmpty(type )&& type.equals("100")){
            //满意度调查，获取待评价消息
            sql = "select p.id AS id, p.app_id AS appId, p.app_name AS appName, p.from_user_id AS fromUserId, p.type_id AS typeId, p.content AS content, p.work_uri AS workUri, p.readed AS readed, p.create_date AS createDate, p.to_user_id AS toUserId, p.message_template_id AS messageTemplateId, p.received_messages AS receivedMessages, p.order_id AS orderId, p.visit_time AS visitTime, p.notifie_flag AS notifieFlag, p.portal_messager_template_type AS  portalMessagerTemplateType " +
                    "from portal_message_remind p " +
                    "JOIN portal_message_template pt " +
                    "on p.message_template_id =pt.id " +
                    "where  pt.state='0' and pt.type='"+type+"'"+" AND pt.classification='0'"+
                    "AND p.type_id='" +typeId+"'" +
                    " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' "+ notifieSql +
                     " order by p.create_date desc ";
        }else{
            //满意度调查，获取待评价消息
            sql = "select p.id AS id, p.app_id AS appId, p.app_name AS appName, p.from_user_id AS fromUserId, p.type_id AS typeId, p.content AS content, p.work_uri AS workUri, p.readed AS readed, p.create_date AS createDate, p.to_user_id AS toUserId, p.message_template_id AS messageTemplateId, p.received_messages AS receivedMessages, p.order_id AS orderId, p.visit_time AS visitTime, p.notifie_flag AS notifieFlag, p.portal_messager_template_type AS  portalMessagerTemplateType " +
                    "from portal_message_remind p " +
                    "JOIN portal_message_template pt " +
                    "on p.message_template_id =pt.id " +
                    "where pt.state='0' and  (pt.type='101' or pt.type='100')"+" AND pt.classification='0'"+
                    "AND p.type_id='" +typeId+"'" +
                    " AND p.to_user_id='" +toUserId+"' " +" AND p.app_id='" +appId+"' "+ notifieSql +
                    " order by p.create_date desc ";
        }
        DataList list= dbQuery.queryBySql(sql,page,size);
        return list;
    }

    /**
     * 根据id修改已读状态
     * @param protalMessageRemindId
     * @return
     * @throws Exception
     */
    public boolean updateMessageRemind(String field,String state,Long protalMessageRemindId) throws Exception {
        String  sql = "UPDATE portal_message_remind pm  SET  pm."+field+"="+state +" WHERE pm.id="+protalMessageRemindId;
        jdbcTemplate.update(sql);
        return true;
    }


}