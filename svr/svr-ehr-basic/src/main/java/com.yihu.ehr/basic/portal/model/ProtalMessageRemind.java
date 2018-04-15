package com.yihu.ehr.basic.portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * 消息提醒
 *
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/20.
 */
@Entity
@Table(name = "portal_message_remind", schema = "", catalog = "healtharchive")
public class ProtalMessageRemind {

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
     * 保存 从消息推送过来的时间到当前时间 的订单信息
     */
    private String order_info;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "app_id", nullable = true)
    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    @Column(name = "app_name", nullable = true)
    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    @Column(name = "from_user_id", nullable = true)
    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    @Column(name = "to_user_id", nullable = true)
    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    @Column(name = "type_id", nullable = true)
    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    @Column(name = "content", nullable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "work_uri", nullable = true)
    public String getWork_uri() {
        return work_uri;
    }

    public void setWork_uri(String work_uri) {
        this.work_uri = work_uri;
    }

    @Column(name = "readed", nullable = true)
    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }

    @Column(name = "create_date", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    @Column(name = "message_template_id", nullable = true)
    public Long getMessage_template_id() {
        return message_template_id;
    }

    public void setMessage_template_id(Long message_template_id) {
        this.message_template_id = message_template_id;
    }

    @Column(name = "received_messages", nullable = true)
    public String getReceived_messages() {
        return received_messages;
    }

    public void setReceived_messages(String received_messages) {
        this.received_messages = received_messages;
    }

    @Column(name = "visit_time", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
    @Column(name = "order_info", nullable = true)
    public String getOrder_info() {
        return order_info;
    }

    public void setOrder_info(String order_info) {
        this.order_info = order_info;
    }
}
