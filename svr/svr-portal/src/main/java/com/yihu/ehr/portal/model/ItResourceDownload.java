package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "it_resource_download")
@Access(value = AccessType.FIELD)
public class ItResourceDownload {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "version", unique = true, nullable = false)
    private String version;

    @Column(name = "platform_type", unique = true, nullable = false)
    private String platformType;

    @Column(name = "pakage_type", unique = true, nullable = false)
    private String pakageType;

    @Column(name = "description", unique = true, nullable = false)
    private String description;

    @Column(name = "develop_lan", unique = true, nullable = false)
    private String developLan;

    @Column(name = "validate_type", unique = true, nullable = false)
    private String validateType;

    public ItResourceDownload() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public String getPakageType() {
        return pakageType;
    }

    public void setPakageType(String pakageType) {
        this.pakageType = pakageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDevelopLan() {
        return developLan;
    }

    public void setDevelopLan(String developLan) {
        this.developLan = developLan;
    }

    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }
}