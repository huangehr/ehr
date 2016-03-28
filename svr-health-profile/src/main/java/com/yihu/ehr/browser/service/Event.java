package com.yihu.ehr.browser.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Event{
    public String date;
    public String summary;
    public String orgName;

    public String orgCode;
    public String diagnosisName = "";
    public String diagnosisCode = "";

    public List<DocumentModel> documents = new ArrayList<>();
}
