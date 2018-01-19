package com.yihu.ehr.entity.emergency;


import com.yihu.ehr.entity.BaseAssignedEntity;


import javax.persistence.*;


/**
 * Entity - 救护车信息表
 * Created by progr1mmer on 2017/11/7.
 */
@Entity
@Table(name = "eme_ambulance")
@Access(value = AccessType.PROPERTY)
public class Ambulance extends BaseAssignedEntity {

    /**
     * 状态
     */
    public enum Status {
        /** 待命中 */
        wait,
        /** 前往中 */
        onWay,
        /** 抵达 */
        arrival,
        /** 返程中 */
        back,
        /** 异常 */
        down
    }

    private Integer location;
    // 所属医院名称
    private String orgName;
    // 随车手机号码
    private String phone;
    // 状态
    private Status status;
    // 图片
    private String img;
    // 百度鹰眼设备号
    private String entityName;

    @Column(name = "location", nullable = false)
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Column(name = "org_name", nullable = false)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Column(name = "phone", nullable = false)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "status", nullable = false)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "img")
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Column(name = "entity_name")
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


}
