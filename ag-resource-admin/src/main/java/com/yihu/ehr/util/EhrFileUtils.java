package com.yihu.ehr.util;

import org.aspectj.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author linaz
 * @created 2016.05.06 11:30
 */
public class EhrFileUtils {

    /**
     * 读取文件的内容
     * @return 返回文件内容
     */
    public String file2String(String path) throws IOException {
        String pastHistoryJsonPath = EhrFileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()+path;
        File file = new File(pastHistoryJsonPath);
        return FileUtil.readAsString(file);
    }
}