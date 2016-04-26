package com.yihu.ehr.profile.core.nostructured;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
public class RawDocument {
    String cdaDocId;
    String url;
    Date expiryDate;
    String keyWordsStr;

    //非结构化content内容 类表包含两个字典 mime_type 和 name 和fastdfs保存的地址
    private List<NoStructuredContent> noStructuredContentList;

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

    public List<NoStructuredContent> getNoStructuredContentList() {
        return noStructuredContentList;
    }

    public void setNoStructuredContentList(List<NoStructuredContent> noStructuredContentList) {
        this.noStructuredContentList = noStructuredContentList;
    }
}