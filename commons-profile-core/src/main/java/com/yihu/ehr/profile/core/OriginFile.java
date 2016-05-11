package com.yihu.ehr.profile.core;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 非结构化档案原始文件。包含一个文档地址中的信息。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class OriginFile {
    private String originUrl;               // 机构健康档案中的地址
    private String mime;
    private Date expireDate;

    // 文件索引，key为文件名
    private Map<String, String> fileUrls = new TreeMap<>();

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String fileUrl) {
        this.originUrl = fileUrl;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public Map<String, String> getFileUrls(){
        return fileUrls;
    }

    public void addStorageUrl(String fileName, String storageUrl){
        fileUrls.put(fileName, storageUrl);
    }
}