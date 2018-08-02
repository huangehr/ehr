package com.yihu.ehr.basic.user.entity;

import com.yihu.ehr.entity.BaseIdentityEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_type_roles")
@Access(value = AccessType.PROPERTY)
public class UserTypeRoles extends BaseIdentityEntity {

    private int typeId;
    private String typeName;
    private String clientId;
    private String clientName;
    private long roleId;
    private String roleName;

    public UserTypeRoles() {
    }

    @Column(name = "type_id",  nullable = true)
    public int getTypeId() {
        return typeId;
    }
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Column(name = "type_name",  nullable = true)
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Column(name = "client_id",  nullable = true)
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Column(name = "client_name",  nullable = true)
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Column(name = "role_id",  nullable = true)
    public long getRoleId() {
        return roleId;
    }
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "role_name",  nullable = true)
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}