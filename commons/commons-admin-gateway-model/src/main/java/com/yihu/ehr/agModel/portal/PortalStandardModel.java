package com.yihu.ehr.agModel.portal;

/**
 * Created by yeshijie on 2017/2/21.
 */
public class PortalStandardModel {
    private Long id;
    private String name;
    private String mdfilePath;
    private Integer releaseFlag;
    private String version;
    private String releaseAuthor;
    private String releaseDate;

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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
