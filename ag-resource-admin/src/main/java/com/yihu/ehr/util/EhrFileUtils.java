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
        String folder=System.getProperty("java.io.tmpdir");
        String filePath = folder+path;
        File file = new File(filePath);
        return FileUtil.readAsString(file);
    }

    public static void main(String[] args) {
        String folder=System.getProperty("java.io.tmpdir");
    }

}