package com.yihu.ehr.service.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.core.*;
import com.yihu.ehr.profile.util.ProfileFactory;
import com.yihu.ehr.profile.util.QualifierTranslator;
import com.yihu.ehr.schema.StdKeySchema;
import com.yihu.ehr.util.DateTimeUtils;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.StringUtils;
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
    CacheReader cacheReader;

    @Autowired
    StdKeySchema keySchema;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Override
    public void resolve(StdProfile profile, File root) throws Exception {
        FileProfile fileProfile = (FileProfile) profile;

        File metaFile = new File(root.getAbsolutePath() + File.separator + "meta.json");
        parseFile(fileProfile, metaFile);
    }

    private void parseFile(FileProfile profile, File metaFile) throws Exception {
        JsonNode root = objectMapper.readTree(metaFile);

        String demographicId = root.get("demographic_id").asText();
        String patientId = root.get("patient_id").asText();
        String eventNo = root.get("event_no").asText();
        int eventType = root.get("event_type").asInt();
        String orgCode = root.get("org_code").asText();
        String eventDate = root.get("event_time").asText();
        String createDate = root.get("create_date").asText();
        String cdaVersion = root.get("inner_version").asText();

        profile.setDemographicId(demographicId);
        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setEventType(EventType.create(eventType));
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(cdaVersion);
        profile.setCreateDate(DateTimeUtils.simpleDateParse(createDate));
        profile.setEventDate(DateTimeUtils.simpleDateParse(eventDate));

        parseDataSets(profile, (ObjectNode) root.get("data_sets"));
        parseFiles(profile, (ArrayNode) root.get("files"), metaFile.getParent() + File.separator + ProfileFactory.DocumentFolder);
    }

    private void parseDataSets(FileProfile profile, ObjectNode dataSets) {
        if (dataSets == null) return;

        Iterator<Map.Entry<String, JsonNode>> iterator = dataSets.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> item = iterator.next();
            String dataSetCode = item.getKey();
            ArrayNode records = (ArrayNode) item.getValue();

            StdDataSet dataSet = new StdDataSet();
            dataSet.setCode(dataSetCode);
            dataSet.setPatientId(profile.getPatientId());
            dataSet.setEventNo(profile.getEventNo());
            dataSet.setOrgCode(profile.getOrgCode());
            dataSet.setCdaVersion(profile.getCdaVersion());

            for (int i = 0; i < records.size(); ++i) {
                DataRecord record = new DataRecord();

                ObjectNode jsonRecord = (ObjectNode) records.get(i);
                Iterator<Map.Entry<String, JsonNode>> filedIterator = jsonRecord.fields();
                while (filedIterator.hasNext()) {
                    Map.Entry<String, JsonNode> field = filedIterator.next();
                    String metaData = translateMetaDataCode(profile.getCdaVersion(), dataSetCode, field.getKey());
                    String value = field.getValue().asText();

                    record.putMetaData(metaData, value);
                }

                dataSet.addRecord(Integer.toString(i), record);
            }

            profile.insertDataSet(dataSetCode, dataSet);
        }
    }

    private void parseFiles(FileProfile profile, ArrayNode files, String documentsPath) throws Exception {
        for (int i = 0; i < files.size(); ++i) {
            ObjectNode objectNode = (ObjectNode) files.get(i);
            String cdaDocumentId = objectNode.get("cda_doc_id").asText();
            String url = objectNode.get("url").asText();
            Date expireDate = DateTimeUtils.simpleDateParse(objectNode.get("expire_date").asText());

            // 解析过程中，使用cda文档id作为文档列表的主键，待解析完成后，统一更新为rowkey
            CdaDocument cdaDocument = profile.getDocuments().get(cdaDocumentId);
            if (cdaDocument == null){
                cdaDocument = new CdaDocument();
                cdaDocument.setId(cdaDocumentId);

                profile.getDocuments().put(cdaDocumentId, cdaDocument);
            }

            ArrayNode content = (ArrayNode) objectNode.get("content");
            for (int j = 0; j < content.size(); ++j) {
                ObjectNode file = (ObjectNode) content.get(j);
                String mimeType = file.get("mime_type").asText();
                String fileList[] = file.get("name").asText().split(";");

                OriginFile originFile = new OriginFile();
                originFile.setMime(mimeType);
                originFile.setOriginUrl(url);
                originFile.setExpireDate(expireDate);

                for (String fileName : fileList){
                    String storageUrl = saveFile(documentsPath + File.separator + fileName);
                    originFile.addStorageUrl(fileName.substring(0, fileName.lastIndexOf('.')), storageUrl);
                }

                cdaDocument.getOriginFiles().add(originFile);
            }
        }
    }

    protected String translateMetaDataCode(String cdaVersion,
                                           String dataSetCode,
                                           String metaData) {
        String metaDataType = cacheReader.read(keySchema.metaDataType(cdaVersion, dataSetCode, metaData));
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
