package com.yihu.ehr.basic.portal.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_notices", schema = "", catalog = "healtharchive")
public class PortalNotices {
    private Long id;
    private Integer type;
    private Integer portalType;
    private String title;
    private String content;
    private String fileId;
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
    @Column(name = "type", nullable = true, insertable = true, updatable = true)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "portal_type", nullable = true, insertable = true, updatable = true)
    public Integer getPortalType() {  return portalType;}

    public void setPortalType(Integer portalType) {this.portalType = portalType;}


    @Basic
    @Column(name = "title", nullable = true, insertable = true, updatable = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "content", nullable = true, insertable = true, updatable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Basic
    @Column(name = "file_id", nullable = true, insertable = true, updatable = true)
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

        PortalNotices that = (PortalNotices) o;

        if (id != that.id) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (fileId != null ? !fileId.equals(that.fileId) : that.fileId != null) return false;
        if (releaseAuthor != null ? !releaseAuthor.equals(that.releaseAuthor) : that.releaseAuthor != null)
            return false;
        if (releaseDate != null ? !releaseDate.equals(that.releaseDate) : that.releaseDate != null) return false;

        return true;
    }

}
