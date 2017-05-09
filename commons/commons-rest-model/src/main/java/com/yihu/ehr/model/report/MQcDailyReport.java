package com.yihu.ehr.model.report;


import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */

public class MQcDailyReport {

    private String id;
    private String orgCode;                 //机构编码
    private Date createDate;                //采集日期
    private String innerVersion;            //适配标准版本
    private Integer realOutpatientNum;            //门诊应收数
    private Integer totalHospitalNum;            //门诊实收数
    private Integer totalOutpatientNum;            //住院应收数
    private Integer realHospitalNum;            //住院实收数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }

    public Integer getRealOutpatientNum() {
        return realOutpatientNum;
    }

    public void setRealOutpatientNum(Integer realOutpatientNum) {
        this.realOutpatientNum = realOutpatientNum;
    }

    public Integer getTotalHospitalNum() {
        return totalHospitalNum;
    }

    public void setTotalHospitalNum(Integer totalHospitalNum) {
        this.totalHospitalNum = totalHospitalNum;
    }

    public Integer getTotalOutpatientNum() {
        return totalOutpatientNum;
    }

    public void setTotalOutpatientNum(Integer totalOutpatientNum) {
        this.totalOutpatientNum = totalOutpatientNum;
    }

    public Integer getRealHospitalNum() {
        return realHospitalNum;
    }

    public void setRealHospitalNum(Integer realHospitalNum) {
        this.realHospitalNum = realHospitalNum;
    }
}
