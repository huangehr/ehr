package com.yihu.ehr.model.profilestatistics;

import java.util.Date;

/**
 * Created by lyr on 2016/7/27.
 */
public class MDailyMonitorFile {

    private String id;
    private String monitorDate;
    private String fileName;
    private Date createTime;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(String monitorDate) {
        this.monitorDate = monitorDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

