package com.yihu.ehr.portal.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource", schema = "", catalog = "healtharchive")
public class ItResource {
    private int id;
    private String name;
    private String version;
    private String platformType;
    private String packageType;
    private String description;
    private String developLan;
    private String validateType;
    private Integer status;
    private String url;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "version", nullable = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "platform_type", nullable = true)
    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    @Column(name = "package_type", nullable = true)
    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "develop_lan", nullable = true)
    public String getDevelopLan() {
        return developLan;
    }

    public void setDevelopLan(String developLan) {
        this.developLan = developLan;
    }

    @Column(name = "validate_type", nullable = true)
    public String getValidateType() {
        return validateType;
    }

    public void setValidateType(String validateType) {
        this.validateType = validateType;
    }

    @Column(name = "status", nullable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "url", nullable = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResource that = (ItResource) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (platformType != null ? !platformType.equals(that.platformType) : that.platformType != null) return false;
        if (packageType != null ? !packageType.equals(that.packageType) : that.packageType != null) return false;
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
        result = 31 * result + (packageType != null ? packageType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (developLan != null ? developLan.hashCode() : 0);
        result = 31 * result + (validateType != null ? validateType.hashCode() : 0);
        return result;
    }
}
