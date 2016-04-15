package com.yihu.ehr.profile.core.unStructured;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
public class UnStructuredDocument {
    String cdaDocId;
    String url;
    Date expiryDate;

//    List<Map<String,Object>> keyWordsList;
    String keyWordsStr;
    //非结构化content内容 类表包含两个字典 mime_type 和 name 和fastdfs保存的地址
    private List<UnStructuredContent> unStructuredContentList;

    public String getCdaDocId() {
        return cdaDocId;
    }

    public void setCdaDocId(String cdaDocId) {
        this.cdaDocId = cdaDocId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getKeyWordsStr() {
        return keyWordsStr;
    }

    public void setKeyWordsStr(String keyWordsStr) {
        this.keyWordsStr = keyWordsStr;
    }

    public List<UnStructuredContent> getUnStructuredContentList() {
        return unStructuredContentList;
    }

    public void setUnStructuredContentList(List<UnStructuredContent> unStructuredContentList) {
        this.unStructuredContentList = unStructuredContentList;
    }
}