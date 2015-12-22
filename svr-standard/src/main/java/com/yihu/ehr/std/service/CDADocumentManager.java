package com.yihu.ehr.std.service;

import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.std.model.DataSetForInterface;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
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
public class CDADocumentManager extends SQLGeneralDAO {

    @Autowired
    private CDADatasetRelationshipManager xCDADatasetRelationshipManager;

    public CDADocumentManager() {
    }

    /**
     * 同时删除多个时 CDAID用逗号隔开
     *
     * @param strVersionId
     * @param ids
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int deleteCDAInfo(String strVersionId, List<String> ids) {
        int result = 0;
        try {
            String strIds = StringUtils.join(ids, "','");
            strIds = "'" + strIds + "'";

            result = xCDADatasetRelationshipManager.deleteRelationshipByCDAId(strVersionId, ids);
            if (result > -1) {
                Session session = currentSession();
                String sql = "delete from " + CDAVersion.getCDATableName(strVersionId) + " where id in(" + strIds + ")";
                result = session.createSQLQuery(sql).executeUpdate();
            }
        } catch (Exception ex) {
            result = -1;
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
    public CDADocument[] getCDAInfoByVersionAndId(String strVersionId, List<String> ids) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);


        String strIds = StringUtils.join(ids, "','");
        strIds = "'" + strIds + "'";

        Query query = session.createSQLQuery("SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
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

            xcdaDocuments[i] = cdaDocument;
        }

        return xcdaDocuments;
    }

    @Transactional
    public CDADocument getDocument(String versionId, String cdaDocumentId) {
        try {
            Session session = currentSession();
            String strTableName = CDAVersion.getCDATableName(versionId);

            String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                    "FROM " + strTableName + " t where t.id = '" + cdaDocumentId + "'";
            Query query = session.createSQLQuery(strSql);

            return (CDADocument) query.uniqueResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public int getCDACountByVersionAndKey(String strVersionId, String strKey) {
        try {
            Session session = currentSession();
            String strTableName = CDAVersion.getCDATableName(strVersionId);

            String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                    "FROM " + strTableName + " t where 1=1 ";
            if (strKey != null && strKey != "") {
                strSql += " and t.code like ':key' or t.`name` like ':key'";
            }

            Query query = session.createSQLQuery(strSql);
            if (strKey != null && strKey != "")
                query.setString("key", "%" + strKey + "%");

            if (query == null)
                return 0;

            List<Object> records = query.list();

            return records.size();
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 根据查询条件获取CDA信息
     * 参数Key支持 Code/Name
     *
     * @param strVersionId
     * @param strKey
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public CDADocument[] getCDAListByVersionAndKey(String strVersionId, String strKey) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
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
    public CDADocument[] getCDAListByVersionAndKey(String strVersionId, String strKey, String strType, int page, int pageSize) {

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
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

            xcdaDocuments[i] = cdaDocument;
        }

        return xcdaDocuments;
    }


    /*
    * 判断CDA Code是否已经存在
    * */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int getCDAByCDACode(String strVersionId, String strCDACode) {
        int iResult = 0;

        try {
            Session session = currentSession();
            String strTableName = CDAVersion.getCDATableName(strVersionId);

            String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                    "FROM " + strTableName + " t where t.`code`=:cdacode";


            Query query = session.createSQLQuery(strSql);
            query.setString("cdacode", strCDACode);

            iResult = query.list().size();
        } catch (Exception ex) {

        }

        return iResult;
    }

    /**
     * 保存CDA信息
     *
     * @param xCDA
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int saveCDAInfo(CDADocument cdaDocument) {
        int result = 0;
        try {
            Session session = currentSession();

            String strTableName = CDAVersion.getCDATableName(cdaDocument.getVersionCode());
            String sql;
            Query query;

            List<String> ids = new ArrayList<>();
            ids.add(cdaDocument.getId());
            CDADocument[] cdaDocuments = getCDAInfoByVersionAndId(cdaDocument.getVersionCode(), ids);

            if (cdaDocuments.length == 0) {
                sql = "insert into " + strTableName +
                        " ( id, code, create_date, create_user, name, print_out, schema_path, source_id, update_date, update_user,hash,description)  " +
                        "values(:id, :code, :create_date, :create_user, :name, :print_out, :schema_path, :source_id, :update_date,:update_user,:hash,:description)";
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
                        "description=:description" +
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
            query.setString("source_id", cdaDocument.getSource_id());
            query.setDate("update_date", cdaDocument.getUpdateDate());
            query.setString("update_user", cdaDocument.getUpdateUser());
            query.setInteger("hash", cdaDocument.getHashCode());
            query.setString("description", cdaDocument.getDescription());
            result = query.executeUpdate();
        } catch (Exception ex) {
            result = -1;
        }
        return result;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSet> getDatasetNotInCDA(String codename, int from, int count, String version, String strCDAId) {
        String dataSetTable = CDAVersion.getDataSetTableName(version);
        String relationTable = CDAVersion.getCDADatasetRelationshipTableName(version);
        Session session = currentSession();
        String sql = " select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                " from " + dataSetTable + " where 1=1 ";
        //+ " and id not in (select b.dataset_id from " + relationTable + " b where b.cda_id !='" + strCDAId + "')";
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

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DataSetForInterface> searchDataSetList(String codename, int from, int count, CDAVersion innerVersion, String strCDAId) {

        //参数获取处理
        List DataSetList = getDatasetNotInCDA(codename, from, count, innerVersion.getVersion(), strCDAId);
        List<DataSetForInterface> dataSetModels = new ArrayList<>();

        for (int i = 0; i < DataSetList.size(); i++) {
            Object[] record = (Object[]) DataSetList.get(i);
            DataSetForInterface info = new DataSetForInterface();
            info.setId(String.valueOf(record[0]));
            info.setCode((String) record[1]);
            info.setName((String) record[2]);
            dataSetModels.add(info);
        }
        //}

        return dataSetModels;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchDataSetInt(String codename, CDAVersion cdaVersion, String strCDAId) {

        String version = cdaVersion.getVersion();
        String dataSetTable = CDAVersion.getDataSetTableName(version);
        String relationTable = CDAVersion.getCDADatasetRelationshipTableName(version);

        Session session = currentSession();
        String sql = " select count(id) " +
                " from " + dataSetTable + " where 1=1 "
                + " and id not in (select b.dataset_id from " + relationTable + " b where b.cda_id !='" + strCDAId + "')";
        if (codename != null && !codename.equals(0) && !codename.equals("")) {
            sql += " and code LIKE :codename or name LIKE :codename";
        }
        Query query = session.createSQLQuery(sql);
        if (codename != null && !codename.equals(0) && !codename.equals(""))
            query.setString("codename", "%" + codename + "%");

        BigInteger bigInteger = (BigInteger) query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }

    /*
    * 判断文件是否存在*/
    public int FileExists(String strCDAId, String strVersionCode) {
        int iResult = 0;
        String strFilePath = FilePath(strVersionCode) + strCDAId + ".xml";
        File file = new File(strFilePath);
        if (file.exists()) {
            iResult = 1;
        }

        return iResult;
    }

    public String FilePath(String strVersionCode) {
        //文件服务器路径
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";
        //路径分割符
        String splitMark = System.getProperty("file.separator");
        //文件路径
        String strMLFilePath = strPath + splitMark + "xml" + splitMark + strVersionCode + splitMark;

        return strMLFilePath;
    }

    /*
    *  根据CDA Id 和 版本编码 生成CDA文档
    * param strCDAId cdaId
    * param strVersionCode 版本编号
    * */
    public int createCDASchemaFile(String strCDAId, String strVersionCode) {
        //操作结果：0：现在失败 1：新增成功
        int result = 1;
        try {
            //获取关联数据集量
            int iSetCount = xCDADatasetRelationshipManager.getRelationshipCountByCDAId(strCDAId, strVersionCode, "");
            //获取全部已关联的数据集
            CDADatasetRelationship[] relationshipsList = xCDADatasetRelationshipManager.getRelationshipByCDAId(strCDAId, strVersionCode, "", 1, iSetCount);

            //文件服务器路径
            String strPath = System.getProperty("java.io.tmpdir");
            strPath += "StandardFiles";

            //路径分割符
            String splitMark = System.getProperty("file.separator");
            //文件路径
            String strMLFilePath = strPath + splitMark + "xml" + splitMark + strVersionCode + splitMark;
            File file = new File(strMLFilePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            String strFilepath = strMLFilePath + strCDAId + ".xsd";

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            //<xs:schema xmlns:xs="http://www.w3.org/2001/MLSchema">
            Element root = doc.createElement("xs:schema");
            root.setAttribute("xmlns:xs", "http://www.w3.org/2001/MLSchema");
            root.setAttribute("targetNamespace", "http://www.w3schools.com");
            root.setAttribute("xmlns", "http://www.w3schools.com");
            root.setAttribute("elementFormDefault", "qualified");
            doc.appendChild(root);

            for (int i = 0; i < relationshipsList.length; i++) {
                //获取数据集
                DataSet dataSet = relationshipsList[i].getDataset();
                Element rowSet = doc.createElement("xs:table");
                rowSet.setAttribute("code", dataSet.getCode());
                root.appendChild(rowSet);

                //获取数据元
                List<MetaData> xMetaDatas = dataSet.getMetaDataList();

                for (int j = 0; j < xMetaDatas.size(); j++) {
                    Element rowEle = doc.createElement("xs:element");
                    rowEle.setAttribute("code", xMetaDatas.get(j).getCode());

                    String strColumnType = xMetaDatas.get(j).getColumnType().equals("") ? "VARCHAR" : xMetaDatas.get(j).getColumnType();

                    rowEle.setAttribute("type", "xs:" + strColumnType);
                    rowSet.appendChild(rowEle);
                }
            }

            //生成ML文件
            outputml(doc, strFilepath);
        } catch (Exception ex) {
            result = 0;
        }
        return result;
    }

    private void outputml(Document doc, String fileName) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();

            DOMSource source = new DOMSource(doc);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进

            OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }

    /*
    * 根据CDA类别 版本号 查询条件 获取CDA
    * @Parma mapKey strVersionId:版本号;strKey:查询条件;type:类别
    *
    * */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDADocument> getCDAListByType(Map<String, Object> mapKey) {

        String strVersionId = mapKey.get("strVersionId").toString();
        String strKey = mapKey.get("strKey").toString();
        String strType = mapKey.get("type").toString();

        Session session = currentSession();
        String strTableName = CDAVersion.getCDATableName(strVersionId);

        String strSql = "SELECT t.id, t.`code`, t.create_date, t.create_user, t.`name`, t.print_out, t.`schema_path`, t.source_id, t.update_date, t.update_user,t.description " +
                "FROM " + strTableName + " t where 1=1 and t.type=:type";
        if (strKey != null && strKey != "") {
            strSql += " and (t.code like :key or t.`name` like :key)";
        }

        Query query = session.createSQLQuery(strSql);
        query.setString("type", strType);
        if (strKey != null && strKey != "")
            query.setString("key", "%" + strKey + "%");

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

            cdaDocuments.add(cdaDocument);
        }

        return cdaDocuments;
    }
}//end CDADocumentManager