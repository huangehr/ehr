package com.yihu.ehr.basic.portal.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_standards", schema = "", catalog = "healtharchive")
public class PortalStandards {
    private Long id;
    private String name;
    private String mdfilePath;
    private Integer releaseFlag;
    private String version;
    private String releaseAuthor;
    private Date releaseDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    @Column(name = "mdfile_path", nullable = true, insertable = true, updatable = true)
    public String getMdfilePath() {
        return mdfilePath;
    }

    public void setMdfilePath(String mdfilePath) {
        this.mdfilePath = mdfilePath;
    }

    @Basic
    @Column(name = "release_flag", nullable = true, insertable = true, updatable = true)
    public Integer getReleaseFlag() {
        return releaseFlag;
    }

    public void setReleaseFlag(Integer releaseFlag) {
        this.releaseFlag = releaseFlag;
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
    @Column(name = "release_author", nullable = true, insertable = true, updatable = true)
    public String getReleaseAuthor() {
        return releaseAuthor;
    }

    public void setReleaseAuthor(String releaseAuthor) {
        this.releaseAuthor = releaseAuthor;
    }

    @Basic
    @Column(name = "release_date", nullable = true, insertable = true, updatable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalStandards that = (PortalStandards) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (mdfilePath != null ? !mdfilePath.equals(that.mdfilePath) : that.mdfilePath != null) return false;
        if (releaseFlag != null ? !releaseFlag.equals(that.releaseFlag) : that.releaseFlag != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (releaseAuthor != null ? !releaseAuthor.equals(that.releaseAuthor) : that.releaseAuthor != null)
            return false;
        if (releaseDate != null ? !releaseDate.equals(that.releaseDate) : that.releaseDate != null) return false;

        return true;
    }

}
