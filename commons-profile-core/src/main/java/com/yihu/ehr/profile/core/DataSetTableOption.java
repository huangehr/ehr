package com.yihu.ehr.profile.core;

import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 9:52
 */
public class DataSetTableOption {
    // 列族
    public enum Family {
        Basic("basic"),
        MetaData("meta_data"),
        Extension("extension");

        private String family;

        Family(String family) {
            this.family = family;
        }

        public String toString() {
            return family;
        }
    }

    // 列
    public enum BasicQualifier {
        ProfileId("archive_id"),
        InnerVersion("inner_version"),
        LastUpdateTime("last_update_time");

        private String qualifier;

        BasicQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String toString() {
            return qualifier;
        }
    }

    /**
     * 获取列族。
     *
     * @return
     */
    public static String[] getFamilies() {
        return new String[]{
                Family.Basic.toString(),
                Family.MetaData.toString(),
                Family.Extension.toString()
        };
    }

    /**
     * 获取指定族的列.
     *
     * @return
     */
    public static String[] getQualifiers(Family family) {
        if (family == Family.Basic) {
            return new String[]{
                    BasicQualifier.ProfileId.toString(),
                    BasicQualifier.InnerVersion.toString(),
                    BasicQualifier.LastUpdateTime.toString()
            };
        } else if (family == Family.MetaData){
            return null;
        }

        return null;
    }

    /**
     * 解析每一个数据集记录, 并其中的列名与值解包, 并以二维数组的形式返回. 此数据存储到 meta_data 列.
     *
     * @param record
     * @return
     */
    public static String[][] metaDataToQualifier(Map<String, String> record) {
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
