package com.yihu.ehr.model.portal;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yeshijie on 2017/2/17.
 */
public class MPortalNotice implements Serializable {

    private Long id;
    private Integer type;
    private String title;
    private String content;
    private String fileId;
    private String releaseAuthor;
    private Date releaseDate;

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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
