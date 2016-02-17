package com.yihu.ehr.standard.cda.service;
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
import java.util.Map;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:17:50
 */
@Transactional
@Service
public class CDADocumentManager {

    @Autowired
    private CdaDatasetRelationshipManager cdaDatasetRelationshipManager;

    @PersistenceContext
    private EntityManager entityManager;

    Session currentSession(){
        return entityManager.unwrap(org.hibernate.Session.class);
    }


    /**
     * 同时删除多个时 CDAID用逗号隔开
     *
     * @param strVersionId
     * @param ids
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int deleteDocument(String strVersionId, List<String> ids) {
        int result = 0;
        String strIds = org.apache.commons.lang.StringUtils.join(ids, "','");
        strIds = "'" + strIds + "'";

        result = cdaDatasetRelationshipManager.deleteRelationshipByCdaId(strVersionId, ids);
        if (result > -1) {
            Session session = currentSession();
            String sql = "delete from " + CDAVersion.getCDATableName(strVersionId) + " where id in(" + strIds + ")";
            result = session.createSQLQuery(sql).executeUpdate();
        }
        return result;
    }

    /**
     * 根据CDAID获取CDA内容
     *
     * @param strVersionId
     * @param ids
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public CDADocument[] getDocumentList(String strVersionId, List<String> ids) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);


        String strIds = org.apache.commons.lang.StringUtils.join(ids, "','");
        strIds = "'" + strIds + "'";

        Query query = session.createSQLQuery("SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.file_group,t.type " +
                "FROM " + strTableName + " t where t.id in (" + strIds + ")");

        List<Object> records = query.list();

        CDADocument[] xcdaDocuments = new CDADocument[records.size()];

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
            cdaDocument.setVersionCode(strVersionId);
            xcdaDocuments[i] = cdaDocument;
        }

        return xcdaDocuments;
    }

    @Transactional
    public CDADocument getDocument(String versionId, String cdaDocumentId) {
        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(versionId);

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
    public int getDocumentCount(String strVersionId, String strKey,String cdaType) {
        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.type,t.file_group " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (strKey != null && strKey != "") {
            strSql += " and (t.code like :key or t.`name` like :key)";
        }

        Query query = session.createSQLQuery(strSql);
        if (strKey != null && strKey != "")
            query.setString("key", "%" + strKey + "%");

        query.setString("type", cdaType);

        if (query == null)
            return 0;

        List<Object> records = query.list();

        return records.size();
    }

    /**
     * 根据查询条件获取CDA信息
     * 参数Key支持 Code/Name
     *
     * @param strVersionId
     * @param strKey
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public CDADocument[] getDocumentList(String strVersionId, String strKey) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.type,t.file_group " +
                "FROM " + strTableName + " t where 1=1 ";
        if (strKey != null && strKey != "") {
            strSql += " and t.code like ':key' or t.`name` like ':key'";
        }

        Query query = session.createSQLQuery(strSql);
        if (strKey != null && strKey != "")
            query.setString("key", "%" + strKey + "%");

        List<Object> records = query.list();

        CDADocument[] xcdaDocuments = new CDADocument[records.size()];

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
            cdaDocument.setVersionCode(strVersionId);
            cdaDocument.setTypeId(record[11] == null ? null : record[11].toString());
            cdaDocument.setFileGroup(record[12] == null ? null : record[12].toString());
            xcdaDocuments[i] = cdaDocument;
        }

        return xcdaDocuments;
    }

    /**
     * 根据查询条件获取CDA信息
     * 参数Key支持 Code/Name
     *
     * @param strVersionId
     * @param strKey
     * @param page
     * @param pageSize
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADocument> getDocumentList(String strVersionId, String strKey, String strType, int page, int pageSize) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.type,t.file_group " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (strKey != null && strKey != "") {
            strSql += " and (t.code like :key or t.`name` like :key)";
        }

        Query query = session.createSQLQuery(strSql);
        if (strKey != null && strKey != "")
            query.setString("key", "%" + strKey + "%");

        query.setString("type", strType);
        if (pageSize != 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
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
            cdaDocument.setVersionCode(strVersionId);
            cdaDocument.setTypeId(record[11] == null ? null : record[11].toString());
            cdaDocument.setFileGroup(record[12] == null ? null : record[12].toString());
            xcdaDocuments.add(cdaDocument);
        }

        return xcdaDocuments;
    }


    public boolean isDocumentExist(String versionId, String documentCode,String documentId) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(versionId);
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

        String strTableName = CDAVersion.getCDATableName(cdaDocument.getVersionCode());
        String sql;
        Query query;

        List<String> ids = new ArrayList<>();
        ids.add(cdaDocument.getId());
        CDADocument[] xCda = getDocumentList(cdaDocument.getVersionCode(), ids);

        if (xCda.length == 0) {
            sql = "insert into " + strTableName +
                    " ( id, code, create_date, create_user, name, print_out, schema_path, source_id, update_date, update_user,hash,description,file_group,type)  " +
                    "values(:id, :code, :create_date, :create_user, :name, :print_out, :schema_path, :source_id, :update_date,:update_user,:hash,:description,:file_group,:type)";
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

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSet> getDatasetNotInCda(String codename, int from, int count, String version, String strCdaId) {
        String dataSetTable = CDAVersion.getDataSetTableName(version);
        String relationTable = CDAVersion.getCDADatasetRelationshipTableName(version);
        Session session = currentSession();
        String sql = " select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                " from " + dataSetTable + " where 1=1 ";
        if (codename != null && !codename.equals(0) && !codename.equals("")) {
            sql += " and code LIKE :codename or name LIKE :codename ";
        }
        Query query = session.createSQLQuery(sql);
        if (codename != null && !codename.equals(0) && !codename.equals(""))
            query.setString("codename", "%" + codename + "%");

        if (count > 0) {
            query.setMaxResults(count);
            query.setFirstResult((from - 1) * count);
        }
        return query.list();
    }


    /**
     * 判断文件是否存在
     */
    public boolean isFileExists(String strCdaId, String strVersionCode) {
        int iResult = 0;
        String strFilePath = FilePath(strVersionCode) + strCdaId + ".xml";
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

    /*
    *  根据CDA Id 和 版本编码 生成CDA文档
    * param strCdaId cdaId
    * param strVersionCode 版本编号
    * */
    public int createCDASchemaFile(String strCdaId, String strVersionCode) throws TransformerException, ParserConfigurationException, FileNotFoundException, UnsupportedEncodingException {
        //操作结果：0：现在失败 1：新增成功
        int result = 1;
        int iSetCount = cdaDatasetRelationshipManager.getRelationshipCountByCdaId(strCdaId, strVersionCode, "");
        List<CdaDatasetRelationship> relationshipsList = cdaDatasetRelationshipManager.getRelationshipByCdaId(strCdaId, strVersionCode, "", 1, iSetCount);

        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";
        String splitMark = System.getProperty("file.separator");
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + strVersionCode + splitMark;
        File file = new File(strXMLFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String strFilepath = strXMLFilePath + strCdaId + ".xsd";

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
            DataSet dataSet = relationshipsList.get(i).getDataset();
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
        return result;
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

    /*
    * 根据CDA类别 版本号 查询条件 获取CDA
    * @Parma mapKey strVersionId:版本号;strKey:查询条件;type:类别
    *
    * */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADocument> getDocumentListByType(Map<String, Object> mapKey) {

        String strVersionId = mapKey.get("cda_version").toString();
        String strKey = mapKey.get("search_key").toString();
        String strType = mapKey.get("type").toString();

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description,t.file_group,t.type " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (strKey != null && strKey != "") {
            strSql += " and (t.code like :key or t.`name` like :key)";
        }

        Query query = session.createSQLQuery(strSql);
        query.setString("type", strType);
        if (strKey != null && strKey != "")
            query.setString("key", "%" + strKey + "%");

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
            cdaDocument.setVersionCode(strVersionId);
            xcdaDocuments.add(cdaDocument);
        }

        return xcdaDocuments;
    }

}//end CDADocumentManager