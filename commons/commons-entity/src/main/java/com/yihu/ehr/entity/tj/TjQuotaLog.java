package com.yihu.ehr.entity.tj;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 统计指标日志
 *
 * @author janseny
 * @version 1.0
 * @updated 2017年6月9日
 */
@Entity
@Table(name = "tj_quota_log")
@Access(value = AccessType.PROPERTY)
public class TjQuotaLog implements Serializable{


    private long id;
    private String quota_code;  //
    private String saas_id; //
    private Integer status;//1成功 0失败
    private String content;//内容
    private Date startTime;//任务开始执行时间
    private Date endTime;//任务结束执行时间

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "quota_code",  nullable = true)
    public String getQuota_code() {
        return quota_code;
    }

    public void setQuota_code(String quota_code) {
        this.quota_code = quota_code;
    }

    @Column(name = "saas_id",  nullable = true)
    public String getSaas_id() {
        return saas_id;
    }

    public void setSaas_id(String saas_id) {
        this.saas_id = saas_id;
    }

    @Column(name = "status",  nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "content",  nullable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "start_time",  nullable = true)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "end_time",  nullable = true)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}