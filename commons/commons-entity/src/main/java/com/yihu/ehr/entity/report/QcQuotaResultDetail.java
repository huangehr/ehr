package com.yihu.ehr.entity.report;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by zdm on 2017/5/16.
 */
@Entity
@Table(name = "qc_quota_result", schema = "", catalog = "healtharchive")
public class QcQuotaResultDetail {

    private String id;
    private String city;       //市级
    private String town;        //区级
    private String value;        //统计结果
    private Long quotaId;       //指标ID
    private String quotaName;    //指标名称
    private String orgCode;    //机构code
    private String orgName;    //机构名称
    private Date eventTime;    //事件时间
    private Integer totalNum;    //应收数
    private Integer realNum;    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
    private Integer errorNum;    //错误数量（该字段只针对数据元的准确性统计）
    private Integer timelyNum;    //及时采集的档案数量
    private String an;    //同比：与去年的当天&周&月相比
    private String mom;    //环比：与前一天&周&月环比
    private String standard; //达标

    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    @Column(name = "id", unique = true, nullable = false, length = 32)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Basic
    @Column(name = "quota_id")
    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }
    @Basic
    @Column(name = "quota_name")
    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }
    @Basic
    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    @Basic
    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "town")
    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Basic
    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    @Basic
    @Column(name = "org_name")
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "event_time")
    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    @Basic
    @Column(name = "total_num")
    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
    @Basic
    @Column(name = "real_num")
    public Integer getRealNum() {
        return realNum;
    }

    public void setRealNum(Integer realNum) {
        this.realNum = realNum;
    }
    @Basic
    @Column(name = "error_num")
    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }
    @Basic
    @Column(name = "timely_num")
    public Integer getTimelyNum() {
        return timelyNum;
    }

    public void setTimelyNum(Integer timelyNum) {
        this.timelyNum = timelyNum;
    }
    @Basic
    @Column(name = "an")
    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }
    @Basic
    @Column(name = "mom")
    public String getMom() {
        return mom;
    }

    public void setMom(String mom) {
        this.mom = mom;
    }

    @Basic
    @Column(name = "standard")
    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }
}
