package com.yihu.ehr.service.resource.stage1.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.constants.UrlScope;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XRedisServiceClient;
import com.yihu.ehr.profile.exception.LegacyPackageException;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.profile.util.PackageDataSet;
import com.yihu.ehr.service.resource.stage1.*;
import com.yihu.ehr.service.resource.stage1.PackModelFactory;
import com.yihu.ehr.profile.util.QualifierTranslator;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

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
    XRedisServiceClient redisServiceClient;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Override
    public void resolveDataSet(StandardPackage profile, File root) throws Exception {

    }

    @Override
    public void resolve(StandardPackage profile, File root) throws Exception {
        FilePackage filePackModel = (FilePackage) profile;

        File metaFile = new File(root.getAbsolutePath() + File.separator + "documents.json");
        parseFile(filePackModel, metaFile);
    }

    private void parseFile(FilePackage profile, File metaFile) throws Exception {
        JsonNode root = objectMapper.readTree(metaFile);

        String demographicId = root.get("demographic_id") == null ? null : root.get("demographic_id").asText();
        String patientId = root.get("patient_id").asText();
        String eventNo = root.get("event_no").asText();
        int eventType = root.get("event_type").asInt();
        String orgCode = root.get("org_code").asText();
        String eventDate = root.get("event_time").asText();
        String createDate = root.get("create_date") == null ? null : root.get("create_date").asText();
        String cdaVersion = root.get("inner_version").asText();
        //if (cdaVersion.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        profile.setDemographicId(demographicId);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventType(EventType.create(eventType));
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(cdaVersion);
        profile.setCreateDate(DateTimeUtil.simpleDateParse(createDate));
        profile.setEventDate(DateTimeUtil.simpleDateParse(eventDate));

        parseDataSets(profile, (ObjectNode) root.get("data_sets"));
        parseFiles(profile, (ArrayNode) root.get("files"), metaFile.getParent() + File.separator + PackModelFactory.DocumentFolder);
    }

    private void parseDataSets(FilePackage profile, ObjectNode dataSets) {
        if (dataSets == null) return;

        Iterator<Map.Entry<String, JsonNode>> iterator = dataSets.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> item = iterator.next();
            String dataSetCode = item.getKey();
            ArrayNode records = (ArrayNode) item.getValue();

            PackageDataSet dataSet = new PackageDataSet();
            dataSet.setCode(dataSetCode);
            dataSet.setPatientId(profile.getPatientId());
            dataSet.setEventNo(profile.getEventNo());
            dataSet.setOrgCode(profile.getOrgCode());
            dataSet.setCdaVersion(profile.getCdaVersion());

            for (int i = 0; i < records.size(); ++i) {
                MetaDataRecord record = new MetaDataRecord();

                ObjectNode jsonRecord = (ObjectNode) records.get(i);
                Iterator<Map.Entry<String, JsonNode>> filedIterator = jsonRecord.fields();
                while (filedIterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = filedIterator.next();
                    //String metaData = translateMetaDataCode(profile.getCdaVersion(), dataSetCode, field.getKey());
                    String value = field.getValue().asText();
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
                String mine_type = file.get("mine_type").asText();
                String url_scope = file.get("url_scope").asText();
                String url = file.get("url").asText();
                String emr_id = file.get("emr_id").asText();
                String emr_name = file.get("emr_name").asText();
                String note = file.has("note")?file.get("note").asText():"";

                OriginFile originFile = new OriginFile();
                originFile.setMime(mine_type);
                originFile.setExpireDate(expireDate);
                originFile.setUrlScope(UrlScope.valueOf(Integer.parseInt(url_scope)));
                originFile.setEmrId(emr_id);
                originFile.setEmrName(emr_name);
                if(!StringUtils.isBlank(note))
                {
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

    protected String translateMetaDataCode(String cdaVersion,
                                           String dataSetCode,
                                           String metaData) {
        String metaDataType = redisServiceClient.getMetaDataType(cdaVersion, dataSetCode, metaData);
        if (StringUtils.isEmpty(metaDataType)) {
            String msg = "Meta data %1 in data set %2 is not found in version %3. FORGET cache standards or it's INVALID?"
                    .replace("%1", metaData)
                    .replace("%2", dataSetCode)
                    .replace("%3", cdaVersion);

            LogService.getLogger().error(msg);
            return null;
        }

        return QualifierTranslator.hBaseQualifier(metaData, metaDataType);
    }

    protected String saveFile(String fileName) throws Exception {
        ObjectNode objectNode = fastDFSUtil.upload(fileName, "File from unstructured profile package.");

        return objectNode.get(FastDFSUtil.GroupField).asText() + "/" + objectNode.get(FastDFSUtil.RemoteFileField).asText();
    }
}
