package com.yihu.ehr.model.archivesecurity;

/**
 * Created by lyr on 2016/7/11.
 */
public class MScArchivePrivate {

    private String id;
    private String userId;
    private String rowKey;
    private int status;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRowKey() {
        return rowKey;
    }
    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
