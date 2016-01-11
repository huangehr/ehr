package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.BizObject;
import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.util.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.environment.Environment;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 16:54:17
 */
public class CDADocument {

    @Value("${admin_region}")
    short adminRegion;

    private String code;
    private Date create_date;
    private String create_user;
    private String description;
    private String id;
    private String name;
    private Date update_date;
    private String update_user;
    private String version_code;

    Environment environment;
    /**
     * 输出排版路径
     */
    private String print_out;
    private String schema;
    /**
     * 标准来源ID
     */
    private String source_id;

    @DifferIgnored
    private int hashCode;

    @Autowired
    private CDAVersionManager cdaVersionManager;

    @Autowired
    private CDADatasetRelationshipManager xCDADatasetRelationshipManager;

    private StandardSourceManager xStandardSourceManager;

    public CDADocument() {
        this.create_date = new Date();
        this.create_user = "Sys";

        Object objectID = new ObjectId(adminRegion, BizObject.StdArchive);
        id = objectID.toString();

        this.OperationType = "";
    }

    public void finalize() throws Throwable {
    }

    public String getPrintOut() {
        return this.print_out;
    }

    public String getSchema() {
        return this.schema;
    }

    public StandardSource getSource() {
        if (this.source_id == null || this.source_id == "")
            return null;
        List<String> listIds = new ArrayList<>();
        listIds.add(this.source_id);
        StandardSource[] infos = xStandardSourceManager.getSourceById(listIds);
        if (infos == null)
            return null;
        return infos[0];
    }

    public String getSource_id() {
        return this.source_id;
    }

    /**
     * @param print_out
     */
    public void setPrintOut(String print_out) {
        this.print_out = print_out;
    }

    /**
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @param source_id
     */
    public void setSourceId(String source_id) {
        this.source_id = source_id;
    }

    public String getCode() {
        return this.code;
    }

    public Date getCreateDate() {
        return this.create_date;
    }

    public String getCreateUser() {
        return this.create_user;
    }

    public String getDescription() {
        return this.description;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Date getUpdateDate() {
        return this.update_date;
    }

    public String getUpdateUser() {
        return this.update_user;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param create_date
     */
    public void setCreateDate(Date create_date) {
        this.create_date = create_date;
    }

    /**
     * @param create_user
     */
    public void setCreateUser(String create_user) {
        this.create_user = create_user;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param update_date
     */
    public void setUpdateDate(Date update_date) {
        this.update_date = update_date;
    }

    /**
     * @param update_user
     */
    public void setUpdateUser(String update_user) {
        this.update_user = update_user;
    }

    public void setVersionCode(String versionCode) {
        this.version_code = versionCode;
    }

    public String getVersionCode() {
        return this.version_code;
    }

    public CDAVersion getVersion() {

        if (this.version_code == null || this.version_code == "")
            return null;
        return cdaVersionManager.getVersion(this.version_code);
    }

    public int getHashCode() {
        hashCode = Objects.hash(id, code, create_date, create_user, name, print_out, schema, source_id);
        return hashCode;
    }

    public CDADatasetRelationship[] getRelationship() {
        if (this.id == null || this.id == "" || this.version_code == null || this.version_code == "")
            return null;
        return xCDADatasetRelationshipManager.getRelationshipByCDAId(this.id, this.version_code);
    }

    public String getOperationType() {
        return OperationType;
    }

    public void setOperationType(String operationType) {
        OperationType = operationType;
    }

    String OperationType;

}//end CDADocument