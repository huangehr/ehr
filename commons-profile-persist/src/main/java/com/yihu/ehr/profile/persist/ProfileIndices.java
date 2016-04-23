package com.yihu.ehr.profile.persist;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.05 17:33
 */
@SolrDocument(solrCoreName = "HealthArchives")
public class ProfileIndices {
    @Field
    private String id;  // 没有数据

    @Field
    private String rowkey;

    @Field("event_date")
    private Date eventDate;

    @Field("demographic_id")
    private String demographicId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return rowkey.contains("$") ? rowkey.substring(0, rowkey.indexOf("$")) : rowkey;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public Date getEventDate() {
        return eventDate;
    }
}
