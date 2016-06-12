package com.yihu.ehr.standard.version.service;

import com.yihu.ehr.util.ObjectVersion;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Entity
@Table(name = "std_cda_versions")
@Access(value = AccessType.PROPERTY)
public class CDAVersion{
    private String author;
    private Date commitTime;
    private boolean inStage;
    private ObjectVersion version;
    private String versionName;
    private ObjectVersion baseVersion;

    public CDAVersion() {
    }

    public CDAVersion(String baseVersion, String author, String versionName) {
        //this.baseVersion = baseVersion == null ? null : new ObjectVersion(baseVersion);
        this.author = author;
        this.commitTime = null;
        this.inStage = true;
        this.version = new ObjectVersion();
        if (baseVersion == null) {
            this.versionName = versionName;
            this.baseVersion = null;
        } else {
            versionName = versionName.substring(1, versionName.length());
            double dVersion = Double.parseDouble(versionName) + 1.0;
            this.versionName = "V" + String.valueOf(dVersion);

            this.baseVersion = new ObjectVersion(baseVersion);
        }
    }

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "version", unique = true, nullable = false)
    @Access(value = AccessType.PROPERTY)
    public String getVersion() {
        if(version==null)return "";
        return version.toString();
    }
    public void setVersion(String version) {
        this.version = new ObjectVersion(version);
    }

    @Column(name = "author", unique = false, nullable = false)
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "base_version", unique = false, nullable = true)
    @Access(value = AccessType.PROPERTY)
    public String getBaseVersion() {
        if (baseVersion == null)
            return null;
        return baseVersion.toString();
    }
    public void setBaseVersion(String baseVersion) {
        this.baseVersion = StringUtils.isEmpty(baseVersion) ? null : new ObjectVersion(baseVersion);
    }

    @Column(name = "commit_time", unique = false, nullable = true)
    public Date getCommitTime() {
        return commitTime;
    }
    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }

    @Column(name = "version_name", unique = false, nullable = true)
    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Column(name = "staged", unique = false, nullable = true )
    public boolean isInStage() {
        return inStage;
    }

    public void setInStage(boolean inStage) {
        this.inStage = inStage;
    }
}