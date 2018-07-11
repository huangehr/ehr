package com.yihu.ehr.basic.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.Date;

/**
 * 2017-02-04 add by hzp
 */
@Entity
@Table(name = "doctors")
@Access(value = AccessType.PROPERTY)
public class Doctors {
    private Long id;
    private String userId; // 云平台用户ID
    private String code; // 医生标识
    private String name; // 姓名
    private String pyCode; // 拼音首字母
    private String sex; // 性别(1男，2女)
    private String photo; // 医生头像
    private String skill; // 医生专长
    private String workPortal; // 医生门户首页
    private String email; // 邮箱
    private String phone; // 联系电话
    private String secondPhone; // 备用电话
    private String familyTel; // 家庭电话（固）
    private String officeTel; // 办公电话（固）
    private String introduction; // 简介
    private String jxzc; // 教学职称 变更为是否制证。参考系统字典 制证标识
    private String lczc; // 临床职称--改成技术职称，值参考系统字典 技术职称
    private String xlzc; // 学历职称
    private String xzzc; // 行政职称
    private String status; // 1生效，0失效
    private Date insertTime; // 创建时间
    private Date updateTime; // 更新时间
    private String idCardNo; // 身份证号码
    private String orgId; // 机构id
    private String orgCode; // 机构code
    private String orgFullName; // 机构名称
    private String roleType; // 参考系统字典 人员类别
    private String deptName; // 部门名称
    private String jobType; // 参考系统字典 执业类别
    private String jobLevel; // 参考系统字典 从事专业类别代码-执业级别
    private String jobScope; // 参考系统字典 执业范围
    private String jobState; // 参考系统字典 执业状态
    private String registerFlag; // 是否考试库连带注册-数据手动导入。默认0为是，1为否
    private String sfzjzl;    //身份证件种类 ,代码：身份证件类别
    private String csrq;    //出生日期 ,格式为：YYYYMMDD，例如：20131125
    private String mzdm;    //民族代码 ,代码：民族代码
    private String cjgzrq;    //参加工作日期 ,格式为：YYYYMMDD，例如：20131125
    private String szksdm;    //所在科室代码 ,代码：所在科室代码
    private String yszyzsbm;    //医师/卫生监督员执业证书编码 ,
    private String sfdddzyys;    //是否多地点执业医师 ,代码：是否不确定代码
    private String dezydwjglb;    //第2执业单位的机构类别 ,代码：多点执业单位的机构类别
    private String dszydwjglb;    //第3执业单位的机构类别 ,代码：多点执业单位的机构类别
    private String sfhdgjzs;    //是否获得国家住院医师规范化培训合格证书 ,代码：是否不确定代码
    private String zyyszsbm;    //住院医师规范化培训合格证书编码 ,填写证书编码
    private String zyjszwdm;    //专业技术职务(聘)代码 ,代码：聘任专业技术职务代码
    private String xldm;    //学历代码 ,代码：学历代码
    private String xwdm;    //学位代码 ,代码：学位代码
    private String szydm;    //所学专业代码 ,代码：所学专业代码
    private String zktc1;    //专科特长1
    private String zktc2;    //专科特长2
    private String zktc3;    //专科特长3
    private String nnryldqk;    //年内人员流动情况 ,代码：调动情况代码
    private String drdcsj;    //调入/调出时间 ,格式为：YYYYMMDD，例如：20131125
    private String bzqk;    //编制情况 ,代码：编制情况
    private String sfzcqkyx;    //是否注册为全科医学专业 ,代码：是否不确定代码
    private String qdhgzs;    //全科医生取得培训合格证书情况 ,代码：全科医生培训合格证书
    private String xzsqpzgz;    //是否由乡镇卫生院或社区卫生服务机构派驻村卫生室工作 ,代码：是否不确定代码
    private String sfcstjgz;    //是否从事统计信息化业务工作 ,代码：是否不确定代码
    private String tjxxhgz;    //统计信息化业务工作 ,代码: 统计信息化业务工作

    // 临时属性
    private String lczcName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "userId", nullable = true)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPyCode() {
        return pyCode;
    }

    public void setPyCode(String pyCode) {
        this.pyCode = pyCode;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getWorkPortal() {
        return workPortal;
    }

    public void setWorkPortal(String workPortal) {
        this.workPortal = workPortal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getFamilyTel() {
        return familyTel;
    }

    public void setFamilyTel(String familyTel) {
        this.familyTel = familyTel;
    }

    public String getOfficeTel() {
        return officeTel;
    }

    public void setOfficeTel(String officeTel) {
        this.officeTel = officeTel;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getJxzc() {
        return jxzc;
    }

    public void setJxzc(String jxzc) {
        this.jxzc = jxzc;
    }

    public String getLczc() {
        return lczc;
    }

    public void setLczc(String lczc) {
        this.lczc = lczc;
    }

    public String getXlzc() {
        return xlzc;
    }

    public void setXlzc(String xlzc) {
        this.xlzc = xlzc;
    }

    public String getXzzc() {
        return xzzc;
    }

    public void setXzzc(String xzzc) {
        this.xzzc = xzzc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "id_card_no", nullable = false)
    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    @Column(name = "org_id")
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "org_code")
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    @Column(name = "org_full_name")
    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName;
    }

    @Column(name = "role_type")
    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @Column(name = "dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Column(name = "job_type")
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Column(name = "job_level")
    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    @Column(name = "job_scope")
    public String getJobScope() {
        return jobScope;
    }

    public void setJobScope(String jobScope) {
        this.jobScope = jobScope;
    }

    @Column(name = "job_state")
    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    @Column(name = "register_flag")
    public String getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(String registerFlag) {
        this.registerFlag = registerFlag;
    }

    @Formula("( SELECT a.value FROM system_dict_entries a WHERE a.dict_id = 118 AND a.code = lczc )")
    public String getLczcName() {
        return lczcName;
    }

    public void setLczcName(String lczcName) {
        this.lczcName = lczcName;
    }
    @Column(name = "SFZJZL")
    public String getSfzjzl() {
        return sfzjzl;
    }

    public void setSfzjzl(String sfzjzl) {
        this.sfzjzl = sfzjzl;
    }
    @Column(name = "CSRQ")
    public String getCsrq() {
        return csrq;
    }

    public void setCsrq(String csrq) {
        this.csrq = csrq;
    }
    @Column(name = "MZDM")
    public String getMzdm() {
        return mzdm;
    }

    public void setMzdm(String mzdm) {
        this.mzdm = mzdm;
    }
    @Column(name = "CJGZRQ")
    public String getCjgzrq() {
        return cjgzrq;
    }

    public void setCjgzrq(String cjgzrq) {
        this.cjgzrq = cjgzrq;
    }
    @Column(name = "SZKSDM")
    public String getSzksdm() {
        return szksdm;
    }

    public void setSzksdm(String szksdm) {
        this.szksdm = szksdm;
    }
    @Column(name = "YSZYZSBM")
    public String getYszyzsbm() {
        return yszyzsbm;
    }

    public void setYszyzsbm(String yszyzsbm) {
        this.yszyzsbm = yszyzsbm;
    }
    @Column(name = "SFDDDZYYS")
    public String getSfdddzyys() {
        return sfdddzyys;
    }

    public void setSfdddzyys(String sfdddzyys) {
        this.sfdddzyys = sfdddzyys;
    }
    @Column(name = "DEZYDWJGLB")
    public String getDezydwjglb() {
        return dezydwjglb;
    }

    public void setDezydwjglb(String dezydwjglb) {
        this.dezydwjglb = dezydwjglb;
    }
    @Column(name = "DSZYDWJGLB")
    public String getDszydwjglb() {
        return dszydwjglb;
    }

    public void setDszydwjglb(String dszydwjglb) {
        this.dszydwjglb = dszydwjglb;
    }
    @Column(name = "SFHDGJZS")
    public String getSfhdgjzs() {
        return sfhdgjzs;
    }

    public void setSfhdgjzs(String sfhdgjzs) {
        this.sfhdgjzs = sfhdgjzs;
    }
    @Column(name = "ZYYSZSBM")
    public String getZyyszsbm() {
        return zyyszsbm;
    }

    public void setZyyszsbm(String zyyszsbm) {
        this.zyyszsbm = zyyszsbm;
    }
    @Column(name = "ZYJSZWDM")
    public String getZyjszwdm() {
        return zyjszwdm;
    }

    public void setZyjszwdm(String zyjszwdm) {
        this.zyjszwdm = zyjszwdm;
    }
    @Column(name = "XLDM")
    public String getXldm() {
        return xldm;
    }

    public void setXldm(String xldm) {
        this.xldm = xldm;
    }
    @Column(name = "XWDM")
    public String getXwdm() {
        return xwdm;
    }

    public void setXwdm(String xwdm) {
        this.xwdm = xwdm;
    }
    @Column(name = "SZYDM")
    public String getSzydm() {
        return szydm;
    }

    public void setSzydm(String szydm) {
        this.szydm = szydm;
    }
    @Column(name = "ZKTC1")
    public String getZktc1() {
        return zktc1;
    }

    public void setZktc1(String zktc1) {
        this.zktc1 = zktc1;
    }
    @Column(name = "ZKTC2")
    public String getZktc2() {
        return zktc2;
    }

    public void setZktc2(String zktc2) {
        this.zktc2 = zktc2;
    }
    @Column(name = "ZKTC3")
    public String getZktc3() {
        return zktc3;
    }

    public void setZktc3(String zktc3) {
        this.zktc3 = zktc3;
    }
    @Column(name = "NNRYLDQK")
    public String getNnryldqk() {
        return nnryldqk;
    }

    public void setNnryldqk(String nnryldqk) {
        this.nnryldqk = nnryldqk;
    }
    @Column(name = "DRDCSJ")
    public String getDrdcsj() {
        return drdcsj;
    }

    public void setDrdcsj(String drdcsj) {
        this.drdcsj = drdcsj;
    }
    @Column(name = "BZQK")
    public String getBzqk() {
        return bzqk;
    }

    public void setBzqk(String bzqk) {
        this.bzqk = bzqk;
    }
    @Column(name = "SFZCQKYX")
    public String getSfzcqkyx() {
        return sfzcqkyx;
    }

    public void setSfzcqkyx(String sfzcqkyx) {
        this.sfzcqkyx = sfzcqkyx;
    }
    @Column(name = "QDHGZS")
    public String getQdhgzs() {
        return qdhgzs;
    }

    public void setQdhgzs(String qdhgzs) {
        this.qdhgzs = qdhgzs;
    }
    @Column(name = "XZSQPZGZ")
    public String getXzsqpzgz() {
        return xzsqpzgz;
    }

    public void setXzsqpzgz(String xzsqpzgz) {
        this.xzsqpzgz = xzsqpzgz;
    }
    @Column(name = "SFCSTJGZ")
    public String getSfcstjgz() {
        return sfcstjgz;
    }

    public void setSfcstjgz(String sfcstjgz) {
        this.sfcstjgz = sfcstjgz;
    }
    @Column(name = "TJXXHGZ")
    public String getTjxxhgz() {
        return tjxxhgz;
    }

    public void setTjxxhgz(String tjxxhgz) {
        this.tjxxhgz = tjxxhgz;
    }
}