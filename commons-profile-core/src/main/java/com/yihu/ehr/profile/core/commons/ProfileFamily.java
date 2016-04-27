package com.yihu.ehr.profile.core.commons;

import java.util.Map;

/**
 * HBase数据库中健康档案表选项. 如列族名称等.
 * 健康档案列族结构: |basic|extension|
 * 健康事件列族结构: |basic|extension|
 * 数据集列族结构: |basic|meta_data|extension|
 * @author Sand
 * @created 2016.04.27 17:06
 */
public enum ProfileFamily {
    Basic("basic"),
    Extension("extension");

    private String family;

    ProfileFamily(String family){
        this.family = family;
    }

    public String toString(){
        return family;
    }

    // 列
    public enum BasicQualifier{
        CardId("card_id"),
        OrgCode("org_code"),
        PatientId("patient_id"),
        EventNo("event_no"),
        EventDate("event_date"),
        EventType("event_type"),
        ProfileType("profile_type"),
        Summary("summary"),
        DemographicId("demographic_id"),
        CreateDate("create_date"),
        DataSets("data_sets"),
        CdaVersion("inner_version");

        private String qualifier;

        BasicQualifier(String qualifier){
            this.qualifier = qualifier;
        }

        public String toString(){
            return qualifier;
        }
    }

    /**
     * 获取列族.
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                ProfileFamily.Basic.toString(),
                ProfileFamily.Extension.toString()
        };
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getColumns(ProfileFamily family) {
        if (family == ProfileFamily.Basic) {
            return new String[]{
                    BasicQualifier.CardId.toString(),
                    BasicQualifier.OrgCode.toString(),
                    BasicQualifier.PatientId.toString(),
                    BasicQualifier.EventNo.toString(),
                    BasicQualifier.EventDate.toString(),
                    BasicQualifier.Summary.toString(),
                    BasicQualifier.DemographicId.toString(),
                    BasicQualifier.CreateDate.toString(),
                    BasicQualifier.DataSets.toString(),
                    BasicQualifier.CdaVersion.toString()
            };
        }

        return null;
    }
}
