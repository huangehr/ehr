package com.yihu.ehr.paient.service.card;

/**
 * Created by zqb on 2015/8/20.
 */
public class CardBrowseModel {
    String  objectId="";
    Integer order=null;
    String  type="";
    String  typeValue="";
    String  number="";
    String  releaseOrg="";
    String  createDate="";
    String  status="";
    String  statusValue="";

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type==null?"":type;
    }

    public String getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number==null?"":number;
    }

    public String getReleaseOrg() {
        return releaseOrg;
    }

    public void setReleaseOrg(String releaseOrg) {
        this.releaseOrg = releaseOrg==null?"":releaseOrg;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status==null?"":status;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }
}
