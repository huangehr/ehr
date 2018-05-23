package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by jansey on 2018/05/16.
 * 分级诊疗
 */
public class TransferTreatmentModel {

    private String _id;
    private String town;
    private String inOrg;// 转入机构
    private String outOrg;// 转出机构
    private int inOrgLevel;// 转入机构等级
    private int outOrgLevel;// 转出机构等级
    private int transEventType;    //转诊类型 0 门诊 1 出院
    private int transFerType;    //转诊方向 0 其他 1 基层医院向上到医院转诊 2 医院向下到基层机构转诊
    private String registerType;       //挂号类别  41 专家

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    private Date eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")// 2017-06-24T11:51:30+080
    @CreatedDate
    private Date createTime;//创建时间

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getInOrg() {
        return inOrg;
    }

    public void setInOrg(String inOrg) {
        this.inOrg = inOrg;
    }

    public String getOutOrg() {
        return outOrg;
    }

    public void setOutOrg(String outOrg) {
        this.outOrg = outOrg;
    }

    public int getInOrgLevel() {
        return inOrgLevel;
    }

    public void setInOrgLevel(int inOrgLevel) {
        this.inOrgLevel = inOrgLevel;
    }

    public int getOutOrgLevel() {
        return outOrgLevel;
    }

    public void setOutOrgLevel(int outOrgLevel) {
        this.outOrgLevel = outOrgLevel;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public int getTransFerType() {
        return transFerType;
    }

    public void setTransFerType(int transFerType) {
        this.transFerType = transFerType;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getTransEventType() {
        return transEventType;
    }

    public void setTransEventType(int transEventType) {
        this.transEventType = transEventType;
    }
}
