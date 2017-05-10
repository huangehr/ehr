package com.yihu.ehr.agModel.report;

import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
public class QcQuotaDictModel {

    private Long id;
    private String code;        //指标编码
    private String name;         //指标名称
    private String desc;        //指标说明
    private String type;       //指标采集方式： 1 - 接口调用  2 - 日志解析 ，预留字段
    private Date createTime;    //创建时间
    private Date modifyTime;    //修改时间
    private String flag;         //1: 正常 0： 删除


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
