package com.yihu.ehr.service.resource.stage1;

import com.yihu.ehr.constants.UrlScope;

import java.util.Collection;
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
    private String mime;
    private Date expireDate;
    private UrlScope urlScope;

    // 文件索引，key为文件名
    private Map<String, String> fileUrls = new TreeMap<>();

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

    public void addUrl(String fileName, String storageUrl){
        fileUrls.put(fileName, storageUrl);
    }

    public UrlScope getUrlScope() {
        return urlScope;
    }

    public void setUrlScope(UrlScope urlScope) {
        this.urlScope = urlScope;
    }

    public String getUrlsStr() {
        Collection values = fileUrls.values();
        String urls = "";
        for (Object object : values) {
            urls+=object.toString();
        }
        return urls;
    }

}