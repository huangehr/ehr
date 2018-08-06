package com.yihu.ehr.test;

import org.junit.Test;

import java.io.*;

/**
 * Created by progr1mmer on 2018/7/10.
 */
public class FileTest {

    @Test
    public void test() throws Exception {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File("E:\\Google\\Downloads\\android-studio-ide-171.4408382-windows.exe")));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("D:\\android-studio-ide-171.4408382-windows.exe"));
            byte[] fileBuffer = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(fileBuffer);
            bufferedOutputStream.write(fileBuffer);
            bufferedOutputStream.flush();
        } finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
        }

    }
}
