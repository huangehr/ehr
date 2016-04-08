package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.extractor.EventExtractor;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.profile.*;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class PackageResolver {
    @Autowired
    ApplicationContext context;

    @Autowired
    DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExtractorChain extractorChain;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String IndexFolder = "index";
    private final static String DocumentFolder = "document";
    private final static String JsonExt = ".json";

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 InDoubt 状态，即无法决定该JSON档案的去向，需要人为干预。
     * <p>
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    public Profile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }


        Profile profile = new Profile();
        UnStructuredProfile unStructuredProfile = new UnStructuredProfile();

        File[] files = root.listFiles();
        String firstFilepath = files[0].getPath();
        String firstFolderName =  firstFilepath.substring(firstFilepath.lastIndexOf("\\")+1);

        List<UnStructuredContent> contentList = new ArrayList<>();  //document底下的文件
        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            switch (firstFolderName){
                case OriFolder:
                    //结构化档案报处理
                    structuredDataSetParse(profile, file.listFiles(),folderName);
                    break;
                case IndexFolder:
                    //轻量级档案包处理
                    parseDataSetLight(profile,file.listFiles());
                case DocumentFolder:
                    //非结构化档案包处理

                    if(folderName.equals(DocumentFolder)){
                        contentList = unstructuredDocumentParse(unStructuredProfile, file.listFiles());
                    }else if(folderName.equals("meta.json")){
                        unstructuredDataSetParse(unStructuredProfile,file,contentList);
                    }
                    break;
                default: break;
            }
        }

        makeEventSummary(profile);

        houseKeep(zipFile, root);

        return profile;
    }




    /**
     * 结构化档案包解析JSON文件中的数据。
     * @param profile
     * @param files
     * @throws IOException
     */
    void structuredDataSetParse(Profile profile, File[] files,String folderName) throws ParseException, IOException {
        for (File file : files) {
            String lastName = folderName.substring(folderName.lastIndexOf("\\")+1);
            //if (!file.getAbsolutePath().endsWith(JsonExt)) continue;
            ProfileDataSet dataSet = generateDataSet(file, lastName.equals(OriFolder) ? true :false);

            // 原始数据存储在表"数据集代码_ORIGIN"
            String dataSetTable = lastName.equals(OriFolder) ? StdObjectQualifierTranslator.originDataTable(dataSet.getCode()) : dataSet.getCode();
            profile.addDataSet(dataSetTable, dataSet);
            profile.setPatientId(dataSet.getPatientId());
            profile.setEventNo(dataSet.getEventNo());
            profile.setOrgCode(dataSet.getOrgCode());
            profile.setCdaVersion(dataSet.getCdaVersion());

            dataSet.setCode(dataSetTable);

            // Extract key data from data set if exists
            if (!lastName.equals(OriFolder)) {
                if (profile.getCardId().length() == 0) {
                    Object object = extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    if (null != object) {
                        Properties properties = (Properties) object;
                        profile.setCardId(properties.getProperty("CardNo"));
                    }
                }

                if (StringUtils.isEmpty(profile.getDemographicId())) {
                    profile.setDemographicId((String) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.DemographicInfo));
                }

                if (profile.getEventDate() == null) {
                    profile.setEventDate((Date) extractorChain.doExtract(dataSet, KeyDataExtractor.Filter.EventDate));
                }
            }
            profile.addDataSet(dataSet.getCode(), dataSet);
            file.delete();
        }

    }





    /**
     * 轻量级档案包解析JSON文件中的数据。
     * @param profile
     * @param
     * @throws IOException
     */
    void parseDataSetLight(Profile profile,File[] files) throws IOException, ParseException {
        if(files==null){
            throw new IOException("There is no file");
        }
        File file = files[0];
        JsonNode jsonNode = objectMapper.readTree(file);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        //设置数据集
        dataSetResolverWithTranslator.parseLightJsonDataSet(profile,jsonNode);
        file.delete();
    }





    /**
     * 非标准化档案包解析document的文件。
     * @param profile
     * @param
     * @throws IOException
     */
    void unstructuredDataSetParse(UnStructuredProfile profile, File file,List<UnStructuredContent> contentList) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(file);


        //公共部分
        String version = jsonNode.get("inner_version").asText();
        //String dataSetCode = jsonNode.get("code").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String patientId = jsonNode.get("patient_id").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String eventDate = jsonNode.path("event_time").asText();

        profile.setCdaVersion(version);
        profile.setEventNo(eventNo);
        profile.setDemographicId(patientId);
        profile.setOrgCode(orgCode);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        profile.setEventDate(format.parse(eventDate));

        //data解析
        JsonNode datas = jsonNode.get("data"); //保存

        List<UnStructuredDocument> unStructuredDocumentList = new ArrayList<>();
        for(int i=0;i<datas.size();i++){
            UnStructuredDocument unStructuredDocument = new UnStructuredDocument();
            JsonNode data = datas.get(i);

            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            unStructuredDocument.setCdaDocId(cdaDocId);
            unStructuredDocument.setUrl(url);
            unStructuredDocument.setExpiryDate(format.parse(expiryDate));


            String keyWordsStr = data.get("key_words").toString();
            JSONObject keyWordsObj = new JSONObject(keyWordsStr);
            Iterator keys = keyWordsObj.keys();

            List<Map<String,Object>> keyWordsList = new ArrayList<>();  //存放keyMap的数组
            keyWordsList = JSONObject2List(keys,keyWordsList,keyWordsObj);
            unStructuredDocument.setKeyWordsList(keyWordsList);


            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息
            List<UnStructuredContent> newContentList = new ArrayList<>();
            for(int j=0;j<contents.size();j++){
                //documentFiles;
                UnStructuredContent unStructuredContent = new UnStructuredContent();
                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/","");

                String[] names = name.split(",");
                for(String filename:names){
                    for(UnStructuredContent ucontent:contentList){
                        String localFileName = ucontent.getLocalFileName();
                        if(filename.equals(localFileName)){
                            unStructuredContent.setMimeType(mimeType);
                            unStructuredContent.setName(name);
                            unStructuredContent.setLocalFileName(ucontent.getLocalFileName());
                            unStructuredContent.setRemotePath(ucontent.getRemotePath());
                            newContentList.add(unStructuredContent);
                        }
                    }
                }
            }
            unStructuredDocument.setUnStructuredContentList(newContentList);
            unStructuredDocumentList.add(unStructuredDocument);

        }
        profile.setUnStructuredDocument(unStructuredDocumentList);


    }

    /**
     * 非标准化档案包解析document的文件。
     * @param profile
     * @param
     * @throws IOException
     */
    List<UnStructuredContent> unstructuredDocumentParse(UnStructuredProfile profile, File[] files) throws Exception {
        List<UnStructuredContent> contentList = new ArrayList<>();
        for (File file : files) {
            UnStructuredContent unStructuredContent = new UnStructuredContent();
            if (file.getAbsolutePath().endsWith(JsonExt)) continue;
            //这里把图片保存的fastdfs
            String filePath = file.getPath();
            String extensionName = filePath.substring(file.getPath().lastIndexOf('.') + 1);
            String localFileName = filePath.substring(file.getPath().lastIndexOf('\\') + 1);
            InputStream is = new FileInputStream(file);
            ObjectNode objectNode = fastDFSUtil.upload(is,extensionName,"");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            String remotePath = "groupName:" + groupName + ",remoteFileName:" + remoteFileName;

            unStructuredContent.setLocalFileName(localFileName);
            unStructuredContent.setRemotePath(remotePath);
            contentList.add(unStructuredContent);
        }
        return contentList;
    }




    /**
     *
     * @param iterator data的Iterator格式
     * @param list   返回的数组
     * @param jsonObject    data的JSONObject格式
     * @return
     */
    public List<Map<String,Object>> JSONObject2List(Iterator iterator,List<Map<String,Object>> list,JSONObject jsonObject){
        while (iterator.hasNext()) {
            Map<String,Object> contentMap = new HashMap<>(); //解析单个content
            String key = (String) iterator.next();
            String value = jsonObject.getString(key);
            contentMap.put(key,value);
            list.add(contentMap);
        }
        return list;
    }

    /**
     * 生产数据集
     * @param jsonFile
     * @param isOrigin
     * @return
     * @throws IOException
     */
    public ProfileDataSet generateDataSet(File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        ProfileDataSet dataSet = dataSetResolverWithTranslator.parseStructuredJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }



    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param profile
     */
    public void makeEventSummary(Profile profile) {
        EventExtractor eventExtractor = context.getBean(EventExtractor.class);

        for (String dataSetTable : profile.getDataSetTables()) {
            if (StringUtils.isEmpty(profile.getSummary()) && eventExtractor.getDataSets().containsKey(dataSetTable)) {
                profile.setSummary(eventExtractor.getDataSets().get(dataSetTable));
            }

            int rowIndex = 0;
            ProfileDataSet dataSet = profile.getDataSet(dataSetTable);
            String[] rowKeys = new String[dataSet.getRecordKeys().size()];
            dataSet.getRecordKeys().toArray(rowKeys);
            for (String rowKey : rowKeys) {
                dataSet.updateRecordKey(rowKey, profile.getId() + "$" + rowIndex++);
            }
        }
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageResolver.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }
}
