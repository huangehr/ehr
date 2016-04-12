package com.yihu.ehr.model.standard;


import java.util.Date;
import java.util.Objects;

/**
 * CDA类型
 *
 * @author AndyCai
 * @version 1.0
 * @created 11-12月-2015 15:52:22
 */
public class MCDAType {

    private String id;
    private String code;
    private String name;
    private String parentId;
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCDAType mcdaType = (MCDAType) o;
        return Objects.equals(id, mcdaType.id) &&
                Objects.equals(code, mcdaType.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}