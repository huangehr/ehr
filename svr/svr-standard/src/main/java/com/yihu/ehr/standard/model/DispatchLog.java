package com.yihu.ehr.standard.model;

import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@Entity
@Table(name = "std_dispatch_log")
@Access(value = AccessType.PROPERTY)
public class DispatchLog {
    @Value("${admin-region}")
    short adminRegion;

    private String id;
    private String orgId;
    private String stdVersionId;
    private Date dispatchTime;
    private String filePath;
    private String fileGroup;
    private String password;

    public void createId(){
        Object objectID = new ObjectId(adminRegion, BizObject.StdProfile);
        setId(objectID.toString());
    }
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

    @Column(name = "org_id", unique = false, nullable = false)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "std_version_id", unique = false, nullable = false)
    public String getStdVersionId() {
        return stdVersionId;
    }

    public void setStdVersionId(String stdVersionId) {
        this.stdVersionId = stdVersionId;
    }

    @Column(name = "dispatch_time", unique = false, nullable = true)
    public Date getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(Date dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    @Column(name = "file_path", unique = false, nullable = true)
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "file_group", unique = false, nullable = true)
    public String getFileGroup() {
        return fileGroup;
    }

    public void setFileGroup(String fileGroup) {
        this.fileGroup = fileGroup;
    }

    @Column(name = "password", unique = false, nullable = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String passsword) {
        this.password = passsword;
    }
}
