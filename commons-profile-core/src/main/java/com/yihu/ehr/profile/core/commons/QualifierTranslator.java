package com.yihu.ehr.profile.core.commons;

import com.yihu.ehr.util.StringBuilderUtil;

/**
 * 标准化对象标识翻译工具。将数据集、数据元的名称进行适应性翻译。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.26 20:35
 */
public class QualifierTranslator {
    private final static String MetaDataCodeSuffix = "_CODE";
    private final static String MetaDataValueSuffix = "_VALUE";

    /**
     * 以数据内部标识为基础，数据类型为辅助，产生一个HBase列名。
     *
     * @param innerCode
     * @return
     */
    public static String hBaseQualifier(String innerCode, String type){
        return new StringBuilderUtil("%1_%2").arg(innerCode).arg(type.substring(0, 1).toUpperCase()).toString();
    }

    public static String[] splitMetaData(String innerCode){
        return new String[]{
                innerCode + MetaDataCodeSuffix,
                innerCode + MetaDataValueSuffix
        };
    }
}
