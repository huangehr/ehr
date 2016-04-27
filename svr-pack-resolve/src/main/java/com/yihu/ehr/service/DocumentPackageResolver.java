package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.common.PackageUtil;
import com.yihu.ehr.constants.ProfileConstant;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.nostructured.NoStructuredContent;
import com.yihu.ehr.profile.core.RawDocument;
import com.yihu.ehr.profile.core.RawDocumentList;
import com.yihu.ehr.profile.core.NonStructedProfile;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
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
public class DocumentPackageResolver extends PackageResolver {
    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    public NonStructedProfile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }

        NonStructedProfile noStructuredProfile = new NonStructedProfile();    //非结构化档案
        noStructuredProfile.setProfileType(ProfileType.NonStructured);
        File[] files = root.listFiles();
        List<RawDocumentList> documentListList = new ArrayList<>();  //document底下的文件
        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            if(folderName.equals(ProfileConstant.DocumentFolder)){
                documentListList = unstructuredDocumentParse(noStructuredProfile, file.listFiles());
            }else if(folderName.equals("meta.json")){
                noStructuredProfile = unstructuredDataSetParse(noStructuredProfile,file, documentListList);
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
    public List<RawDocumentList> unstructuredDocumentParse(NonStructedProfile profile, File[] files) throws Exception {
        List<RawDocumentList> documentListList = new ArrayList<>();
        for (File file : files) {
            RawDocumentList documentList = new RawDocumentList();
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

            documentList.setLocalFileName(localFileName);
            documentList.setRemotePath(remotePath);
            documentListList.add(documentList);
        }
        return documentListList;
    }

    /**
     * 非标准化档案包解析document的文件。
     * @param noStructuredProfile
     * @param
     * @throws IOException
     */
    public NonStructedProfile unstructuredDataSetParse(NonStructedProfile noStructuredProfile, File file, List<RawDocumentList> documentListList) throws Exception {
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

        List<RawDocument> rawDocumentList = new ArrayList<>();


        for(int i=0;i<datas.size();i++){


            JsonNode data = datas.get(i);

            RawDocument rawDocument = new RawDocument();
            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            rawDocument.setCdaDocumentId(cdaDocId);
            rawDocument.setFileUrl(url);
            rawDocument.setExpireDate(format.parse(expiryDate));


            String keyWordsStr = data.get("key_words").toString();
//            JSONObject keyWordsObj = new JSONObject(keyWordsStr);
//            Iterator keys = keyWordsObj.keys();
//
//            List<Map<String,Object>> keyWordsList = new ArrayList<>();  //存放keyMap的数组
//            keyWordsList = JSONObject2List(keys,keyWordsList,keyWordsObj);
//            unStructuredDocument.setKeyWordsList(keyWordsList);

            rawDocument.setKeyWordsStr(keyWordsStr);

            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息

            List<NoStructuredContent> noStructuredContentList = new ArrayList<>();
            for(int j=0;j<contents.size();j++){
                //documentFiles;
                NoStructuredContent noStructuredContent = new NoStructuredContent();
                List<RawDocumentList> documentListListNew = new ArrayList<>();


                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/","");

                String[] names = name.split(",");

                for(String filename:names){
                    for(RawDocumentList documentList : documentListList){
                        String localFileName = documentList.getLocalFileName();
                        if(filename.equals(localFileName)){
                            documentList.setMimeType(mimeType);
                            documentList.setName(filename);
                            documentList.setLocalFileName(documentList.getLocalFileName());
                            documentList.setRemotePath(documentList.getRemotePath());
                            documentListListNew.add(documentList);
                        }
                    }
                }
                noStructuredContent.setNoStructuredDocumentFileList(documentListListNew);
                noStructuredContentList.add(noStructuredContent);


                rawDocument.setNoStructuredContentList(noStructuredContentList);
            }

            rawDocumentList.add(rawDocument);

        }
        noStructuredProfile.setRawDocuments(rawDocumentList);

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
