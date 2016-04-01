package com.yihu.ehr.profile;

import java.util.Map;

/**
 * HBase数据库中健康档案表选项. 如列族名称等.
 * 健康档案列族结构: |basic|extension|
 * 健康事件列族结构: |basic|extension|
 * 数据集列族结构: |basic|meta_data|extension|
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.23 18:11
 */
public class ProfileTableOptions {
    public enum Table {
        ArchiveTable,
        DataSetTable
    }

    public enum TableFamily {
        Basic,
        Extension,
        MetaData
    }

    public static final String ProfileTable = "HealthArchives";           // 健康档案表

    /*通用列族 */
    public static final String FamilyBasic = "basic";
    public static final String FamilyExtension = "extension";

    /* 数据集表特有列族 */
    public static final String FamilyMetaData = "meta_data";

    /* 健康档案basic列 */
    public static final String AcCardId = "card_id";
    public static final String AcOrgCode = "org_code";
    public static final String AcPatientId = "patient_id";
    public static final String AcEventNo = "event_no";
    public static final String AcEventDate = "event_date";
    public static final String AcSummary = "summary";
    public static final String AcDemographicId = "demographic_id";
    public static final String AcCreateDate = "create_date";
    public static final String AcDataSets = "data_sets";
    public static final String AcInnerVersion = "inner_version";

    /* 数据集basic列 */
    public static final String DScArchiveId = "archive_id";
    public static final String DScOrgCode = "org_code";
    public static final String DScPatientId = "patient_id";
    public static final String DScEventNo = "event_no";
    public static final String DScInnerVersion = "inner_version";
    public static final String DScLastUpdateTime = "last_update_time";

    /* 数据集 extension 列*/
    public static final String DScOriginFileURL = "origin_file_url";

    /**
     * 获取指定表的列族.
     *
     * @return
     */
    public static String[] getColumnFamilies(Table table) {
        switch (table) {
            case ArchiveTable:
                return new String[]{
                        FamilyBasic,
                        FamilyExtension
                };

            case DataSetTable:
                return new String[]{
                        FamilyBasic,
                        FamilyMetaData,
                        FamilyExtension
                };

            default:
                throw new IllegalArgumentException("未知表类型");
        }
    }

    /**
     * 获取指定表族的列.
     *
     * @return
     */
    public static String[] getColumns(Table table, TableFamily family) {
        if (family == TableFamily.Basic) {
            if (table == Table.ArchiveTable) {
                return new String[]{
                        AcCardId,
                        AcOrgCode,
                        AcPatientId,
                        AcEventNo,
                        AcEventDate,
                        AcSummary,
                        AcDemographicId,
                        AcCreateDate,
                        AcDataSets,
                        AcInnerVersion
                };
            } else if (table == Table.DataSetTable) {
                return new String[]{
                        DScArchiveId,
                        DScOrgCode,
                        DScPatientId,
                        DScEventNo,
                        DScInnerVersion,
                        DScLastUpdateTime
                };
            }
        } else if (family == TableFamily.Extension){
            if (table == Table.DataSetTable) {
                return new String[]{
                        DScOriginFileURL
                };
            }
        }

        return null;
    }

    /**
     * 解析每一个数据集记录, 并其中的列名与值解包, 并以二维数组的形式返回. 此数据存储到 meta_data 列.
     *
     * @param record
     * @return
     */
    public static String[][] dataSetRecordToHBaseColumn(Map<String, String> record) {
        final int NAME = 0;
        final int VALUE = 1;

        String[][] array = new String[2][record.size()];

        int i = 0;
        for (String key : record.keySet()) {
            array[NAME][i] = key;
            array[VALUE][i++] = record.get(key);
        }

        return array;
    }
}
