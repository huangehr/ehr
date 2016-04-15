package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.core.unStructured.UnStructuredContent;
import com.yihu.ehr.profile.core.unStructured.UnStructuredDocument;
import com.yihu.ehr.profile.core.unStructured.UnStructuredDocumentFile;
import com.yihu.ehr.profile.core.unStructured.UnStructuredProfile;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class UnStructuredPackageResolver {
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

    private final static String JsonExt = ".json";


    /**
     * 非标准化档案包解析document的文件。
     * @param profile
     * @param
     * @throws IOException
     */
    public List<UnStructuredDocumentFile> unstructuredDocumentParse(UnStructuredProfile profile, File[] files) throws Exception {
        List<UnStructuredDocumentFile> unStructuredDocumentFileList = new ArrayList<>();
        for (File file : files) {
            UnStructuredDocumentFile unStructuredDocumentFile = new UnStructuredDocumentFile();
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

            unStructuredDocumentFile.setLocalFileName(localFileName);
            unStructuredDocumentFile.setRemotePath(remotePath);
            unStructuredDocumentFileList.add(unStructuredDocumentFile);
        }
        return unStructuredDocumentFileList;
    }

    /**
     * 非标准化档案包解析document的文件。
     * @param unStructuredProfile
     * @param
     * @throws IOException
     */
    public UnStructuredProfile unstructuredDataSetParse(UnStructuredProfile unStructuredProfile, File file, List<UnStructuredDocumentFile> unStructuredDocumentFileList) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(file);


        //公共部分
        String version = jsonNode.get("inner_version").asText();
        //String dataSetCode = jsonNode.get("code").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String patientId = jsonNode.get("patient_id").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String eventDate = jsonNode.path("event_time").asText();

        unStructuredProfile.setCdaVersion(version);
        unStructuredProfile.setEventNo(eventNo);
        unStructuredProfile.setDemographicId(patientId);
        unStructuredProfile.setOrgCode(orgCode);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        unStructuredProfile.setEventDate(format.parse(eventDate));

        //data解析
        JsonNode datas = jsonNode.get("data"); //保存

        List<UnStructuredDocument> unStructuredDocumentList = new ArrayList<>();


        for(int i=0;i<datas.size();i++){


            JsonNode data = datas.get(i);

            UnStructuredDocument unStructuredDocument = new UnStructuredDocument();
            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            unStructuredDocument.setCdaDocId(cdaDocId);
            unStructuredDocument.setUrl(url);
            unStructuredDocument.setExpiryDate(format.parse(expiryDate));


            String keyWordsStr = data.get("key_words").toString();
//            JSONObject keyWordsObj = new JSONObject(keyWordsStr);
//            Iterator keys = keyWordsObj.keys();
//
//            List<Map<String,Object>> keyWordsList = new ArrayList<>();  //存放keyMap的数组
//            keyWordsList = JSONObject2List(keys,keyWordsList,keyWordsObj);
//            unStructuredDocument.setKeyWordsList(keyWordsList);

            unStructuredDocument.setKeyWordsStr(keyWordsStr);

            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息

            List<UnStructuredContent> unStructuredContentList = new ArrayList<>();
            for(int j=0;j<contents.size();j++){
                //documentFiles;
                UnStructuredContent unStructuredContent = new UnStructuredContent();
                List<UnStructuredDocumentFile> unStructuredDocumentFileListNew = new ArrayList<>();


                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/","");

                String[] names = name.split(",");

                for(String filename:names){
                    for(UnStructuredDocumentFile unStructuredDocumentFile : unStructuredDocumentFileList){
                        String localFileName = unStructuredDocumentFile.getLocalFileName();
                        if(filename.equals(localFileName)){
                            unStructuredDocumentFile.setMimeType(mimeType);
                            unStructuredDocumentFile.setName(filename);
                            unStructuredDocumentFile.setLocalFileName(unStructuredDocumentFile.getLocalFileName());
                            unStructuredDocumentFile.setRemotePath(unStructuredDocumentFile.getRemotePath());
                            unStructuredDocumentFileListNew.add(unStructuredDocumentFile);
                        }
                    }
                }
                unStructuredContent.setUnStructuredDocumentFileList(unStructuredDocumentFileListNew);
                unStructuredContentList.add(unStructuredContent);


                unStructuredDocument.setUnStructuredContentList(unStructuredContentList);
            }

            unStructuredDocumentList.add(unStructuredDocument);

        }
        unStructuredProfile.setUnStructuredDocument(unStructuredDocumentList);

        file.delete();
        return unStructuredProfile;
    }


}
