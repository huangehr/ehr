package com.yihu.ehr.profile.core.commons;

/**
 * 数据集表列族。
 *
 * @author Sand
 * @created 2016.04.27 16:56
 */
public enum DataSetFamily {
    Basic("basic"),
    MetaData("meta_data"),
    Extension("extension");

    private String family;

    DataSetFamily(String family) {
        this.family = family;
    }

    public String value() {
        return family;
    }

    public static String[] getFamilies() {
        return new String[]{
                DataSetFamily.Basic.toString(),
                DataSetFamily.MetaData.toString(),
                DataSetFamily.Extension.toString()
        };
    }

    public static String[] getColumns(DataSetFamily family) {
        if (family == DataSetFamily.Basic) {
            return new String[]{
                    BasicColumns.ProfileId.toString(),
                    BasicColumns.InnerVersion.toString(),
                    BasicColumns.OrgCode.toString(),
                    BasicColumns.LastUpdateTime.toString()
            };
        } else if (family == DataSetFamily.MetaData){
            return null;
        }else if(family == DataSetFamily.Extension){
            return new String[]{
                    ExtensionColumns.Url.toString()
            };
        }

        return null;
    }

    // Basic列
    public enum BasicColumns {
        ProfileId("archive_id"),
        InnerVersion("inner_version"),
        OrgCode("org_code"),
        LastUpdateTime("last_update_time");

        private String qualifier;

        BasicColumns(String qualifier) {
            this.qualifier = qualifier;
        }

        public String toString() {
            return qualifier;
        }
    }

    // Extension列
    public enum ExtensionColumns {
        Url("url");

        private String qualifier;

        ExtensionColumns(String qualifier) {
            this.qualifier = qualifier;
        }

        public String toString() {
            return qualifier;
        }
    }
}
