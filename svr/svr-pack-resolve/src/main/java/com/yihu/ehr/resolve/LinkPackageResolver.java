package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.exception.ResolveException;
import com.yihu.ehr.profile.extractor.KeyDataExtractor;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.model.LinkPackageDataSet;
import com.yihu.ehr.profile.model.MetaDataRecord;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.details.LinkFile;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.ftp.FtpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.csource.common.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 轻量级档案包解析器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class LinkPackageResolver extends PackageResolver {

    @Value("${ftp.address}")
    private String address;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.port}")
    private int port;
    @Value("${fast-dfs.pacs-group-name:group1}")
    private String groupName;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Override
    public void resolve(OriginalPackage originalPackage, File root) throws Exception {
        File indexFile = new File(root.getAbsolutePath() + File.separator  + "index" + File.separator + "patient_index.json");
        parseFile((LinkPackage) originalPackage, indexFile);
    }

    private void parseFile (LinkPackage linkPackage, File indexFile) throws Exception{
        JsonNode jsonNode = objectMapper.readTree(indexFile);
        if (jsonNode.isNull()) {
            throw new IllegalJsonFileException("Invalid json file when generate data set");
        }
        String patientId = jsonNode.get("patient_id") == null ? "" : jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no") == null ? "" : jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code") == null ? "" : jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version") == null ? "" : jsonNode.get("inner_version").asText();
        String visitType = jsonNode.get("visit_type") == null? "" : jsonNode.get("visit_type").asText();
        String eventDate = jsonNode.get("event_time") == null ? "" : jsonNode.get("event_time").asText();
        String expireDate = jsonNode.get("expire_date") == null? "" : jsonNode.get("expire_date").asText();

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
        if (StringUtils.isEmpty(version)) {
            errorMsg.append("innerVersion is null;");
        }
        if (StringUtils.isEmpty(eventDate)) {
            errorMsg.append("eventTime is null;");
        }
        if (StringUtils.isEmpty(visitType)) {
            errorMsg.append("visitType is null;");
        }
        if (!StringUtils.isEmpty(errorMsg.toString())){
            throw new IllegalJsonDataException(errorMsg.toString());
        }
        linkPackage.setPatientId(patientId);
        linkPackage.setEventNo(eventNo);
        linkPackage.setEventType(EventType.create(visitType));
        linkPackage.setOrgCode(orgCode);
        linkPackage.setCdaVersion(version);
        linkPackage.setEventTime(DateUtil.strToDate(eventDate));
        linkPackage.setVisitType(visitType);
        linkPackage.setExpireDate(DateUtil.strToDate(expireDate));
        // dataset节点，存储数据集URL
        JsonNode dataSetNode = jsonNode.get("dataset");
        Iterator<String> fieldNames = dataSetNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();
            String url = dataSetNode.get(dataSetCode).asText();
            LinkPackageDataSet dataSet = new LinkPackageDataSet();
            dataSet.setCode(dataSetCode);
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setOrgCode(orgCode);
            dataSet.setCdaVersion(version);
            dataSet.setEventTime(DateUtil.strToDate(eventDate));
            dataSet.setUrl(url);
            linkPackage.insertDataSet(dataSetCode, dataSet);
        }

        //--------------增加ftp影像文件解析
        JsonNode filesNode = jsonNode.get("files");
        if (filesNode != null){
            List<LinkFile> linkFiles = linkPackage.getLinkFiles();

            ArrayNode arrayNode = (ArrayNode) filesNode;
            FtpUtils ftpUtils = null;
            try {
                ftpUtils = new FtpUtils(username, password, address, port);
                ftpUtils.connect();
                FTPClient ftpClient = ftpUtils.getFtpClient();
                for (int i = 0; i < arrayNode.size(); ++i){
                    JsonNode fileNode = arrayNode.get(i);
                    JsonNode file = fileNode.get("file");
                    if (null == file){
                        throw new IllegalJsonFileException("fileName is null.");
                    }
                    String fileName = file.asText();
                    String fileExtension;
                    if (fileName.contains(".")) {
                        fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                    } else {
                        throw new IllegalJsonFileException("上传影像档案文件失败, 文件缺失扩展名.");
                    }
                    JsonNode urlNode = fileNode.get("url");
                    if(urlNode == null){
                        throw new IllegalJsonFileException("缺失ftp路径地址");
                    }
                    String url = urlNode.asText();
                    if(!url.startsWith("ftp:/")){
                        throw new IllegalJsonFileException("ftp路径地址格式有误");
                    }
                    String path = url.substring(5);//将url前面的ftp:/截取掉,剩下的path为文件的完整路径(包含文件名)
                    FTPFile[] ftpFiles = ftpClient.listFiles(path);
                    if (ftpFiles == null || ftpFiles.length == 0){
                        throw new ResolveException("ftp上找不到该文件:" + path);
                    }
                    JsonNode reportFormNoNode = fileNode.get("report_form_no");
                    if(reportFormNoNode == null){
                        throw new ResolveException("report_form_no is null");
                    }
                    String reportFormNo = reportFormNoNode.asText();
                    JsonNode serialNoNode = fileNode.get("serial_no");
                    if(serialNoNode == null){
                        throw new ResolveException("serial_no is null");
                    }
                    String serialNo = serialNoNode.asText();
                    InputStream inputStream = ftpUtils.getInputStream(path);
                    long fileSize = ftpFiles[0].getSize();
                    NameValuePair[] fileMetaData = new NameValuePair[1];
                    fileMetaData[0] = new NameValuePair("description", "File from link profile package.");
                    //影像文件存在group2
                    ObjectNode msg = fastDFSUtil.upload(groupName, inputStream, fileExtension, fileMetaData);
                    LinkFile linkFile = new LinkFile();
                    linkFile.setFileSize(fileSize);
                    linkFile.setFileExtension(fileExtension);
                    linkFile.setOriginName(fileName);
                    linkFile.setReportFormNo(reportFormNo);
                    linkFile.setSerialNo(serialNo);
                    String fastdfsUrl = msg.get(FastDFSUtil.GROUP_NAME).asText() + "/" + msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
                    linkFile.setUrl(fastdfsUrl);
                    linkFiles.add(linkFile);
                    path = path.substring(0, path.length() - fileName.length());//文件路径,不包含文件名
                    ftpUtils.deleteFile(path, fileName);
                }
            } finally {
                if (ftpUtils != null){
                    ftpUtils.closeConnect();
                }
            }

        }
        //----------------------ftp影像文件解析end----

        // summary节点可能不存在
        JsonNode summaryNode = jsonNode.get("summary");
        if (summaryNode == null) {
            return;
        }
        fieldNames = summaryNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();
            LinkPackageDataSet linkPackageDataSet = (LinkPackageDataSet)linkPackage.getDataSet(dataSetCode);
            if (linkPackageDataSet == null) {
                linkPackageDataSet = new LinkPackageDataSet();
            }
            linkPackageDataSet.setCode(dataSetCode);
            linkPackageDataSet.setPatientId(patientId);
            linkPackageDataSet.setEventNo(eventNo);
            linkPackageDataSet.setOrgCode(orgCode);
            linkPackageDataSet.setCdaVersion(version);
            linkPackageDataSet.setEventTime(DateUtil.strToDate(eventDate));
            ArrayNode arrayNode = (ArrayNode) summaryNode.get(dataSetCode);
            for (int i = 0; i < arrayNode.size(); ++i){
                MetaDataRecord record = new MetaDataRecord();
                Iterator<String> metaDataCodes = arrayNode.get(i).fieldNames();
                while (metaDataCodes.hasNext()){
                    String metaDataCode = metaDataCodes.next();
                    record.putMetaData(metaDataCode, arrayNode.get(i).get(metaDataCode).asText());
                }
                linkPackageDataSet.addRecord(Integer.toString(linkPackageDataSet.getRecordCount()), record);
            }
            //提取身份信息
            if (StringUtils.isEmpty(linkPackage.getDemographicId()) || StringUtils.isEmpty(linkPackage.getPatientName())) {
                Map<String, Object> properties = extractorChain.doExtract(linkPackageDataSet, KeyDataExtractor.Filter.Identity);
                String demographicId = (String) properties.get(ResourceCells.DEMOGRAPHIC_ID);
                String patientName = (String) properties.get(ResourceCells.PATIENT_NAME);
                if (!StringUtils.isEmpty(demographicId)) {
                    linkPackage.setDemographicId(demographicId.trim());
                }
                if (!StringUtils.isEmpty(patientName)) {
                    linkPackage.setPatientName(patientName);
                }
            }
            linkPackage.insertDataSet(dataSetCode, linkPackageDataSet);
        }
    }
}
