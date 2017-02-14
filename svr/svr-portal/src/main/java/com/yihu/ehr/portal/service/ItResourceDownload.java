package com.yihu.ehr.portal.service;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource_download", schema = "", catalog = "healtharchive")
public class ItResourceDownload {
    private int id;
    private String name;
    private String version;
    private String platformType;
    private String pakageType;
    private String description;
    private String developLan;
    private String validateType;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "version", nullable = true, insertable = true, updatable = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Basic
    @Column(name = "platform_type", nullable = true, insertable = true, updatable = true)
    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    @Basic
    @Column(name = "pakage_type", nullable = true, insertable = true, updatable = true)
    public String getPakageType() {
        return pakageType;
    }

    public void setPakageType(String pakageType) {
        this.pakageType = pakageType;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "develop_lan", nullable = true, insertable = true, updatable = true)
    public String getDevelopLan() {
        return developLan;
    }

    public void setDevelopLan(String developLan) {
        this.developLan = developLan;
    }

    @Basic
    @Column(name = "validate_type", nullable = true, insertable = true, updatable = true)
    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResourceDownload that = (ItResourceDownload) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (platformType != null ? !platformType.equals(that.platformType) : that.platformType != null) return false;
        if (pakageType != null ? !pakageType.equals(that.pakageType) : that.pakageType != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (developLan != null ? !developLan.equals(that.developLan) : that.developLan != null) return false;
        if (validateType != null ? !validateType.equals(that.validateType) : that.validateType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (platformType != null ? platformType.hashCode() : 0);
        result = 31 * result + (pakageType != null ? pakageType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (developLan != null ? developLan.hashCode() : 0);
        result = 31 * result + (validateType != null ? validateType.hashCode() : 0);
        return result;
    }
}
