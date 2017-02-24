package com.yihu.ehr.model.portal;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yeshijie on 2017/2/21.
 */
public class MPortalStandards implements Serializable {

    private Long id;
    private String name;
    private String mdfilePath;
    private Integer releaseFlag;
    private String version;
    private String releaseAuthor;
    private Date releaseDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Integer getReleaseFlag() {
        return releaseFlag;
    }

    public void setReleaseFlag(Integer releaseFlag) {
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
