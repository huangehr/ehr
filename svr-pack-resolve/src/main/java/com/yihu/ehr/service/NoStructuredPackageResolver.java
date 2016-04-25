package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.common.PackageUtil;
import com.yihu.ehr.constants.ProfileConstant;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.nostructured.NoStructuredContent;
import com.yihu.ehr.profile.core.nostructured.NoStructuredDocument;
import com.yihu.ehr.profile.core.nostructured.NoStructuredDocumentFile;
import com.yihu.ehr.profile.core.nostructured.NoStructuredProfile;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
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
public class NoStructuredPackageResolver {
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
    public NoStructuredProfile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }
        NoStructuredProfile noStructuredProfile = new NoStructuredProfile();    //非结构化档案
        noStructuredProfile.setProfileType(ProfileType.NoStructured);
        File[] files = root.listFiles();
        List<NoStructuredDocumentFile> noStructuredDocumentFileList = new ArrayList<>();  //document底下的文件
        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            if(folderName.equals(ProfileConstant.DocumentFolder)){
                noStructuredDocumentFileList = unstructuredDocumentParse(noStructuredProfile, file.listFiles());
            }else if(folderName.equals("meta.json")){
                noStructuredProfile = unstructuredDataSetParse(noStructuredProfile,file, noStructuredDocumentFileList);
            }
        }

        //makeEventSummary(structuredProfile);

        houseKeep(zipFile, root);

        return noStructuredProfile;
    }


    /**
     * 非标准化档案包解析document的文件。
     * @param profile
     * @param
     * @throws IOException
     */
    public List<NoStructuredDocumentFile> unstructuredDocumentParse(NoStructuredProfile profile, File[] files) throws Exception {
        List<NoStructuredDocumentFile> noStructuredDocumentFileList = new ArrayList<>();
        for (File file : files) {
            NoStructuredDocumentFile noStructuredDocumentFile = new NoStructuredDocumentFile();
            if (file.getAbsolutePath().endsWith(ProfileConstant.JsonExt)) continue;
            //这里把图片保存的fastdfs
            String filePath = file.getPath();
            String extensionName = filePath.substring(file.getPath().lastIndexOf('.') + 1);
            String localFileName = filePath.substring(file.getPath().lastIndexOf('\\') + 1);
            InputStream is = new FileInputStream(file);
            ObjectNode objectNode = fastDFSUtil.upload(is,extensionName,"");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            String remotePath = "groupName:" + groupName + ",remoteFileName:" + remoteFileName;

            noStructuredDocumentFile.setLocalFileName(localFileName);
            noStructuredDocumentFile.setRemotePath(remotePath);
            noStructuredDocumentFileList.add(noStructuredDocumentFile);
        }
        return noStructuredDocumentFileList;
    }

    /**
     * 非标准化档案包解析document的文件。
     * @param noStructuredProfile
     * @param
     * @throws IOException
     */
    public NoStructuredProfile unstructuredDataSetParse(NoStructuredProfile noStructuredProfile, File file, List<NoStructuredDocumentFile> noStructuredDocumentFileList) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(file);


        //公共部分
        String version = jsonNode.get("inner_version").asText();
        //String dataSetCode = jsonNode.get("code").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String patientId = jsonNode.get("patient_id").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String eventDate = jsonNode.path("event_time").asText();

        noStructuredProfile.setCdaVersion(version);
        noStructuredProfile.setEventNo(eventNo);
        noStructuredProfile.setDemographicId(patientId);
        noStructuredProfile.setOrgCode(orgCode);
        noStructuredProfile.setPatientId(patientId);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        noStructuredProfile.setEventDate(format.parse(eventDate));

        //data解析
        JsonNode datas = jsonNode.get("data"); //保存

        List<NoStructuredDocument> noStructuredDocumentList = new ArrayList<>();


        for(int i=0;i<datas.size();i++){


            JsonNode data = datas.get(i);

            NoStructuredDocument noStructuredDocument = new NoStructuredDocument();
            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            noStructuredDocument.setCdaDocId(cdaDocId);
            noStructuredDocument.setUrl(url);
            noStructuredDocument.setExpiryDate(format.parse(expiryDate));


            String keyWordsStr = data.get("key_words").toString();
//            JSONObject keyWordsObj = new JSONObject(keyWordsStr);
//            Iterator keys = keyWordsObj.keys();
//
//            List<Map<String,Object>> keyWordsList = new ArrayList<>();  //存放keyMap的数组
//            keyWordsList = JSONObject2List(keys,keyWordsList,keyWordsObj);
//            unStructuredDocument.setKeyWordsList(keyWordsList);

            noStructuredDocument.setKeyWordsStr(keyWordsStr);

            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息

            List<NoStructuredContent> noStructuredContentList = new ArrayList<>();
            for(int j=0;j<contents.size();j++){
                //documentFiles;
                NoStructuredContent noStructuredContent = new NoStructuredContent();
                List<NoStructuredDocumentFile> noStructuredDocumentFileListNew = new ArrayList<>();


                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/","");

                String[] names = name.split(",");

                for(String filename:names){
                    for(NoStructuredDocumentFile noStructuredDocumentFile : noStructuredDocumentFileList){
                        String localFileName = noStructuredDocumentFile.getLocalFileName();
                        if(filename.equals(localFileName)){
                            noStructuredDocumentFile.setMimeType(mimeType);
                            noStructuredDocumentFile.setName(filename);
                            noStructuredDocumentFile.setLocalFileName(noStructuredDocumentFile.getLocalFileName());
                            noStructuredDocumentFile.setRemotePath(noStructuredDocumentFile.getRemotePath());
                            noStructuredDocumentFileListNew.add(noStructuredDocumentFile);
                        }
                    }
                }
                noStructuredContent.setNoStructuredDocumentFileList(noStructuredDocumentFileListNew);
                noStructuredContentList.add(noStructuredContent);


                noStructuredDocument.setNoStructuredContentList(noStructuredContentList);
            }

            noStructuredDocumentList.add(noStructuredDocument);

        }
        noStructuredProfile.setNoStructuredDocumentList(noStructuredDocumentList);

        file.delete();
        return noStructuredProfile;
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageUtil.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }



}
