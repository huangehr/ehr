package com.yihu.ehr.model.profile;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

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
