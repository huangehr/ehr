package com.yihu.ehr.browser.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:09
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DataSetModel {
    public String dataSetCode;
    public String dataKeys = "";
}
