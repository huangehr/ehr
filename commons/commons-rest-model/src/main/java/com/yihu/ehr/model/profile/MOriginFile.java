package com.yihu.ehr.model.profile;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Sand
 * @created 2016.05.09 18:56
 */
public class MOriginFile {
    String mime;
    String originUrl;
    Date expireDate;

    Map<String, String> files = new TreeMap<>();

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public void setFiles(Map<String, String> files) {
        this.files = files;
    }
}
