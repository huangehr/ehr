package com.yihu.ehr.model.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @created 2016.05.09 18:56
 */
public class MRawDocument {
    String originUrl;
    Date expireDate;
    List<String> files = new ArrayList<>();

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
