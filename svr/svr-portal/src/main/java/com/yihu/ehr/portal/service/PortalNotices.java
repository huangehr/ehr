package com.yihu.ehr.portal.service;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "portal_notices", schema = "", catalog = "healtharchive")
public class PortalNotices {
    private int id;
    private Integer type;
    private String content;
    private String fileId;
    private String releaseAuthor;
    private Timestamp releaseDate;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
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

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (fileId != null ? fileId.hashCode() : 0);
        result = 31 * result + (releaseAuthor != null ? releaseAuthor.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        return result;
    }
}
