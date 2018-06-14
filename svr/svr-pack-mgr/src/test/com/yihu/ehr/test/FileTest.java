package com.yihu.ehr.test;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by progr1mmer on 2018/6/13.
 */
public class FileTest {

    @Test
    public void test() throws Exception {
        File file = new File("D:\\新建文本文档.txt");
        Long length = file.length();
        byte [] content = new byte[length.intValue()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(content);
        String s = new String(content).replaceAll("\r\n", "").replaceAll("\\s*", "");
        System.out.println(s);
    }
}
