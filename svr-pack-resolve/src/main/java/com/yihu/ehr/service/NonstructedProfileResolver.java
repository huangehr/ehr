package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.profile.core.NonstructedProfile;
import com.yihu.ehr.profile.core.Profile;
import com.yihu.ehr.profile.core.ProfileDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 16:33
 */
public class NonstructedProfileResolver implements ProfileResolver {
    @Override
    public void resolve(Profile profile, File root) {
        /*JsonNode jsonNode = objectMapper.readTree(file);

        String version = jsonNode.get("inner_version").asText();
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
        JsonNode datas = jsonNode.get("data");
        List<UnStructuredDocument> unStructuredDocumentList = new ArrayList<>();

        for (int i = 0; i < datas.size(); i++) {
            JsonNode data = datas.get(i);

            UnStructuredDocument unStructuredDocument = new UnStructuredDocument();
            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            unStructuredDocument.setCdaDocId(cdaDocId);
            unStructuredDocument.setUrl(url);
            unStructuredDocument.setExpiryDate(format.parse(expiryDate));

            String keyWordsStr = data.get("key_words").toString();

            unStructuredDocument.setKeyWordsStr(keyWordsStr);

            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息

            List<UnStructuredContent> unStructuredContentList = new ArrayList<>();
            for (int j = 0; j < contents.size(); j++) {
                UnStructuredContent unStructuredContent = new UnStructuredContent();
                List<ProfileDocument> profileDocumentListNew = new ArrayList<>();

                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/", "");

                String[] names = name.split(",");

                for (String filename : names) {
                    for (ProfileDocument profileDocument : profileDocumentList) {
                        String localFileName = profileDocument.getLocalFileName();
                        if (filename.equals(localFileName)) {
                            profileDocument.setMimeType(mimeType);
                            profileDocument.setName(filename);
                            profileDocument.setLocalFileName(profileDocument.getLocalFileName());
                            profileDocument.setRemotePath(profileDocument.getRemotePath());
                            profileDocumentListNew.add(profileDocument);
                        }
                    }
                }

                unStructuredContent.setDocumentFileList(profileDocumentListNew);
                unStructuredContentList.add(unStructuredContent);
                unStructuredDocument.setUnStructuredContentList(unStructuredContentList);
            }

            unStructuredDocumentList.add(unStructuredDocument);
        }

        profile.setUnStructuredDocument(unStructuredDocumentList);*/
    }

    /**
     * 非标准化档案包解析document的文件。
     *
     * @param profile
     * @param
     * @throws IOException
     */
    List<ProfileDocument> nonstructedDocumentResolve(NonstructedProfile profile, File[] files) throws Exception {
        /*List<ProfileDocument> profileDocumentList = new ArrayList<>();
        for (File file : files) {
            ProfileDocument profileDocument = new ProfileDocument();
            if (file.getAbsolutePath().endsWith(JsonExt)) continue;

            //这里把图片保存的fastdfs
            String filePath = file.getPath();
            String extensionName = filePath.substring(file.getPath().lastIndexOf('.') + 1);
            String localFileName = filePath.substring(file.getPath().lastIndexOf('\\') + 1);
            InputStream is = new FileInputStream(file);
            ObjectNode objectNode = fastDFSUtil.upload(is, extensionName, "");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            String remotePath = "groupName:" + groupName + ",remoteFileName:" + remoteFileName;

            profileDocument.setLocalFileName(localFileName);
            profileDocument.setRemotePath(remotePath);
            profileDocumentList.add(profileDocument);
        }

        return profileDocumentList;*/
        return null;
    }
}
