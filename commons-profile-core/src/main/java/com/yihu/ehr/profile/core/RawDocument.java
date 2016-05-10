package com.yihu.ehr.profile.core;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 非结构化档案类。包含一个文档地址中的信息。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class RawDocument {
    private String cdaDocumentId;           // CDA文档ID
    private String originUrl;               // 机构健康档案中的地址
    private String mimeType;
    private Date expireDate;

    // 文件索引，key为文件名
    private Map<String, String> storageUrls = new TreeMap<>();

    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Map<String, String> getStorageUrls(){
        return storageUrls;
    }

    public String formatStorageUrls(){
        StringBuilder builder = new StringBuilder();
        for (String fileName : storageUrls.keySet()){
            builder.append(fileName).append(":").append(storageUrls.get(fileName)).append(";");
        }

        return builder.toString();
    }

    public void addStorageUrl(String fileName, String storageUrl){
        storageUrls.put(fileName, storageUrl);
    }
}