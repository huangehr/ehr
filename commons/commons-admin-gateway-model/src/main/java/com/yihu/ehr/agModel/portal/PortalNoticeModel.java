package com.yihu.ehr.agModel.portal;

/**
 * Created by yeshijie on 2017/2/17.
 */
public class PortalNoticeModel {
    private Long id;
    private Integer type;
    private String typeName;
    private Integer portalType;
    private String portalTypeName;
    private String title;
    private String content;
    private String fileId;
    private String releaseAuthor;
    private String releaseDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPortalType() { return portalType;}

    public void setPortalType(Integer portalType) {this.portalType = portalType;}

    public String getPortalTypeName() { return portalTypeName;}

    public void setPortalTypeName(String portalTypeName) {this.portalTypeName = portalTypeName;}

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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
