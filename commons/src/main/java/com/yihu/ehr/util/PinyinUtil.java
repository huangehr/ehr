package com.yihu.ehr.util;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 拼音工具包装。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 15:33
 */
public class PinyinUtil {
    /**
     * 提取汉字的首字母
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str, boolean upperCase) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);

            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }

        return upperCase ? convert.toUpperCase() : convert;
    }
}
