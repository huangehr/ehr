package com.yihu.ehr.entity.report;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by janseny on 2017/5/8.
 */
@Entity
@Table(name = "qc_daily_report", schema = "", catalog = "healtharchive")
public class QcDailyReport {

    private String id;
    private String orgCode;                 //机构编码
    private Date createDate;                //采集日期
    private String innerVersion;            //适配标准版本
    private Integer realOutpatientNum;            //门诊应收数
    private Integer totalHospitalNum;            //门诊实收数
    private Integer totalOutpatientNum;            //住院应收数
    private Integer realHospitalNum;            //住院实收数
    private Date addDate;                     //添加时间

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
    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Basic
    @Column(name = "create_date")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Basic
    @Column(name = "inner_version")
    public String getInnerVersion() {
        return innerVersion;
    }

    public void setInnerVersion(String innerVersion) {
        this.innerVersion = innerVersion;
    }
    @Basic
    @Column(name = "real_outpatient_num")
    public Integer getRealOutpatientNum() {
        return realOutpatientNum;
    }

    public void setRealOutpatientNum(Integer realOutpatientNum) {
        this.realOutpatientNum = realOutpatientNum;
    }
    @Basic
    @Column(name = "total_hospital_num")
    public Integer getTotalHospitalNum() {
        return totalHospitalNum;
    }

    public void setTotalHospitalNum(Integer totalHospitalNum) {
        this.totalHospitalNum = totalHospitalNum;
    }
    @Basic
    @Column(name = "total_outpatient_num")
    public Integer getTotalOutpatientNum() {
        return totalOutpatientNum;
    }

    public void setTotalOutpatientNum(Integer totalOutpatientNum) {
        this.totalOutpatientNum = totalOutpatientNum;
    }
    @Basic
    @Column(name = "real_hospital_num")
    public Integer getRealHospitalNum() {
        return realHospitalNum;
    }

    public void setRealHospitalNum(Integer realHospitalNum) {
        this.realHospitalNum = realHospitalNum;
    }

    @Basic
    @Column(name = "add_date")
    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}
