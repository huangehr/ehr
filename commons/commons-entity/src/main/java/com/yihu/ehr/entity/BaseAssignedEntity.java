package com.yihu.ehr.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity - 基类（非自动递增）
 * Created by progr1mmer on 2017/11/17.
 */
@MappedSuperclass
@EntityListeners(AssignedEntityListener.class)
public abstract class BaseAssignedEntity implements Serializable {

    private static final long serialVersionUID = -67186388306700736L;

    /** ID */
    private String id;
    /** 创建日期 */
    private Date createDate;
    /** 创建者 */
    private String creator;
    /** 修改日期 */
    private Date modifyDate;
    /** 修改者 */
    private String modifier;

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "create_date", nullable = false, updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "creator")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "modify_date", nullable = false)
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Column(name = "modifier")
    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!BaseAssignedEntity.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        BaseAssignedEntity other = (BaseAssignedEntity) obj;
        return getId() != null ? getId().equals(other.getId()) : false;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }
}
