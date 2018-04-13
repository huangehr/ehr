package com.yihu.ehr.model.org;

import java.io.Serializable;

/**
 *  部门成员 model
 * @author zdm
 * @vsrsion 1.0
 * Created at 2018/4/10.
 */
public class MJkzlOrgMemberRelation implements Serializable {
    /**
     * 总部的医生uid
     */
    private String jkzlDoctorUid;
    /**
     * 总部的医生userid
     */
    private String jkzlUserId;
    /**
     * 总部的坐诊sn
     */
    private String jkzlDoctorSn;
    /**
     * 总部的科室Id
     */
    private String jkzlHosDeptId;
    /**
     * 总部的机构Id
     */
    private String jkzlHosId;

    public String getJkzlDoctorUid() {
        return jkzlDoctorUid;
    }

    public void setJkzlDoctorUid(String jkzlDoctorUid) {
        this.jkzlDoctorUid = jkzlDoctorUid;
    }

    public String getJkzlUserId() {
        return jkzlUserId;
    }

    public void setJkzlUserId(String jkzlUserId) {
        this.jkzlUserId = jkzlUserId;
    }

    public String getJkzlDoctorSn() {
        return jkzlDoctorSn;
    }

    public void setJkzlDoctorSn(String jkzlDoctorSn) {
        this.jkzlDoctorSn = jkzlDoctorSn;
    }

    public String getJkzlHosDeptId() {
        return jkzlHosDeptId;
    }

    public void setJkzlHosDeptId(String jkzlHosDeptId) {
        this.jkzlHosDeptId = jkzlHosDeptId;
    }

    public String getJkzlHosId() {
        return jkzlHosId;
    }

    public void setJkzlHosId(String jkzlHosId) {
        this.jkzlHosId = jkzlHosId;
    }
}
