package com.yihu.ehr.profile.controller.portals.sanofi;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.20 15:43
 */
public class Lis {
    public final static String LisCore = "HDSD00_08";

    public final static String ChnName = "JDSD02_03_13_S";
    public final static String EngName = "JDSD02_03_14_S";
    public final static String Value = "JDSD02_03_04_S";
    public final static String Type = "JDSD02_03_05_S";
    public final static String Unit = "HDSD00_01_547_S";
    public final static String UpperBound = "JDSD02_03_06_S";
    public final static String LowerBound = "JDSD02_03_07_S";
    public final static String CreateDate = "JDSD02_03_10_D";

    @Field(ChnName)
    String chnName;

    @Field(EngName)
    String engName;

    @Field(Value)
    String value;

    @Field(Type)
    String type;

    @Field(Unit)
    String unit;

    @Field(UpperBound)
    String upperBound;

    @Field(LowerBound)
    String lowerBound;

    @Field(CreateDate)
    Date createDate;

    public String getChnName() {
        return chnName;
    }

    public String getEngName() {
        return engName;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public Date getCreateDate() {
        return createDate;
    }
}
