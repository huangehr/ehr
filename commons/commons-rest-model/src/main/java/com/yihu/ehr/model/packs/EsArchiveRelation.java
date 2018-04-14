package com.yihu.ehr.model.packs;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * ElasticSearch:archive_relation
 * Created by progr1mmer on 2018/4/11.
 */
public class EsArchiveRelation implements Serializable {

    private String _id; //主键关联profile_id
    private String name; //姓名
    private String org_code; //机构编码
    private String org_name; //机构名称
    private String id_card_no; //身份证号码
    private Integer gender; //性别
    private String telephone; //手机号码
    private String card_type; //就诊卡类型
    private String card_no; //就诊卡号
    private Integer event_type; //事件类型
    private String event_no; //事件号
    private Date event_date; //事件时间
    private String sn; //编码
    private Date relation_date; //关联时间;
    private Date create_date; //创建时间
    private Long apply_id; //关联档案申请id
    private Long card_id; //申领卡ID
    private Integer identify_flag; //身份识别标识 0不可识别 1可以识别

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getId_card_no() {
        return id_card_no;
    }

    public void setId_card_no(String id_card_no) {
        this.id_card_no = id_card_no;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public Integer getEvent_type() {
        return event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }

    public String getEvent_no() {
        return event_no;
    }

    public void setEvent_no(String event_no) {
        this.event_no = event_no;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getEvent_date() {
        return event_date;
    }

    public void setEvent_date(Date event_date) {
        this.event_date = event_date;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getRelation_date() {
        return relation_date;
    }

    public void setRelation_date(Date relation_date) {
        this.relation_date = relation_date;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Long getApply_id() {
        return apply_id;
    }

    public void setApply_id(Long apply_id) {
        this.apply_id = apply_id;
    }

    public Long getCard_id() {
        return card_id;
    }

    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }

    public Integer getIdentify_flag() {
        return identify_flag;
    }

    public void setIdentify_flag(Integer identify_flag) {
        this.identify_flag = identify_flag;
    }
}
