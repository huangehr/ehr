package com.yihu.ehr.standard.cdatype.service;
import com.yihu.ehr.util.ObjectVersion;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * CDA类型
 *
 * @author AndyCai
 * @version 1.0
 * @created 11-12月-2015 15:52:22
 */
@Entity
@Table(name = "std_cda_type")
@Access(value = AccessType.PROPERTY)
public class CDAType {

    private String id;
    private String code;
    private String name;
    private String parentId;
    private Date createDate;
    private String createUser;
    private Date updateDate;
    private String updateUser;
    private String description;

    public CDAType() {
        id  = UUID.randomUUID().toString().replace("-","");
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "code",  nullable = false)
    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return this.code;
    }

    @Column(name = "name",  nullable = false)
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    @Column(name = "parent_id",  nullable = true)
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    public String getParentId() {
        return this.parentId;
    }

    @Column(name = "create_date",  nullable = true)
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getCreateDate() {
        return this.createDate;
    }

    @Column(name = "create_user",  nullable = true)
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public String getCreateUser() {
        return this.createUser;
    }

    @Column(name = "update_date",  nullable = true)
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    public Date getUpdateDate() {
        return this.updateDate;
    }

    @Column(name = "update_user",  nullable = true)
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public String getUpdateUser() {
        return this.updateUser;
    }

//    public CDAType getParentCdaType() {
//
////        XCDATypeManager xcdaTypeManager = ServiceFactory.getService(Services.CDATypeManager);
////        List<XCDAType> listType = xcdaTypeManager.getCdatypeInfoByIds(this.parentId);
////        if (listType.size() > 0) {
////            return (CDAType) listType.get(0);
////        }
//        return null;
//    }

    @Column(name = "description",  nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}