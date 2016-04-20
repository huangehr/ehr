package com.yihu.ehr.model.profile;

import java.util.Date;

/**
 * 档案索引.
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 17:11
 */
public class MProfileIndices {
    private String rowkey;
    private Date eventDate;
    private String demographicId;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public void setDemographicId(String demographicId) {
        this.demographicId = demographicId;
    }
}
