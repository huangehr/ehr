package com.yihu.ehr.profile.legacy.memory.model;

import com.yihu.ehr.profile.legacy.memory.util.ResourceRecord;
import org.apache.commons.lang3.StringUtils;

/**
 * 档案资源子表记录。
 *
 * 子表记录的rowkey格式：主表rowkey$数据集代码$序号
 *
 * @author Sand
 * @created 2016.05.16 16:06
 */
public class SubRecord extends ResourceRecord {
    private final static char Delimiter = '$';
    private final static String RowKeyFormat = "%s$%s$%s";

    String rowkey;

    public String getProfileId(){
        if (StringUtils.isEmpty(rowkey)) return "";

        return rowkey.substring(0, rowkey.indexOf(Delimiter));
    }

    public String getDataSetCode() {
        if (StringUtils.isEmpty(rowkey)) return "";

        return rowkey.substring(rowkey.indexOf(Delimiter), rowkey.lastIndexOf(Delimiter));
    }

    public int getIndex(){
        if (StringUtils.isEmpty(rowkey)) return 0;

        return Integer.parseInt(rowkey.substring(rowkey.lastIndexOf(Delimiter)));
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public void setRowkey(String profileId, String dataSetCode, int index){
        rowkey = String.format(RowKeyFormat, profileId, dataSetCode, index);
    }
}
