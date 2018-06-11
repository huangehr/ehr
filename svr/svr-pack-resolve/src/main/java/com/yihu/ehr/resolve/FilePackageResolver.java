package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.UrlScope;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.resolve.model.stage1.*;
import com.yihu.ehr.resolve.model.stage1.details.CdaDocument;
import com.yihu.ehr.resolve.model.stage1.details.OriginFile;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 文件档案包解析器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class FilePackageResolver extends PackageResolver {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Override
    public void resolve(OriginalPackage originalPackage, File root) throws Exception {
        File documents = new File(root.getAbsolutePath() + File.separator + "documents.json");
        parseFile((FilePackage)originalPackage, documents);
    }

    private void parseFile(FilePackage filePackage, File documents) throws Exception {
        JsonNode root = objectMapper.readTree(documents);

        String demographicId = root.get("demographic_id") == null ? "" : root.get("demographic_id").asText();
        String patientId = root.get("patient_id") == null ? "" : root.get("patient_id").asText();
        String orgCode = root.get("org_code") == null ? "" : root.get("org_code").asText();
        String eventNo = root.get("event_no") == null ? "" : root.get("event_no").asText();
        int eventType = root.get("event_type") == null ? -1 : root.get("event_type").asInt();
        String eventDate = root.get("event_time") == null ? "" : root.get("event_time").asText();
        String createDate = root.get("create_date") == null ? "" : root.get("create_date").asText();
        String cdaVersion = root.get("inner_version") == null ? "" : root.get("inner_version").asText();
        //验证档案基础数据的完整性，当其中某字段为空的情况下直接提示档案包信息缺失。
        StringBuilder errorMsg = new StringBuilder();
        if (StringUtils.isEmpty(patientId)){
            errorMsg.append("patientId is null;");
        }
        if (StringUtils.isEmpty(eventNo)){
            errorMsg.append("eventNo is null;");
        }
        if (StringUtils.isEmpty(orgCode)){
            errorMsg.append("orgCode is null;");
        }
        if (StringUtils.isEmpty(cdaVersion)) {
            errorMsg.append("innerVersion is null;");
        }
        if (StringUtils.isEmpty(eventDate)) {
            errorMsg.append("eventTime is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())){
            throw new IllegalJsonDataException(errorMsg.toString());
        }
        filePackage.setDemographicId(demographicId);
        filePackage.setPatientId(patientId);
        filePackage.setEventNo(eventNo);
        if (eventType != -1) {
            filePackage.setEventType(EventType.create(eventType));
        }
        filePackage.setOrgCode(orgCode);
        filePackage.setEventTime(DateUtil.strToDate(eventDate));
        filePackage.setCreateDate(DateUtil.strToDate(createDate));
        filePackage.setCdaVersion(cdaVersion);

        parseDataSets(filePackage, (ObjectNode) root.get("data_sets"));
        parseFiles(filePackage, (ArrayNode) root.get("files"), documents.getParent() + File.separator + "documents");
    }

    private void parseDataSets(FilePackage profile, ObjectNode dataSets) {
        if (dataSets == null) {
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> iterator = dataSets.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> item = iterator.next();
            String dataSetCode = item.getKey();
            PackageDataSet dataSet = new PackageDataSet();
            dataSet.setPatientId(profile.getPatientId());
            dataSet.setEventNo(profile.getEventNo());
            dataSet.setCdaVersion(profile.getCdaVersion());
            dataSet.setCode(dataSetCode);
            dataSet.setOrgCode(profile.getOrgCode());
            dataSet.setEventTime(profile.getEventTime());
            dataSet.setCreateTime(profile.getCreateDate());
            ArrayNode records = (ArrayNode) item.getValue();
            for (int i = 0; i < records.size(); ++i) {
                MetaDataRecord record = new MetaDataRecord();
                ObjectNode jsonRecord = (ObjectNode) records.get(i);
                Iterator<Map.Entry<String, JsonNode>> filedIterator = jsonRecord.fields();
                while (filedIterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = filedIterator.next();
                    //String metaData = translateMetaDataCode(profile.getCdaVersion(), dataSetCode, field.getKey());
                    String value = field.getValue().asText().equals("null") ? "" : field.getValue().asText();
                    if (field.getKey() != null) {
                        record.putMetaData(field.getKey(), value);
                    }
                }
                dataSet.addRecord(Integer.toString(i), record);
            }
            profile.insertDataSet(dataSetCode, dataSet);
        }
    }

    private void parseFiles(FilePackage profile, ArrayNode files, String documentsPath) throws Exception {
        for (int i = 0; i < files.size(); ++i) {
            ObjectNode objectNode = (ObjectNode) files.get(i);
            String cdaDocumentId = objectNode.get("cda_doc_id").asText();
            Date expireDate = null;
            if (objectNode.get("expire_date") != null) {
                expireDate = DateTimeUtil.simpleDateParse(objectNode.get("expire_date").asText());
            }

            // 解析过程中，使用cda文档id作为文档列表的主键，待解析完成后，统一更新为rowkey
            CdaDocument cdaDocument = profile.getCdaDocuments().get(cdaDocumentId);
            if (cdaDocument == null) {
                cdaDocument = new CdaDocument();
                cdaDocument.setId(cdaDocumentId);
                profile.getCdaDocuments().put(cdaDocumentId, cdaDocument);
            }

            ArrayNode content = (ArrayNode) objectNode.get("content");
            for (int j = 0; j < content.size(); ++j) {
                ObjectNode file = (ObjectNode) content.get(j);
                String mine_type = file.get("mime_type").asText();//必填
                String url_scope = file.get("url_scope") == null ? "" : file.get("url_scope").asText();//可选
                String url = file.get("url")== null ? "": file.get("url").asText();//可选
                String emr_id = file.get("emr_id").asText();
                String emr_name = file.get("emr_name").asText();
                String note = file.has("note")?file.get("note").asText():"";//可选

                OriginFile originFile = new OriginFile();
                originFile.setMime(mine_type);
                originFile.setExpireDate(expireDate);
                if ("public".equalsIgnoreCase(url_scope)){
                    originFile.setUrlScope(UrlScope.valueOf(0));
                } else if ("private".equalsIgnoreCase(url_scope)){
                    originFile.setUrlScope(UrlScope.valueOf(1));
                }
                originFile.setEmrId(emr_id);
                originFile.setEmrName(emr_name);
                if (!StringUtils.isBlank(note)) {
                    originFile.setNote(note);
                }
                if (file.get("name") != null) {
                    String fileList[] = file.get("name").asText().split(";");
                    for (String fileName : fileList) {
                        String fileNames[] = fileName.split(",");
                        for (String file_name : fileNames) {
                            File f = new File(documentsPath + File.separator + file_name);
                            if (f.exists()) {
                                String storageUrl = saveFile(documentsPath + File.separator + file_name);
                                originFile.addUrl(file_name.substring(0, file_name.indexOf('.')), storageUrl);
                            }
                        }
                    }
                }
                for (String fileUrl : url.split(",")) {
                    originFile.addUrl(fileUrl, fileUrl);
                }
                cdaDocument.getOriginFiles().add(originFile);
            }
        }
    }

    private String saveFile(String fileName) throws Exception {
        ObjectNode objectNode = fastDFSUtil.upload(fileName, "File from unstructured profile package.");

        return objectNode.get(FastDFSUtil.GROUP_NAME).asText() + "/" + objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
    }
}
