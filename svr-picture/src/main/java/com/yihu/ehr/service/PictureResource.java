package com.yihu.ehr.service;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Entity
@Table(name = "file_resource")
@Access(value = AccessType.PROPERTY)
public class PictureResource {

    private String id;                   // objectId
    private String storagePath;          // FDS path
    private String mime;                 // user/org/patient 冗余字段，后继图片管理用
    private String objectId;             // userId/orgCode/patientId 各类编号都是唯一
    private String usage;                // face/credentials  用途 头像 资质认证
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "storage_path",  nullable = true)
    public String getStoragePath() {
        return storagePath;
    }
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    @Column(name = "mime",  nullable = true)
    public String getMime() {
        return mime;
    }
    public void setMime(String mime) {
        this.mime = mime;
    }

    @Column(name = "object_id",  nullable = true)
    public String getObjectId() {
        return objectId;
    }
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Column(name = "usage",  nullable = true)
    public String getUsage() {
        return usage;
    }
    public void setUsage(String usage) {
        this.usage = usage;
    }

    @Column(name = "create_date",  nullable = true)
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "create_user",  nullable = true)
    public String getCreateUser() {
        return createUser;
    }
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @Column(name = "update_date",  nullable = true)
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name = "update_user",  nullable = true)
    public String getUpdateUser() {
        return updateUser;
    }
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}