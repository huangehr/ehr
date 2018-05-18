package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by wxw on 2018/3/24.
 */
public class OutPatientCostModel {
    @JestId
    private String _id;
    private Integer type;    // 1疾病 2科室
    private String code;    // 疾病或科室编码
    private String name;
    private Double result;
    private String town;    // 所在区县
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT+8")    // 2017-06-24T11:51:30+080
    private Date eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT+8")    // 2017-06-24T11:51:30+080
    @CreatedDate
    private Date createTime;//创建时间

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
