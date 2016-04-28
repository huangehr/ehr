package com.yihu.ehr.profile.core.profile;

import com.yihu.ehr.util.StringBuilderUtil;

import javax.activation.MimeType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 非结构化档案类。包含一个文档地址中的信息。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class RawDocument extends ArrayList<String> {
    String cdaDocumentId;
    String url;         // 机构健康档案中的地址

    MimeType mimeType;

    Date expireDate;

    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String fileUrl) {
        this.url = fileUrl;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getFileList(){
        StringBuilder builder = new StringBuilder();
        for (String storagePath : this){
            builder.append(storagePath).append(";");
        }

        return builder.toString();
    }
}