package com.yihu.ehr.model.portal;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public class MMessageRemind {

    private Long id;
    private String app_id;
    private String app_name;
    private String from_user_id;
    private String to_user_id;
    private String type_id;
    private String content;
    private String work_uri;
    private Integer readed;
    private Date create_date;
    private Long message_template_id;
    /**
     * 推送过来的消息
     */
    private String received_messages;

    /**
     * 就诊时间
     */
    private Date visit_time;
    /**
     * 就诊部门地址
     */
    private String deptAdress;
    /**
     * 预约挂号订单id
     */
    private String notice;
    /**
     * 温馨提示
     */
    private String order_id;
    private String fromUserName;
    private String toUserName;
    private MRegistration mRegistration;
    /**
     * 我的就诊-是否通知：0为通知，1为不通知。我的档案：0未评价、1为已评价
     */
    private String notifie_flag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWork_uri() {
        return work_uri;
    }

    public void setWork_uri(String work_uri) {
        this.work_uri = work_uri;
    }

    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Long getMessage_template_id() {
        return message_template_id;
    }

    public void setMessage_template_id(Long message_template_id) {
        this.message_template_id = message_template_id;
    }

    public String getReceived_messages() {
        return received_messages;
    }

    public void setReceived_messages(String received_messages) {
        this.received_messages = received_messages;
    }
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(Date visit_time) {
        this.visit_time = visit_time;
    }

    public String getDeptAdress() {
        return deptAdress;
    }

    public void setDeptAdress(String deptAdress) {
        this.deptAdress = deptAdress;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public MRegistration getmRegistration() {
        return mRegistration;
    }

    public void setmRegistration(MRegistration mRegistration) {
        this.mRegistration = mRegistration;
    }

    public String getNotifie_flag() {
        return notifie_flag;
    }

    public void setNotifie_flag(String notifie_flag) {
        this.notifie_flag = notifie_flag;
    }
}
