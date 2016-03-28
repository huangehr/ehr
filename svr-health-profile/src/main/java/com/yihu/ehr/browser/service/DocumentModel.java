package com.yihu.ehr.browser.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:09
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DocumentModel {
    public String cdaVersion;
    public String cdaDocumentId;
    public String cdaDocumentName;
    public String orgCode;

    public List<DataSetModel> dataSets = new ArrayList<>();
}
