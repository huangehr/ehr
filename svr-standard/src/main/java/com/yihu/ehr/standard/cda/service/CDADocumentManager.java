package com.yihu.ehr.standard.cda.service;
import com.yihu.ehr.standard.datasets.service.DataSet;
import com.yihu.ehr.standard.datasets.service.MetaData;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:17:50
 */
@Transactional
@Service
public class CDADocumentManager {

    @Autowired
    private CdaDataSetRelationshipManager cdaDataSetRelationshipManager;

    @PersistenceContext
    private EntityManager entityManager;

    Session currentSession(){
        return entityManager.unwrap(org.hibernate.Session.class);
    }


    /**
     * 同时删除多个时 CDAID用逗号隔开
     *
     * @param versionCode
     * @param ids
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean deleteDocument(String[] ids,String versionCode) {
        cdaDataSetRelationshipManager.deleteRelationshipByCdaId(versionCode,ids);
        Session session = currentSession();
        String sql = "delete from " + CDAVersionUtil.getCDATableName(versionCode) + " where id in(:ids)";
        Query query = session.createSQLQuery(sql);
        query.setParameterList("ids",ids);
        query.executeUpdate();
        return true;
    }

    /**
     * 根据CDAID获取CDA内容
     *
     * @param versionCode
     * @param ids
     */
    public List<CDADocument> getDocumentList(String[] ids,String versionCode) {

        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDATableName(versionCode);
        Query query = session.createSQLQuery("SELECT " +
                "t.id," +
                "t.code," +
                "t.create_date," +
                "t.create_user," +
                "t.name," +
                "t.print_out," +
                "t.schema_path," +
                "t.source_id," +
                "t.update_date," +
                "t.update_user," +
                "t.description," +
                "t.file_group," +
                "t.type " +
                "FROM " + strTableName + " t where t.id in (:ids)");
        query.setParameterList("ids",ids);
        List<Object> records = query.list();
        List<CDADocument> xcdaDocuments = new ArrayList<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            CDADocument cdaDocument = new CDADocument();
            cdaDocument.setId(record[0].toString());
            cdaDocument.setCode(record[1].toString());
            cdaDocument.setCreateDate((Date) record[2]);
            cdaDocument.setCreateUser(record[3].toString());
            cdaDocument.setName(record[4].toString());
            cdaDocument.setPrintOut(record[5] == null ? null : record[5].toString());
            cdaDocument.setSchema(record[6] == null ? null : record[6].toString());
            cdaDocument.setSourceId(record[7].toString());
            cdaDocument.setUpdateDate(record[8] == null ? null : (Date) record[8]);
            cdaDocument.setUpdateUser(record[9] == null ? null : record[9].toString());
            cdaDocument.setDescription(record[10] == null ? null : record[10].toString());
            cdaDocument.setFileGroup(record[11] == null ? null : record[11].toString());
            cdaDocument.setTypeId(record[12] == null ? null : record[12].toString());
            cdaDocument.setVersionCode(versionCode);
            xcdaDocuments.add(cdaDocument);
        }
        return xcdaDocuments;
    }

    @Transactional
    public CDADocument getDocument(String versionId, String cdaDocumentId) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDATableName(versionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.type,t.file_group " +
                "FROM " + strTableName + " t where t.id = '" + cdaDocumentId + "'";
        Query query = session.createSQLQuery(strSql);
        Object[] record = (Object[]) query.uniqueResult();

        CDADocument cdaDocument = new CDADocument();
        cdaDocument.setId(record[0].toString());
        cdaDocument.setCode(record[1].toString());
        cdaDocument.setCreateDate((Date) record[2]);
        cdaDocument.setCreateUser(record[3].toString());
        cdaDocument.setName(record[4].toString());
        cdaDocument.setPrintOut(record[5] == null ? null : record[5].toString());
        cdaDocument.setSchema(record[6] == null ? null : record[6].toString());
        cdaDocument.setSourceId(record[7].toString());
        cdaDocument.setUpdateDate(record[8] == null ? null : (Date) record[8]);
        cdaDocument.setUpdateUser(record[9] == null ? null : record[9].toString());
        cdaDocument.setDescription(record[10] == null ? null : record[10].toString());
        cdaDocument.setVersionCode(versionId);
        cdaDocument.setTypeId(record[11] == null ? null : record[11].toString());
        cdaDocument.setFileGroup(record[12] == null ? null : record[12].toString());
        return cdaDocument;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public int getDocumentCount(String versionCode,String code,String name,String type) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDATableName(versionCode);
        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.type,t.file_group " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (!StringUtils.isEmpty(code)) {
            strSql += " and t.code like :code";
        }
        if (!StringUtils.isEmpty(name)) {
            strSql += " and t.name like :name";
        }
        Query query = session.createSQLQuery(strSql);
        if (!StringUtils.isEmpty(code)){
            query.setString("code", "%" + code + "%");
        }
        if (!StringUtils.isEmpty(name)){
            query.setString("name", "%" + name + "%");
        }
        query.setString("type", type);
        return query.list().size();
    }


    /**
     * 根据查询条件获取CDA信息
     * 参数Key支持 Code/Name
     */
    public List<CDADocument> getDocumentList(String versionCode,String code,String name,String type,int page,int pageSize) {
        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDATableName(versionCode);
        String strSql = "SELECT t.id," +
                "t.code," +
                "t.create_date," +
                "t.create_user," +
                "t.name," +
                "t.print_out," +
                "t.schema_path," +
                "t.source_id," +
                "t.update_date," +
                "t.update_user," +
                "t.description," +
                "t.type," +
                "t.file_group " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (!StringUtils.isEmpty(code)) {
            strSql += " and t.code like :code";
        }
        if (!StringUtils.isEmpty(name)) {
            strSql += " and t.name like :name";
        }
        Query query = session.createSQLQuery(strSql);
        if (!StringUtils.isEmpty(code)){
            query.setString("code", "%" + code + "%");
        }
        if (!StringUtils.isEmpty(name)){
            query.setString("name", "%" + name + "%");
        }
        query.setString("type", type);
        if (pageSize != 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
        List<Object> records = query.list();
        List<CDADocument> cdaDocuments = new ArrayList<>();
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            CDADocument cdaDocument = new CDADocument();
            cdaDocument.setId(record[0].toString());
            cdaDocument.setCode(record[1].toString());
            cdaDocument.setCreateDate((Date) record[2]);
            cdaDocument.setCreateUser(record[3].toString());
            cdaDocument.setName(record[4].toString());
            cdaDocument.setPrintOut(record[5] == null ? null : record[5].toString());
            cdaDocument.setSchema(record[6] == null ? null : record[6].toString());
            cdaDocument.setSourceId(record[7].toString());
            cdaDocument.setUpdateDate(record[8] == null ? null : (Date) record[8]);
            cdaDocument.setUpdateUser(record[9] == null ? null : record[9].toString());
            cdaDocument.setDescription(record[10] == null ? null : record[10].toString());
            cdaDocument.setVersionCode(versionCode);
            cdaDocument.setTypeId(record[11] == null ? null : record[11].toString());
            cdaDocument.setFileGroup(record[12] == null ? null : record[12].toString());
            cdaDocuments.add(cdaDocument);
        }
        return cdaDocuments;
    }


    public boolean isDocumentExist(String versionId, String documentCode,String documentId) {

        Session session = currentSession();
        String strTableName = CDAVersionUtil.getCDATableName(versionId);
        String strSql;

        if(StringUtils.isEmpty(documentId)){
            strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                    "FROM " + strTableName + " t where t.`code`=:cdacode";
        }else{
            strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                    "FROM " + strTableName + " t where t.`code`=:cdacode and t.id!=:docId";
        }
        Query query = session.createSQLQuery(strSql);
        query.setString("cdacode", documentCode);
        if(!StringUtils.isEmpty(documentId)){
            query.setString("docId",documentId);
        }
        return query.list().size() > 0;
    }

    /**
     * 保存CDA信息
     *
     * @param xCDA
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean saveDocument(CDADocument xCDA) {

        CDADocument cdaDocument = (CDADocument) xCDA;
        Session session = currentSession();

        String strTableName = CDAVersionUtil.getCDATableName(cdaDocument.getVersionCode());
        String sql;
        Query query;
        String[] ids = new String[]{cdaDocument.getId()};
        List<CDADocument> xCda = getDocumentList(ids,cdaDocument.getVersionCode());
        if (xCda.size() == 0) {
            sql = "insert into " + strTableName +" "+
                    "(id," +
                    "code," +
                    "create_date," +
                    "create_user," +
                    "name," +
                    "print_out," +
                    "schema_path," +
                    "source_id," +
                    "update_date," +
                    "update_user," +
                    "hash," +
                    "description," +
                    "file_group," +
                    "type)  " +
                    "values(:id," +
                    ":code," +
                    ":create_date," +
                    ":create_user," +
                    ":name," +
                    ":print_out," +
                    ":schema_path," +
                    ":source_id," +
                    ":update_date," +
                    ":update_user," +
                    ":hash," +
                    ":description," +
                    ":file_group," +
                    ":type)";
        } else {
            sql = "update " + strTableName +
                    " set " +
                    "code = :code, " +
                    "create_date = :create_date, " +
                    "create_user = :create_user, " +
                    "name = :name, " +
                    "print_out = :print_out, " +
                    "schema_path = :schema_path, " +
                    "source_id = :source_id, " +
                    "update_date=:update_date," +
                    "update_user=:update_user," +
                    "hash=:hash," +
                    "description=:description," +
                    "file_group=:file_group," +
                    "type=:type" +
                    " where id = :id";

            cdaDocument.setUpdateDate(new Date());
        }

        query = session.createSQLQuery(sql);
        query.setString("id", cdaDocument.getId());
        query.setString("code", cdaDocument.getCode());
        query.setDate("create_date", cdaDocument.getCreateDate());
        query.setString("create_user", cdaDocument.getCreateUser());
        query.setString("name", cdaDocument.getName());
        query.setString("print_out", cdaDocument.getPrintOut());
        query.setString("schema_path", cdaDocument.getSchema());
        query.setString("source_id", cdaDocument.getSourceId());
        query.setDate("update_date", cdaDocument.getUpdateDate());
        query.setString("update_user", cdaDocument.getUpdateUser());
        query.setInteger("hash", cdaDocument.getHashCode());
        query.setString("description", cdaDocument.getDescription());
        query.setString("file_group", cdaDocument.getFileGroup() == null ? "" : cdaDocument.getFileGroup());
        query.setString("type", cdaDocument.getTypeId());
        query.executeUpdate();
        return true;
    }


    /**
     * 判断文件是否存在
     */
    public boolean isFileExists(String cdaId, String strVersionCode) {
        String strFilePath = FilePath(strVersionCode) + cdaId + ".xml";
        File file = new File(strFilePath);
        return file.exists();
    }

    public String FilePath(String strVersionCode) {
        //文件服务器路径
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";
        //路径分割符
        String splitMark = System.getProperty("file.separator");
        //文件路径
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + strVersionCode + splitMark;
        return strXMLFilePath;
    }

    /**
     * 根据CDA Id 和 版本编码 生成CDA文档
     * @param cdaId
     * @param versionCode
     * @return
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public boolean createCDASchemaFile(String cdaId, String versionCode) throws TransformerException, ParserConfigurationException, FileNotFoundException, UnsupportedEncodingException {
        //操作结果：0：现在失败 1：新增成功
        int iSetCount = cdaDataSetRelationshipManager.getRelationshipCountByCdaId(cdaId, versionCode);
        List<CdaDataSetRelationship> relationshipsList = cdaDataSetRelationshipManager.getCDADataSetRelationshipByCDAId(cdaId, versionCode, 1, iSetCount);

        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";
        String splitMark = System.getProperty("file.separator");
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark;
        File file = new File(strXMLFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String strFilepath = strXMLFilePath + cdaId + ".xsd";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("xs:schema");
        root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
        root.setAttribute("targetNamespace", "http://www.w3schools.com");
        root.setAttribute("xmlns", "http://www.w3schools.com");
        root.setAttribute("elementFormDefault", "qualified");
        doc.appendChild(root);
        for (int i = 0; i < relationshipsList.size(); i++) {
            //获取数据集
//            DataSet dataSet = relationshipsList.get(i).getDataset();
            DataSet dataSet = new DataSet();
            Element rowSet = doc.createElement("xs:table");
            rowSet.setAttribute("code", dataSet.getCode());
            root.appendChild(rowSet);

            //获取数据元
            //// TODO: 2016/2/5
//            List<MetaData> xMetaDatas = dataSet.getMetaDataList();
            List<MetaData> xMetaDatas = null;

            for (int j = 0; j < xMetaDatas.size(); j++) {
                Element rowEle = doc.createElement("xs:element");
                rowEle.setAttribute("code", xMetaDatas.get(j).getCode());

                String strColumnType = xMetaDatas.get(j).getColumnType().equals("") ? "VARCHAR" : xMetaDatas.get(j).getColumnType();

                rowEle.setAttribute("type", "xs:" + strColumnType);
                rowSet.appendChild(rowEle);
            }
        }
        //生成XML文件
        outputXml(doc, strFilepath);
        return true;
    }

    private void outputXml(Document doc, String fileName) throws TransformerException, FileNotFoundException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进

        OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
    }


}//end CDADocumentManager