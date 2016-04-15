package com.yihu.ehr.profile.core;

import com.yihu.ehr.profile.core.ProfileDataSet;

import javax.activation.MimeType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 非结构化健康档案的文档。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 10:44
 */
public class ProfileDocument {
    String cdaDocumentId;
    String url;
    Date expireDate;
    Map<String, ProfileDataSet> keywords;
    List<Files> content;

    static class Files{
        public MimeType mimeType = new MimeType();
        public List<String> fileList = new ArrayList<>();
    }
}