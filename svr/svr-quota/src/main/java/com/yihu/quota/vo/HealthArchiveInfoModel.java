package com.yihu.quota.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.searchbox.annotations.JestId;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

/**
 * Created by wxw on 2018/3/12.
 */
public class HealthArchiveInfoModel {
    @JestId
    private String id;
    private String name;    // 姓名
    private int sex;    // 性别编号
    private String sexName;
    private int ageCode;    // 年龄段编号
    private String ageName;
    private String town;    // 所在区县
    private long result;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT+8")// 2017-06-24T11:51:30+080
    private Date eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT+8")// 2017-06-24T11:51:30+080
    @CreatedDate
    private Date createTime;//创建时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public int getAgeCode() {
        return ageCode;
    }

    public void setAgeCode(int ageCode) {
        this.ageCode = ageCode;
    }

    public String getAgeName() {
        return ageName;
    }

    public void setAgeName(String ageName) {
        this.ageName = ageName;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
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
