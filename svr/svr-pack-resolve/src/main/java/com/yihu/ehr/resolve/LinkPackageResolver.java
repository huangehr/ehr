package com.yihu.ehr.resolve;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.util.MetaDataRecord;
import com.yihu.ehr.resolve.model.stage1.LinkFile;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackageDataSet;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.ftp.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

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
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Override
    public void resolve(StandardPackage profile, File root) throws IOException, ParseException , NoSuchAlgorithmException, MyException {
        LinkPackage linkPackModel = (LinkPackage) profile;

        File indexFile = new File(root.getAbsolutePath() + File.separator  + "index"+ File.separator+"patient_index.json");
        parseFile(linkPackModel, indexFile);
    }

    private void parseFile(LinkPackage profile, File indexFile) throws IOException, ParseException, NoSuchAlgorithmException, MyException {
        JsonNode jsonNode = objectMapper.readTree(indexFile);

        String patientId = jsonNode.get("patient_id").asText();
        String eventNo = jsonNode.get("event_no").asText();
        String orgCode = jsonNode.get("org_code").asText();
        String version = jsonNode.get("inner_version").asText();
        String eventDate = jsonNode.get("event_time").asText();
        String expireDate = jsonNode.get("expire_date").asText();
        //if (version.equals("000000000000")) throw new LegacyPackageException("Package is collected via cda version 00000000000, ignored.");

        profile.setPatientId(patientId);
        profile.setEventNo(eventNo);
        profile.setOrgCode(orgCode);
        profile.setCdaVersion(version);
        profile.setEventDate(DateUtil.strToDate(eventDate));
        profile.setExpireDate(DateUtil.strToDate(expireDate));

        // dataset节点，存储数据集URL
        JsonNode dataSetNode = jsonNode.get("dataset");
        Iterator<String> fieldNames = dataSetNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();
            String url = dataSetNode.get(dataSetCode).asText();

            LinkPackageDataSet dataSet = new LinkPackageDataSet();
            dataSet.setOrgCode(orgCode);
            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCode(dataSetCode);
            dataSet.setUrl(url);
            dataSet.setCdaVersion(version);

            profile.insertDataSet(dataSetCode, dataSet);
        }

        //--------------增加ftp影像文件解析
        JsonNode filesNode = jsonNode.get("files");
        if(filesNode != null){
            List<LinkFile> linkFiles = profile.getLinkFiles();

            ArrayNode arrayNode = (ArrayNode) filesNode;
            FtpUtils ftpUtils = new FtpUtils(username, password, address, port);
            ftpUtils.connect();
            FTPClient ftpClient = ftpUtils.getFtpClient();
            for (int i = 0; i < arrayNode.size(); ++i){
                JsonNode fileNode = arrayNode.get(i);
                String fileName = fileNode.get("file").asText();
                String fileExtension="";
                if (fileName.contains(".")) {
                    fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                } else {
                    throw new RuntimeException("上传轻量档案文件失败, 文件缺失扩展名.");
                }
                String url = fileNode.get("url").asText();
                String path = url.substring(5);//将url前面的ftp:/截取掉,剩下的path为文件的完整路径(包含文件名)

                FTPFile[] ftpFiles = ftpClient.listFiles(path);
                if(ftpFiles==null || ftpFiles.length==0){
                    throw new RuntimeException("ftp上找不到该文件:"+path);
                }
                InputStream inputStream = ftpUtils.getInputStream(path);
                long fileSize = ftpFiles[0].getSize();
                NameValuePair[] fileMetaData = new NameValuePair[1];
                fileMetaData[0] = new NameValuePair("description", "File from link profile package.");
                ObjectNode msg = fastDFSUtil.uploadBySocket(inputStream, fileExtension, (int)fileSize,fileMetaData);
                LinkFile linkFile = new LinkFile();
                linkFile.setFileExtension(fileExtension);
                linkFile.setOriginName(fileName);

                String fastdfsUrl = msg.get(FastDFSUtil.GROUP_NAME).asText() + "/" + msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
                linkFile.setUrl(fastdfsUrl);
                linkFiles.add(linkFile);
                path = path.substring(0,path.length()-fileName.length());//文件路径,不包含文件名
                ftpUtils.deleteFile(path,fileName);
            }
            ftpUtils.connect();
        }
        //----------------------ftp影像文件解析end----

        // summary节点可能不存在
        JsonNode summaryNode = jsonNode.get("summary");
        if (summaryNode == null) return;

        fieldNames = summaryNode.fieldNames();
        while (fieldNames.hasNext()) {
            String dataSetCode = fieldNames.next();

            LinkPackageDataSet linkPackageDataSet = (LinkPackageDataSet)profile.getDataSet(dataSetCode);
            if (linkPackageDataSet == null) linkPackageDataSet = new LinkPackageDataSet();

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
            linkPackageDataSet.setOrgCode(orgCode);
            linkPackageDataSet.setPatientId(patientId);
            linkPackageDataSet.setEventNo(eventNo);
            linkPackageDataSet.setCdaVersion(version);
            linkPackageDataSet.setCode(dataSetCode);
            profile.insertDataSet(dataSetCode, linkPackageDataSet);
        }
    }
}
