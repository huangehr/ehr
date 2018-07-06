package com.yihu.ehr.agModel.user;

/**
 * Created by yeshijie on 2017/2/14.
 */
public class DoctorDetailModel {
    private Long id;
    private String userId; // 云平台用户ID
    private String code; // 医生标识
    private String name; // 姓名
    private String pyCode; // 拼音首字母
    private String sex; // 性别(1男，2女)
    private String photo; // 医生头像
    private String skill; // 医生专长
    private String workPortal; // 医生专长
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
    private String insertTime; // 创建时间
    private String updateTime; // 更新时间
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
    private String SFZJZL;    //身份证件种类 ,代码：身份证件类别
    private String CSRQ;    //出生日期 ,格式为：YYYYMMDD，例如：20131125
    private String MZDM;    //民族代码 ,代码：民族代码
    private String CJGZRQ;    //参加工作日期 ,格式为：YYYYMMDD，例如：20131125
    private String SZKSDM;    //所在科室代码 ,代码：所在科室代码
    private String YSZYZSBM;    //医师/卫生监督员执业证书编码 ,
    private String SFDDDZYYS;    //是否多地点执业医师 ,代码：是否不确定代码
    private String DEZYDWJGLB;    //第2执业单位的机构类别 ,代码：多点执业单位的机构类别
    private String DSZYDWJGLB;    //第3执业单位的机构类别 ,代码：多点执业单位的机构类别
    private String SFHDGJZS;    //是否获得国家住院医师规范化培训合格证书 ,代码：是否不确定代码
    private String ZYYSZSBM;    //住院医师规范化培训合格证书编码 ,填写证书编码
    private String ZYJSZWDM;    //专业技术职务(聘)代码 ,代码：聘任专业技术职务代码
    private String XLDM;    //学历代码 ,代码：学历代码
    private String XWDM;    //学位代码 ,代码：学位代码
    private String SZYDM;    //所学专业代码 ,代码：所学专业代码
    private String ZKTC1;    //专科特长1
    private String ZKTC2;    //专科特长2
    private String ZKTC3;    //专科特长3
    private String NNRYLDQK;    //年内人员流动情况 ,代码：调动情况代码
    private String DRDCSJ;    //调入/调出时间 ,格式为：YYYYMMDD，例如：20131125
    private String BZQK;    //编制情况 ,代码：编制情况
    private String SFZCQKYX;    //是否注册为全科医学专业 ,代码：是否不确定代码
    private String QDHGZS;    //全科医生取得培训合格证书情况 ,代码：全科医生培训合格证书
    private String XZSQPZGZ;    //是否由乡镇卫生院或社区卫生服务机构派驻村卫生室工作 ,代码：是否不确定代码
    private String SFCSTJGZ;    //是否从事统计信息化业务工作 ,代码：是否不确定代码
    private String TJXXHGZ;    //统计信息化业务工作 ,代码: 统计信息化业务工作

    private String lczcName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getJobScope() {
        return jobScope;
    }

    public void setJobScope(String jobScope) {
        this.jobScope = jobScope;
    }

    public String getJobState() {
        return jobState;
    }

    public void setJobState(String jobState) {
        this.jobState = jobState;
    }

    public String getRegisterFlag() {
        return registerFlag;
    }

    public void setRegisterFlag(String registerFlag) {
        this.registerFlag = registerFlag;
    }

    public String getLczcName() {
        return lczcName;
    }

    public void setLczcName(String lczcName) {
        this.lczcName = lczcName;
    }

    public String getSFZJZL() {
        return SFZJZL;
    }

    public void setSFZJZL(String SFZJZL) {
        this.SFZJZL = SFZJZL;
    }

    public String getCSRQ() {
        return CSRQ;
    }

    public void setCSRQ(String CSRQ) {
        this.CSRQ = CSRQ;
    }

    public String getMZDM() {
        return MZDM;
    }

    public void setMZDM(String MZDM) {
        this.MZDM = MZDM;
    }

    public String getCJGZRQ() {
        return CJGZRQ;
    }

    public void setCJGZRQ(String CJGZRQ) {
        this.CJGZRQ = CJGZRQ;
    }

    public String getSZKSDM() {
        return SZKSDM;
    }

    public void setSZKSDM(String SZKSDM) {
        this.SZKSDM = SZKSDM;
    }

    public String getYSZYZSBM() {
        return YSZYZSBM;
    }

    public void setYSZYZSBM(String YSZYZSBM) {
        this.YSZYZSBM = YSZYZSBM;
    }

    public String getSFDDDZYYS() {
        return SFDDDZYYS;
    }

    public void setSFDDDZYYS(String SFDDDZYYS) {
        this.SFDDDZYYS = SFDDDZYYS;
    }

    public String getDEZYDWJGLB() {
        return DEZYDWJGLB;
    }

    public void setDEZYDWJGLB(String DEZYDWJGLB) {
        this.DEZYDWJGLB = DEZYDWJGLB;
    }

    public String getDSZYDWJGLB() {
        return DSZYDWJGLB;
    }

    public void setDSZYDWJGLB(String DSZYDWJGLB) {
        this.DSZYDWJGLB = DSZYDWJGLB;
    }

    public String getSFHDGJZS() {
        return SFHDGJZS;
    }

    public void setSFHDGJZS(String SFHDGJZS) {
        this.SFHDGJZS = SFHDGJZS;
    }

    public String getZYYSZSBM() {
        return ZYYSZSBM;
    }

    public void setZYYSZSBM(String ZYYSZSBM) {
        this.ZYYSZSBM = ZYYSZSBM;
    }

    public String getZYJSZWDM() {
        return ZYJSZWDM;
    }

    public void setZYJSZWDM(String ZYJSZWDM) {
        this.ZYJSZWDM = ZYJSZWDM;
    }

    public String getXLDM() {
        return XLDM;
    }

    public void setXLDM(String XLDM) {
        this.XLDM = XLDM;
    }

    public String getXWDM() {
        return XWDM;
    }

    public void setXWDM(String XWDM) {
        this.XWDM = XWDM;
    }

    public String getSZYDM() {
        return SZYDM;
    }

    public void setSZYDM(String SZYDM) {
        this.SZYDM = SZYDM;
    }

    public String getZKTC1() {
        return ZKTC1;
    }

    public void setZKTC1(String ZKTC1) {
        this.ZKTC1 = ZKTC1;
    }

    public String getZKTC2() {
        return ZKTC2;
    }

    public void setZKTC2(String ZKTC2) {
        this.ZKTC2 = ZKTC2;
    }

    public String getZKTC3() {
        return ZKTC3;
    }

    public void setZKTC3(String ZKTC3) {
        this.ZKTC3 = ZKTC3;
    }

    public String getNNRYLDQK() {
        return NNRYLDQK;
    }

    public void setNNRYLDQK(String NNRYLDQK) {
        this.NNRYLDQK = NNRYLDQK;
    }

    public String getDRDCSJ() {
        return DRDCSJ;
    }

    public void setDRDCSJ(String DRDCSJ) {
        this.DRDCSJ = DRDCSJ;
    }

    public String getBZQK() {
        return BZQK;
    }

    public void setBZQK(String BZQK) {
        this.BZQK = BZQK;
    }

    public String getSFZCQKYX() {
        return SFZCQKYX;
    }

    public void setSFZCQKYX(String SFZCQKYX) {
        this.SFZCQKYX = SFZCQKYX;
    }

    public String getQDHGZS() {
        return QDHGZS;
    }

    public void setQDHGZS(String QDHGZS) {
        this.QDHGZS = QDHGZS;
    }

    public String getXZSQPZGZ() {
        return XZSQPZGZ;
    }

    public void setXZSQPZGZ(String XZSQPZGZ) {
        this.XZSQPZGZ = XZSQPZGZ;
    }

    public String getSFCSTJGZ() {
        return SFCSTJGZ;
    }

    public void setSFCSTJGZ(String SFCSTJGZ) {
        this.SFCSTJGZ = SFCSTJGZ;
    }

    public String getTJXXHGZ() {
        return TJXXHGZ;
    }

    public void setTJXXHGZ(String TJXXHGZ) {
        this.TJXXHGZ = TJXXHGZ;
    }
}
