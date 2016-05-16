package com.yihu.ehr.agModel.esb;// default package

/**
 * @author linaz
 * @created 2016.05.12 18:03
 */
public class HosSqlTaskModel  {

    String id;
    String orgCode;
    String systemCode;
    String sqlscript;
    String result;
    String status;
    String message;
    String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSqlscript() {
        return sqlscript;
    }

    public void setSqlscript(String sqlscript) {
        this.sqlscript = sqlscript;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}