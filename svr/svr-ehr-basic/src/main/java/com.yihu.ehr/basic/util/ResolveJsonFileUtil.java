package com.yihu.ehr.basic.util;

import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.*;

/**
 * Created by janseny on 2017/5/9.
 */
public class ResolveJsonFileUtil {

    public String ReadFile(String Path){
        BufferedReader reader = null;
        String laststr = "";
        try{
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }


    public File[] unzip(String zipFile,String pwd, String toDir) throws Exception {
        File[] filse = null;
        try {
            filse = new Zipper().unzip(new File(zipFile),toDir , pwd);
            if (filse == null  || filse.length == 0) {
                throw new RuntimeException("Invalid package file error, no file ");
            }
        }catch (Exception e){
            LogService.getLogger(ResolveJsonFileUtil.class).warn("unzip failed resolve: " + e.getMessage());
        }
        return filse;
    }


    public File[] unzip(File zipFile,String pwd , String toDir) throws Exception {
        File[] filse = null;
        try {
            filse = new Zipper().unzip( zipFile, toDir, pwd);
            if (filse == null  || filse.length == 0) {
                throw new RuntimeException("Invalid package file error, no file ");
            }
        }catch (Exception e){
            LogService.getLogger(ResolveJsonFileUtil.class).warn("unzip failed resolve: " + e.getMessage());
        }
        return filse;
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteDirectory(new File(zipFile));
            FileUtils.deleteDirectory(root);
        } catch (Exception e) {
            LogService.getLogger(ResolveJsonFileUtil.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }

//    public static void  main (String args[]){
//        String s = "2017-06-08T11:40:37Z";
//        Date createDate = DateUtil.parseDate(s, "yyyy-MM-dd'T'HH:mm:ss");
//        System.out.println("");
//
//    }

}
