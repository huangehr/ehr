package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.yihu.ehr.profile.core.NonStructedProfile;
import com.yihu.ehr.profile.core.StructedProfile;
import com.yihu.ehr.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

/**
 * 文件档案包解析器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class DocumentPackageResolver extends PackageResolver {

    @Override
    public void resolve(StructedProfile profile, File root) throws IOException, ParseException {
        NonStructedProfile nonStructedProfile = (NonStructedProfile) profile;

        File metaFile = new File(root.getAbsolutePath() + File.pathSeparator + "meta.json");
        parseFile(nonStructedProfile, metaFile);
    }

    private void parseFile(NonStructedProfile profile, File metaFile) throws IOException, ParseException {
        JsonNode jsonNode = objectMapper.readTree(metaFile);

        String patientId = jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version").asText();
        String eventDate = jsonNode.get("event_time").asText();

        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(version);
        profile.setEventDate(DateTimeUtils.utcDateTimeParse(eventDate));
    }

    /*public List<RawDocumentList> unstructuredDocumentParse(NonStructedProfile profile, File[] files) throws Exception {
        List<RawDocumentList> documentListList = new ArrayList<>();
        for (File file : files) {
            RawDocumentList documentList = new RawDocumentList();
            if (file.getAbsolutePath().endsWith(ProfileConstant.JsonExt)) continue;
            //这里把图片保存的fastdfs
            String filePath = file.getPath();
            String extensionName = filePath.substring(file.getPath().lastIndexOf('.') + 1);
            String localFileName = filePath.substring(file.getPath().lastIndexOf('\\') + 1);
            InputStream is = new FileInputStream(file);
            ObjectNode objectNode = fastDFSUtil.upload(is, extensionName, "");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            String remotePath = "groupName:" + groupName + ",remoteFileName:" + remoteFileName;

            documentList.setLocalFileName(localFileName);
            documentList.setRemotePath(remotePath);
            documentListList.add(documentList);
        }
        return documentListList;
    }
    public NonStructedProfile unstructuredDataSetParse(NonStructedProfile noStructuredProfile, File file, List<RawDocumentList> documentListList) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(file);
        String version = jsonNode.get("inner_version").asText();
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


        for (int i = 0; i < datas.size(); i++) {


            JsonNode data = datas.get(i);

            RawDocument rawDocument = new RawDocument();
            String cdaDocId = data.get("cda_doc_id").asText();
            String url = data.get("url").asText();
            String expiryDate = data.get("expiry_date").asText();

            rawDocument.setCdaDocumentId(cdaDocId);
            rawDocument.setFileUrl(url);
            rawDocument.setExpireDate(format.parse(expiryDate));


            String keyWordsStr = data.get("key_words").toString();
            rawDocument.setKeyWordsStr(keyWordsStr);

            //document底下文件处理
            JsonNode contents = data.get("content");   //文件信息

            List<NoStructuredContent> noStructuredContentList = new ArrayList<>();
            for (int j = 0; j < contents.size(); j++) {
                //documentFiles;
                NoStructuredContent noStructuredContent = new NoStructuredContent();
                List<RawDocumentList> documentListListNew = new ArrayList<>();


                JsonNode object = contents.get(j);
                String mimeType = object.get("mime_type").asText();
                String name = object.get("name").asText().replace("/", "");

                String[] names = name.split(",");

                for (String filename : names) {
                    for (RawDocumentList documentList : documentListList) {
                        String localFileName = documentList.getLocalFileName();
                        if (filename.equals(localFileName)) {
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
    }*/
}
