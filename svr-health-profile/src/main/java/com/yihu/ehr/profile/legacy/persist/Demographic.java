package com.yihu.ehr.profile.legacy.persist;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.20 15:42
 */
public class Demographic {
    public final static String DemographicCore = "HDSA00_01";

    public final static String IdCardNo = "HDSA00_01_017_S";
    public final static String LegacyName = "HDSA00_01_009_S";
    public final static String Name = "HDSD00_01_002_S";
    public final static String LegacyTelephone = "HDSA00_01_019_S";
    public final static String Telephone = "HDSD00_01_008_S";
    public final static String LegacyGender = "HDSA00_01_011_VALUE_S";
    public final static String Gender = "HDSA00_01_016_VALUE_S";
    public final static String Birthday = "HDSA00_01_012_D";

    @Field
    String id;

    @Field("rowkey")
    String rowkey;

    @Field(IdCardNo)
    String demographicId;

    @Field(LegacyName)
    String legacyName;

    @Field(Name)
    String name;

    @Field(LegacyGender)
    String legacyGender;

    @Field(Gender)
    String gender;

    @Field(LegacyTelephone)
    String legacyTelephone;

    @Field(Telephone)
    String telephone;

    @Field(Birthday)
    Date birthday;

    public String getProfileId(){
        return this.rowkey.substring(0, this.rowkey.indexOf("$"));
    }

    public String getRowkey() {
        return rowkey;
    }

    public String getDemographicId() {
        return demographicId;
    }

    public String getName() {
        return StringUtils.isNotEmpty(legacyName) ? legacyName : name;
    }

    public String getGender() {
        return StringUtils.isNotEmpty(legacyGender) ? legacyGender : gender;
    }

    public String getTelephone() {
        return StringUtils.isNotEmpty(legacyTelephone) ? legacyTelephone : telephone;
    }

    public Date getBirthday() {
        return birthday;
    }
}
