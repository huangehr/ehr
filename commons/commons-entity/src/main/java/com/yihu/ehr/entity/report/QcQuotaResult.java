package com.yihu.ehr.entity.report;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
@Entity
@Table(name = "qc_quota_result", schema = "", catalog = "healtharchive")
public class QcQuotaResult {

    private String id;
    private Date quotaDate;    //统计时间(yyyy-mm-dd)
    private Long quotaId;       //指标ID
    private String quatoName;    //指标名称
    private String value;        //统计结果
    private String city;       //市级
    private String cityName; //市级名称
    private String town;        //区级
    private String townName;    //区级名称
    private String orgCode;    //机构code
    private String orgName;    //机构名称
    private Date receiveTime;    //采集日期
    private Date eventTime;    //事件时间
    private Integer totalNum;    //应收数
    private Integer realNum;    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
    private Integer errorNum;    //错误数量（该字段只针对数据元的准确性统计）
    private Integer timelyNum;    //及时采集的档案数量
    private String an;    //同比：与去年的当天&周&月相比
    private String mom;    //环比：与前一天&周&月环比


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
    @Column(name = "quota_date")
    public Date getQuotaDate() {
        return quotaDate;
    }

    public void setQuotaDate(Date quotaDate) {
        this.quotaDate = quotaDate;
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
    @Column(name = "quato_name")
    public String getQuatoName() {
        return quatoName;
    }

    public void setQuatoName(String quatoName) {
        this.quatoName = quatoName;
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
    @Column(name = "city_name")
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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
    @Column(name = "town_name")
    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
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
    @Column(name = "receive_time")
    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
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
}
