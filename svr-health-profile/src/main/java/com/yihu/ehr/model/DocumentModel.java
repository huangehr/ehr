package com.yihu.ehr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CWS
 * @version 1.0
 * @created 2015.12.26 16:08
 */
public class DocumentModel {
    public String cdaVersion;
    public String cdaDocumentId;
    public String cdaDocumentName;
    public String orgCode;

    public List<MDataSet> dataSets = new ArrayList<>();
}
