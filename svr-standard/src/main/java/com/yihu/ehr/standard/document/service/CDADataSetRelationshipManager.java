package com.yihu.ehr.standard.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author AndyCai
 * @version 1.0
 * @created 02-9月-2015 14:00:55
 */
@Service
public class CDADataSetRelationshipManager extends BaseHbmService<BaseCDADataSetRelationship> {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private CDADocumentService cdaDocumentService;

    private final static String ENTITY_PRE = "com.yihu.ehr.standard.document.service.CDADataSetRelationship";


    public Class getRelationshipServiceEntity(String version){
        try {
            return Class.forName(ENTITY_PRE + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "CDA数据集关系", version);
        }
    }


    public Class getDocumentServiceEntity(String version){
        try {
            return Class.forName("com.yihu.ehr.standard.document.service.CDADocument" + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "CDA文档", version);
        }
    }

    /**
     * 根据关系ID删除关联关系
     *
     * @param ids
     */
    public boolean deleteRelationshipById(String version, String[] ids) {
        delete(ids, getRelationshipServiceEntity(version));
        return true;
    }

    /**
     * 根据CDAID删除CDA数据集关联关系
     * @param version
     * @param cdaIds
     */
    public void deleteRelationshipByCdaIds(String version,String[] cdaIds) {
        Class entityClass = getRelationshipServiceEntity(version);
        List<BaseCDADataSetRelationship> ls = search(entityClass, "cdaId="+ String.join(",", cdaIds));
        String ids = "";
        for(int i = 0;i<ls.size();i++){
            BaseCDADataSetRelationship cdaDataSetRelationship = ls.get(i);
            if(i==0){
                ids+=cdaDataSetRelationship.getId();
            }else {
                ids+=","+cdaDataSetRelationship.getId();
            }
            //ids += cdaDataSetRelationship.getId();
        }

        delete(ids.split(","), getRelationshipServiceEntity(version));
//        Session session = currentSession();
//        String sql = "delete from " + CDAVersionUtil.getCDADatasetRelationshipTableName(strVersion) + " where cda_id in(:cdaIds)";
//        Query query = session.createSQLQuery(sql);
//        query.setParameterList("cdaIds",cdaIds)
//        .executeUpdate();
    }

    /**
     * 根据CDAID获取关联关系
     * @param entityClass
     * @param fields
     * @param filters
     * @param sorts
     * @param size
     * @param page
     * @return
     */
    public List getCDADataSetRelationships(Class entityClass,String fields,String filters,String sorts,int size,int page) {

        List ls = search(entityClass, fields, filters, sorts, page, size);
        return ls;
    }

    public List getCDADataSetRelationshipByCDAId(Class entityClass,String cdaId) {

        List ls = search(entityClass, "cdaId="+cdaId);
        return ls;
    }


    /**
     * 获取关联关系
     */
    public List<CDADataSetRelationship> getCDADataSetRelationship(String versionCode) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(versionCode);
        String strSql = "SELECT t.id," +
                "t.cda_id," +
                "t.dataSet_id" +
                " from " + strTableName + " t ";
        List<Object> records = session.createSQLQuery(strSql).list();
        List<CDADataSetRelationship> infos = new ArrayList<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            CDADataSetRelationship info = new CDADataSetRelationship();
            info.setId(record[0].toString());
            info.setCdaId(record[1].toString());
            info.setDataSetId(record[2].toString());
            infos.add(info);
        }
        return infos;
    }


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
        deleteRelationshipByCdaIds(versionCode,new String[]{cdaId});

        Class entityClass = getDocumentServiceEntity(versionCode);
        List<CDADocument> cdaDocuments = cdaDocumentService.search(entityClass, "id="+ cdaId);

        if (cdaDocuments.size() <= 0) return false;

        BaseCDADocument cdaDocument = cdaDocuments.get(0);
        if (cdaDocument.getFileGroup() != null
                && !cdaDocument.getFileGroup().equals("")
                && cdaDocument.getSchema() != null
                && !cdaDocument.getSchema().equals("")) {
            fastDFSUtil.delete(cdaDocument.getFileGroup(), cdaDocument.getSchema());
        }
        if (dataSetIds==null) return true;

        List<CDADataSetRelationship> infos = new ArrayList<>();
        for (int i = 0; i < dataSetIds.length; i++) {
            String dataSetId = dataSetIds[i];
            CDADataSetRelationship info = new CDADataSetRelationship();
            info.setCdaId(cdaId);
            info.setDataSetId(dataSetId);
            infos.add(info);
        }
        //cdaDataSetRelationshipManager.addRelationship(infos,versionCode);
        if(infos.size()>0){
            addRelationship(infos,versionCode);
        }else{
            throw new ApiException(ErrorCode.GetStdVersionFailed);
        }
        String strFilePath = SaveCdaFile(xmlInfo, versionCode, cdaId);

        //将文件上传到服务器中
        ObjectNode msg = fastDFSUtil.upload(strFilePath, "");
        String strFileGroup = msg.get(FastDFSUtil.GroupField).asText();
        String strSchemePath = msg.get(FastDFSUtil.RemoteFileField).asText();
        File file = new File(strFilePath);

        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }

        boolean bRes = SaveXmlFilePath(new String[]{cdaId}, versionCode, strFileGroup, strSchemePath);
        return bRes;
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
        strPath += "StandardFiles";

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

        Class entityClass  = getDocumentServiceEntity(versionCode);

        List<CDADocument> cdaDocuments = cdaDocumentService.search(entityClass, "id="+ String.join(",",cdaIds));
        if (cdaDocuments.size() <= 0) {
            return false;
        }

        BaseCDADocument cdaDocument = cdaDocuments.get(0);
        cdaDocument.setFileGroup(fileGroup);
        cdaDocument.setSchema(filePath);
        cdaDocument.setVersionCode(versionCode);
        cdaDocumentService.saveCdaDocument(cdaDocument);
        return true;
    }

    public CDADocument saveCdaInfo(String cdaDocumentJsonData) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CDADocument cdaInfo = objectMapper.readValue(cdaDocumentJsonData, CDADocument.class);
        if(cdaDocumentService.isDocumentExist(cdaInfo.getVersionCode(), cdaInfo.getCode(), cdaInfo.getId())){
            throw new Exception("已存在");
        }

        cdaDocumentService.save(cdaInfo);
        return cdaInfo;
    }


    /**
     * 新增关联关系
     *
     * @param cdaDatasetRelationships
     */
    public boolean addRelationship(List<CDADataSetRelationship> cdaDatasetRelationships,String versionCode) {
        Session session = currentSession();

        String strTableName = CDAVersionUtil.getCDADatasetRelationshipTableName(versionCode);
        String sql;
        Query query;

        String strValues = "";
        for (int i = 0; i < cdaDatasetRelationships.size(); i++) {
            CDADataSetRelationship info = (CDADataSetRelationship) cdaDatasetRelationships.get(i);
            if (org.springframework.util.StringUtils.isEmpty(info.getId())) {
                String strId = UUID.randomUUID().toString();
                info.setId(strId);
            }
            strValues += "('" + info.getId() + "','" + info.getCdaId() + "','" + info.getDataSetId() + "'),";
        }
        strValues = strValues.substring(0, strValues.length() - 1);

        sql = "insert into " + strTableName +
                " (id,cda_id,dataset_id) " +
                "values " + strValues;
        query = session.createSQLQuery(sql);
        query.executeUpdate();
        return true;
    }


}