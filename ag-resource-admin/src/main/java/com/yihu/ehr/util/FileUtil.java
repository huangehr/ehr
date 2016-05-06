package com.yihu.ehr.util;

import com.yihu.ehr.AgResourceAdminApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author linaz
 * @created 2016.05.06 11:30
 */
public class FileUtil {

    /**
     * 读取文件的内容
     * @return 返回文件内容
     */
    public String file2String(String path){
        String pastHistoryJsonPath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()+path;
        File file = new File(pastHistoryJsonPath);
        String result = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result = result + "\n" +s;
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}