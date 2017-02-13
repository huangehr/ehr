package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "portal_standards")
@Access(value = AccessType.FIELD)
public class PortalStandards {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "mdfile_path", unique = true, nullable = false)
    private String mdfilePath;

    @Column(name = "release_flag", unique = true, nullable = false)
    private int releaseFlag;

    @Column(name = "version", unique = true, nullable = false)
    private String version;

    @Column(name = "release_author", unique = true, nullable = false)
    private String releaseAuthor;

    @Column(name = "release_date", unique = true, nullable = false)
    private Date releaseDate;

    public PortalStandards() {}

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

    public String getMdfilePath() {
        return mdfilePath;
    }

    public void setMdfilePath(String mdfilePath) {
        this.mdfilePath = mdfilePath;
    }

    public int getReleaseFlag() {
        return releaseFlag;
    }

    public void setReleaseFlag(int releaseFlag) {
        this.releaseFlag = releaseFlag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseAuthor() {
        return releaseAuthor;
    }

    public void setReleaseAuthor(String releaseAuthor) {
        this.releaseAuthor = releaseAuthor;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}