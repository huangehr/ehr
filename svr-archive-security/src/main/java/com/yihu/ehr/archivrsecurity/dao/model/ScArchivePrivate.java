package com.yihu.ehr.archivrsecurity.dao.model;

import javax.persistence.*;

/**
 * Created by lyr on 2016/7/11.
 */
@Entity
@Table(name="sc_archive_private")
public class ScArchivePrivate {

    private String id;
    private String userId;
    private String rowKey;
    private int status;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique = true,nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="user_id",nullable = false)
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="row_key",nullable = false)
    public String getRowKey() {
        return rowKey;
    }
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    @Column(name="status")
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
