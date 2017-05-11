package com.yihu.ehr.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.report.QcDailyDatasetsModel;
import com.yihu.ehr.agModel.report.QcDailyMetadataModel;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;

/**
 * Created by janseny on 2017/5/9.
 */
public class ResolveJsonFileUtil {


    private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;
    private final static String FilePath = "ehrData";


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

    public static void main(String args[]) {
        ResolveJsonFileUtil rj = new ResolveJsonFileUtil();
        try {
            rj.resolveContent("E:\\test\\datasets.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void resolveContent(String filePath) throws Exception {
        File[] filse = unzip(filePath,"");
        for (File file :filse){
            if(file.getName().contains("datasets")){
                QcDailyDatasetsModel dataSets =  generateDataSet(file);
                System.out.print("datasets");
            }else{
                List<QcDailyMetadataModel> metadataList  =  generateMetadata(file);
                System.out.print("metadataModel");
            }
            System.out.print("222");
        }

    }


    public File[] unzip(String zipFile,String pwd) throws Exception {
        File[] filse = null;
        try {
            filse = new Zipper().unzip(new File(zipFile), TempPath + FilePath, pwd);
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
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(ResolveJsonFileUtil.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }



    /**
     * 解析数据集
     *
     * @param jsonFile
     * @return
     * @throws IOException
     */
    private QcDailyDatasetsModel generateDataSet(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate datasets");
        }
        QcDailyDatasetsModel dataSet = QcDatasetsParser.parseStructuredJsonQcDatasetsModel(jsonNode);
        return dataSet;
    }

    /**
     * 解析数据元
     *
     * @param jsonFile
     * @return
     * @throws IOException
     */
    private List<QcDailyMetadataModel> generateMetadata(File jsonFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate Metadata");
        }
        List<QcDailyMetadataModel> metadataList = QcMetadataParser.parseStructuredJsonMetadateModel(jsonNode);
        return metadataList;
    }




}
