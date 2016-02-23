package com.yihu.ehr.standard.cda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 版本管理实现类。
 *
 * @author Sand
 * @version 1.0
 * @updated 02-7月-2015 14:40:20
 */
@Service
@Transactional
public class CDAManager {

    @Autowired
    private CdaDatasetRelationshipManager cdaDatasetRelationshipManager;

    @Autowired
    private CDADocumentManager cdaDocumentManager;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，在新增信息
     * @param dataSetIds 关联的数据集
     * @param cdaId  cda文档 ID
     * @param versionCode 版本号
     * @param xmlInfo xml 文件内容
     * @return 操作结果
     */
    public boolean SaveDataSetRelationship(String[] dataSetIds,String cdaId,String versionCode, String xmlInfo) throws Exception {

        cdaDatasetRelationshipManager.deleteRelationshipByCdaId(versionCode,new String[]{cdaId});
        List<CDADocument> cdaDocuments = cdaDocumentManager.getDocumentList(versionCode,new String[]{cdaId});
        if (cdaDocuments.size() <= 0) {
            return false;
            //请先选择CDA
        }
        CDADocument cdaDocument = cdaDocuments.get(0);
        if (cdaDocument.getFileGroup() != null
                && !cdaDocument.getFileGroup().equals("")
                && cdaDocument.getSchema() != null
                && !cdaDocument.getSchema().equals("")) {
            fastDFSUtil.delete(cdaDocument.getFileGroup(), cdaDocument.getSchema());
        }
        if (dataSetIds==null) {
            //关系保存成功
            return true;
        }
        List<CdaDataSetRelationship> infos = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            String dataSetId = dataSetIds[i];
            CdaDataSetRelationship info = new CdaDataSetRelationship();
            info.setCdaId(cdaId);
            info.setDataSetId(dataSetId);
            infos.add(info);
        }
        cdaDatasetRelationshipManager.addRelationship(infos,versionCode);
        if(infos.size()>0){
            cdaDatasetRelationshipManager.addRelationship(infos,versionCode);
        }else{
            throw new ApiException(ErrorCode.GetStdVersionFailed)
            return false;
        }
        String strFilePath = SaveCdaFile(xmlInfo, versionCode, cdaId);
        //将文件上传到服务器中
        ObjectNode msg = fastDFSUtil.upload(strFilePath, "");
        String strFileGroup = msg.get(FastDFSUtil.GroupField).asText();//setFilePath
        String strSchemePath = msg.get(FastDFSUtil.RemoteFileField).asText();//setFileName
        File file = new File(strFilePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        boolean bRes = SaveXmlFilePath(new String[]{cdaId}, versionCode, strFileGroup, strSchemePath);
        if (bRes) {
            //关系保存成功
            return true;
        } else {
            //关系保存失败
            return false;
        }
    }

    /**
     * 将String 保存为XML文件
     *
     * @param fileInfo 文件信息
     * @return 返回 文件路径
     */
    public String SaveCdaFile(String fileInfo, String versionCode, String cdaId) throws IOException {
        fileInfo = fileInfo.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        String strPath = System.getProperty("java.io.tmpdir");
        String splitMark = System.getProperty("file.separator");
        strPath += splitMark+"StandardFiles";
        //文件路径
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "createfile" + splitMark + cdaId + ".xml";

        File file = new File(strXMLFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(fileInfo);
        bw.flush();
        bw.close();
        fw.close();

        return strXMLFilePath;
    }

    public boolean SaveXmlFilePath(String[] cdaIds, String versionCode, String fileGroup, String filePath) {
        List<CDADocument> cdaDocuments = cdaDocumentManager.getDocumentList(versionCode, cdaIds);
        if (cdaDocuments.size() <= 0) {
            //未找到CDA
            return false;
        }
        CDADocument cdaDocument = cdaDocuments.get(0);
        cdaDocument.setFileGroup(fileGroup);
        cdaDocument.setSchema(filePath);
        cdaDocument.setVersionCode(versionCode);
        return cdaDocumentManager.saveDocument(cdaDocument);
    }

    public CDADocument SaveCdaInfo(String cdaDocumentJsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CDADocument cdaInfo = objectMapper.readValue(cdaDocumentJsonData, CDADocument.class);
        if(cdaDocumentManager.isDocumentExist(cdaInfo.getVersionCode(), cdaInfo.getCode(), cdaInfo.getId())){
            throw new Exception("已存在");
        }
        cdaDocumentManager.saveDocument(cdaInfo);
        return cdaInfo;
    }



}