package com.yihu.ehr.profile.core.commons;

import com.yihu.ehr.profile.core.StdDataSet;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据集工具。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.07 9:52
 */
public class DataSetUtil {
    public final static String OriginDataSetFlag = "_ORIGIN";

    /**
     * 为原始数据的数据集产生一个别名, 原始将数据存储在这个别名中.
     *
     * @param dataSetCode
     */
    public static String originDataSetCode(String dataSetCode) {
        return dataSetCode + OriginDataSetFlag;
    }

    /**
     * 将原始数据集转换为标准数据集.
     *
     * @param originDataSetTable
     * @return
     */
    public static String standardDataSetCode(String originDataSetTable) {
        return originDataSetTable.replace(OriginDataSetFlag, "");
    }

    /**
     * 判断是否为原始数据集。
     *
     * @param dataSetCode
     * @return
     */
    public static boolean isOriginDataSet(String dataSetCode){
        return dataSetCode.endsWith(OriginDataSetFlag);
    }

    public static Map<String, String> getBasicFamilyMap(StdDataSet dataSet){
        Map<String, String> map = new HashMap<>();
        map.put("", );

        return map;
    }

    public static Map<String, String> getMetaDataFamilyMap(String rowkey, StdDataSet dataSet){
        return dataSet.getRecord(rowkey).getMetaDataGroup();
    }
}
