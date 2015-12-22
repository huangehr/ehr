package com.yihu.ehr.std.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.ServiceFactory;
import com.yihu.ehr.std.model.*;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 标准下发信息操作
 *
 * @author AndyCai
 * @version 1.0
 * @created 23-7月-2015 15:51:39
 */
@Transactional
@Service
public class StdDispatchManager extends SQLGeneralDAO {

    @Autowired
    FastDFSUtil fastDFSUtil;

    StdVersionFileManager stdVersionFileManager;
    static DataSetManager dataSetManager;
    static CDAVersionManager cdaVersionManager;
    static DictManager xDictManager;
    static StdVersionFileManager xStdVersionFileManager;
    static StdDiffer xStdDiffer;

    @Autowired
    private CDADocumentManager xcdaDocumentManager;

    @Autowired
    private CDADatasetRelationshipManager xCDADatasetRelationshipManager;

    public StdDispatchManager() {

    }

    public void finalize() throws Throwable {

    }

    public StdSendInfo createStdSendInfo() {
        return new StdSendInfo();
    }

    /**
     * 根据关键字获取下发信息(关键字可为以下内容：机构名称，版本号，创建人，修改人)
     *
     * @param strKey
     */
    public StdSendInfo[] GetStdSendInfos(String strKey) {
        return null;
    }


    /**
     * 根据机构ID获取标准下发详细信息
     *
     * @param strOrgId
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public StdSendInfo GetStdSendInfoByOrgId(String strOrgId) {
        if (strOrgId == null || strOrgId == "") {
            return null;
        }

        Session session = currentSession();
        Query query = session.createSQLQuery("SELECT id,org_id,std_version_id,create_dtime,create_user,update_dtime,update_user FROM std_version_file_info WHERE org_id=:orgId");
        query.setInteger("orgId", Integer.parseInt(strOrgId));
        Object[] record = (Object[]) query.uniqueResult();

        StdSendInfo info = null;
        if (record != null && record.length > 0) {
            info = new StdSendInfo();
            info.setId((long) record[0]);
            info.setOrgId((long) record[1]);
            info.setStdVersionId((String) record[2]);
            info.setCreateDTime((Date) record[3]);
            info.setCreateUser((String) record[4]);
            info.setUpdateDTime((Date) record[5]);
            info.setUpdateUser((String) record[6]);
        }
        return info;
    }

    /**
     * 保存下发信息(ID为0时新增，否则修改)
     *
     * @param info
     */
    public void SaveStdSendInfo(StdSendInfo info) {
        StdSendInfo sendInfo = (StdSendInfo) info;
        Session session = currentSession();

        String strSql;
        Query query;
        if (sendInfo.getId() == 0) {
            strSql = "SELECT max(id) FROM std_version_file_info";
            query = session.createSQLQuery(strSql);
            Object object = query.uniqueResult();

            long id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
            sendInfo.setId(id);

            strSql = "INSERT INTO std_version_file_info (id,org_id,std_version_id,create_dtime,create_user)" +
                    "VALUES(:id,:orgId,:versionId,:createDTime,:createUser)";
        } else {
            strSql = "UPDATE std_version_file_info" +
                    " SET " +
                    " org_id=:orgId," +
                    " std_version_id=:versionId," +
                    " create_dtime=:createDTime," +
                    " create_user=:createUser" +
                    " WHERE id=:id";
        }

        query = session.createSQLQuery(strSql);
        query.setLong("id", sendInfo.getId());
        query.setLong("orgId", sendInfo.getOrgId());
        query.setString("versionId", sendInfo.getStdVersionId());
        query.setDate("createDTime", sendInfo.getCreateDTime());
        query.setString("createUser", sendInfo.getCreateUser());

        query.executeUpdate();
    }

    /**
     * 下发标准  被动调用，生成标准文件并返回文件的byte[]
     * 已生成全版本标准文件，还有差异化版本文件未生成
     *
     * @param strSourceVersionId 将下发的标准版本
     * @return Map 1.filepath 文件路径  2.password 文件夹密码
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public Map SendStandard(String strSourceVersionId) {
        return SendStandard(strSourceVersionId, "");
    }

    /**
     * 下发标准  被动调用，生成标准文件并返回文件的byte[]
     * 已生成全版本标准文件，还有差异化版本文件未生成
     *
     * @param strSourceVersionId 将下发的标准版本
     * @param stdTargetVersionId 当前使用的版本
     * @return Map 1.filepath 文件路径  2.password 文件夹密码
     */

    @Transactional(Transactional.TxType.SUPPORTS)
    public Map SendStandard(String strSourceVersionId, String stdTargetVersionId) {
        Map map = null;
        try {
            if (stdVersionFileManager == null)
                stdVersionFileManager = ServiceFactory.getService(Services.StdVersionFileManager);
            StdVersionFileInfo fileInfo = null;

            fileInfo = stdVersionFileManager.GetVersionFileInfo(strSourceVersionId, stdTargetVersionId);

            if (fileInfo != null) {
                map = new HashMap<>();
                map.put(FastDFSUtil.GroupField, fileInfo.getFilePath());//setFilePath
                map.put(FastDFSUtil.RemoteFileField, fileInfo.getFileName());//setFileName
                map.put("password", fileInfo.getFile_pwd());

            } else {

                /************20151110 CMS 暂不考虑差异化版本更新问题，版本更新均为全版本下发 Start*************/
                            /*以下判断为差异化版本更新判断*/

                // if (stdTargetVersionId == null || stdTargetVersionId == "") {
                //全版本下发
                map = CreateFullVersionFile(strSourceVersionId);
//                } else {
                //差异化版本下发
//                    map = CreateDiffVersionFile(strSourceVersionId, stdTargetVersionId);
//                }

                /************20151110 CMS 暂不考虑差异化版本更新问题，版本更新均为全版本下发 End*************/
            }
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }

        return map;
    }

    public String getFileSystemPath() {
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";

        return strPath;
    }

    /**
     * 创建版本文件，并返回文件路径
     *
     * @param sourceVersionId
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map CreateFullVersionFile(String sourceVersionId) {
        Map resultMap = new HashMap<>();

        //创建标准版本 数据集 数据元 字典 字典值 5个ML文件 ，并生成zip压缩包
        if (cdaVersionManager == null)
            cdaVersionManager = ServiceFactory.getService(Services.CDAVersionManager);
        if (dataSetManager == null)
            dataSetManager = ServiceFactory.getService(Services.DataSetManager);
        if (xDictManager == null)
            xDictManager = ServiceFactory.getService(Services.DictManager);
        if (xStdVersionFileManager == null)
            xStdVersionFileManager = ServiceFactory.getService(Services.StdVersionFileManager);

        String strFilePath = getFileSystemPath();

        String splitMark = System.getProperty("file.separator");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strFileName = format.format(new Date()) + ".zip";
        String strMLFilePath = strFilePath + splitMark + "xml" + splitMark + format.format(new Date()) + splitMark;
        String strZIPFilePath = strFilePath + splitMark + "zip" + splitMark;
        File zipFile = new File(strZIPFilePath);

        File targetFile = new File(strMLFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!zipFile.exists()) {
            zipFile.mkdirs();
        }

        //获取标准版本信息
        List<CDAVersion> listVersion = new ArrayList<CDAVersion>();
        CDAVersion sourceVersion = cdaVersionManager.getVersionById(sourceVersionId);
        listVersion.add(sourceVersion);

        try {
            //创建版本文件
            String strVersionFileName = "std_inner_version.xml";
            createStandardVersionFile(strMLFilePath, strVersionFileName, listVersion);

            //获取数据集信息
            DataSet[] xDataSet = dataSetManager.getDataSetList(0, 0, sourceVersion);
            List<DataSet> listDateSet = Arrays.asList(xDataSet);
            String strDataSetFileName = "std_dataset.xml";

            //创建数据集文件，并获取数据元信息
            List<MetaData> listMateData = createDataSetFile(strMLFilePath, strDataSetFileName, listDateSet);
            //创建数据元文件
            String strMetaDataFileName = "std_metadata.xml";
            createMetaDataFile(strMLFilePath, strMetaDataFileName, listMateData);
            //创建CDA文档
            String strCDAFileName = "std_cda.xml";
            List<CDADocument> listCDA = Arrays.asList(xcdaDocumentManager.getCDAListByVersionAndKey(sourceVersion.getVersion(), ""));
            createCDAFile(strMLFilePath, strCDAFileName, listCDA);

            //创建关系文档
            String strRelationFileName = "std_cda_dataset_relationship.xml";
            List<CDADatasetRelationship> listRelation = Arrays.asList(xCDADatasetRelationshipManager.getRelationshipByVersion(sourceVersion.getVersion()));
            createCDADatasetRelationshipFile(strMLFilePath, strRelationFileName, listRelation);

            //获取字典信息
            List<Dict> listDict = Arrays.asList(xDictManager.getDictList(0, 0, sourceVersion));
            String strDictFileName = "std_dict.xml";

            //创建字典文档，并获取字典值信息
            List<DictEntry> listEntry = createDictFile(strMLFilePath, strDictFileName, listDict);

            //创建字典值文档
            String strEntryFileName = "std_dict_item.xml";
            createDictEntryFile(strMLFilePath, strEntryFileName, listEntry);

            //源文件路径
            String strCDAFilePath = strFilePath + splitMark + "xml" + splitMark + sourceVersion.getVersion() + splitMark;
            File file = new File(strCDAFilePath);
            //判断文件夹是否存在
            if (file.exists()) {
                //目标文件路径
                String strCDAFileZipPath = strMLFilePath + "CDA_File" + splitMark;
                File fileCDAZip = new File(strCDAFileZipPath);
                if (!fileCDAZip.exists()) {
                    fileCDAZip.mkdirs();
                }

                File[] fileCDA = file.listFiles();
                for (int i = 0; i < fileCDA.length; i++) {
                    if (fileCDA[i].isFile()) {
                        //复制文件
                        try {
                            copyFile(fileCDA[i], new File(strCDAFileZipPath + fileCDA[i].getName()));
                        } catch (Exception ex) {

                        }
                    }
                }
            }

            //生成Zip文件
            Zipper zipper = new Zipper();
            File resourcesFile = new File(strMLFilePath);
            String strPwd = UUID.randomUUID().toString();
            zipper.zipFile(resourcesFile, strZIPFilePath + strFileName, strPwd);

            //将文件上传到服务器中
            ObjectNode msg = fastDFSUtil.upload(strZIPFilePath + strFileName, "");

            resultMap.put(FastDFSUtil.GroupField, msg.get(FastDFSUtil.GroupField).asText());//setFilePath
            resultMap.put(FastDFSUtil.RemoteFileField, msg.get(FastDFSUtil.RemoteFileField).asText());//setFileName
            resultMap.put("password", strPwd);

            StdVersionFileInfo fileInfo = new StdVersionFileInfo();
            fileInfo.setFileName(msg.get(FastDFSUtil.RemoteFileField).asText());
            fileInfo.setFilePath(msg.get(FastDFSUtil.GroupField).asText());
            fileInfo.setFile_pwd(strPwd);
            fileInfo.setSourceVersionId(sourceVersionId);
            fileInfo.setTargetVersionId("");

            fileInfo.setCreateDTime(new Date());
            fileInfo.setCreateUser("sys");
            xStdVersionFileManager.SaveVersionFileInfo(fileInfo);

        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }

        return resultMap;
    }

    public void copyFile(File sourcefile, File targetFile) throws IOException {

        //新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourcefile);
        BufferedInputStream inbuff = new BufferedInputStream(input);

        //新建文件输出流并对它进行缓冲
        FileOutputStream out = new FileOutputStream(targetFile);
        BufferedOutputStream outbuff = new BufferedOutputStream(out);

        //缓冲数组
        byte[] b = new byte[1024 * 5];
        int len = 0;
        while ((len = inbuff.read(b)) != -1) {
            outbuff.write(b, 0, len);
        }

        //刷新此缓冲的输出流
        outbuff.flush();

        //关闭流
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }


    /**
     * 创建标准版本ML
     *
     * @param strFilePath
     * @param strFileName
     * @param cdaVersion
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createStandardVersionFile(String strFilePath, String strFileName, List<CDAVersion> cdaVersion) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_inner_version");
            doc.appendChild(root);

            //创建版本文件列信息
            root.appendChild(getVersionColumnElementRoot(doc));

            //创建版本详细信息
            for (int i = 0; i < cdaVersion.size(); i++) {
                CDAVersion version = cdaVersion.get(i);
                Element rowRoot = doc.createElement("row");

                Element rowData_Id = doc.createElement("id");
                rowData_Id.setTextContent("");
                rowRoot.appendChild(rowData_Id);

                Element rowData_code = doc.createElement("code");
                rowData_code.setTextContent(version.getVersion());
                rowRoot.appendChild(rowData_code);

                Element rowData_comm = doc.createElement("comment");
                rowData_comm.setTextContent("");
                rowRoot.appendChild(rowData_comm);

                Element rowData_val = doc.createElement("valid");
                rowData_val.setTextContent("1");
                rowRoot.appendChild(rowData_val);

                Element rowData_type = doc.createElement("type");
                rowData_type.setTextContent("1");
                rowRoot.appendChild(rowData_type);

                Element rowData_vd = doc.createElement("valid_date");
                rowData_vd.setTextContent(String.valueOf(version.getCommitTime()));
                rowRoot.appendChild(rowData_vd);

                Element rowAuthor = doc.createElement("author");
                rowAuthor.setTextContent(version.getAuthor() == "" ? "健康之路" : version.getAuthor());
                rowRoot.appendChild(rowAuthor);

                root.appendChild(rowRoot);
            }
            strFilePath = strFilePath + strFileName;
            outputml(doc, strFilePath);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }

    public Element getVersionColumnElementRoot(Document doc) {
        String[] strColumn = {"id|N", "code|S", "comment|S", "valid|N", "type|N", "valid_date|D", "author|S"};
        Element colRoot = doc.createElement("metadata");

        for (int i = 0; i < strColumn.length; i++) {
            String[] strColumnSplit = strColumn[i].split("\\|");
            Element colElement = doc.createElement("col");
            colElement.setAttribute("name", strColumnSplit[0]);
            colElement.setAttribute("type", strColumnSplit[1]);
            colRoot.appendChild(colElement);
        }

        return colRoot;
    }

    /**
     * 创建数据集文件
     *
     * @param strFilePath
     * @param strFileName
     * @param dataSet
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MetaData> createDataSetFile(String strFilePath, String strFileName, List<DataSet> dataSet) {
        //创建数据集ML 返回数据集IDList
        List<MetaData> listMetaDta = new ArrayList<MetaData>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_dataset");
            doc.appendChild(root);

            //创建数据集文件列信息
            Element colRoot = doc.createElement("metadata");

            String[] strColumn = {"summary|S", "valid|N", "inner_version|S", "ref_standard|S", "code|S", "catalog|S", "name|S", "publisher|S", "key_word|S", "id|N", "lang|S", "version|S"};
            root.appendChild(getColumnElement(doc, colRoot, strColumn));

            //创建数据集详细信息
            for (int i = 0; i < dataSet.size(); i++) {
                DataSet xDataSet = dataSet.get(i);
                Element rowRoot = doc.createElement("row");
                switch (xDataSet.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        System.out.println("#### " + (xDataSet == null));
                        listMetaDta.addAll(xDataSet.getMetaDataList());
                        break;
                }

                Element rowData_sum = doc.createElement("summary");
                rowData_sum.setTextContent("");
                rowRoot.appendChild(rowData_sum);

                Element rowData_val = doc.createElement("valid");
                rowData_val.setTextContent("1");
                rowRoot.appendChild(rowData_val);

                Element rowData_inner = doc.createElement("inner_version");
                rowData_inner.setTextContent(xDataSet.getStdVersion());
                rowRoot.appendChild(rowData_inner);

                Element rowData_ref = doc.createElement("ref_standard");
                rowData_ref.setTextContent(String.valueOf(xDataSet.getReference()));
                rowRoot.appendChild(rowData_ref);

                Element rowData_code = doc.createElement("code");
                rowData_code.setTextContent(xDataSet.getCode());
                rowRoot.appendChild(rowData_code);

                Element rowData_cat = doc.createElement("catalog");
                rowData_cat.setTextContent(String.valueOf(xDataSet.getCatalog()));
                rowRoot.appendChild(rowData_cat);

                Element rowData_name = doc.createElement("name");
                rowData_name.setTextContent(xDataSet.getName());
                rowRoot.appendChild(rowData_name);

                Element rowData_key = doc.createElement("key_word");
                rowData_key.setTextContent("");
                rowRoot.appendChild(rowData_key);

                Element rowData_id = doc.createElement("id");
                rowData_id.setTextContent(String.valueOf(xDataSet.getId()));
                rowRoot.appendChild(rowData_id);

                Element rowData_lang = doc.createElement("lang");
                rowData_lang.setTextContent(String.valueOf(xDataSet.getLang()));
                rowRoot.appendChild(rowData_lang);

                Element rowData_ver = doc.createElement("version");
                rowData_ver.setTextContent("");
                rowRoot.appendChild(rowData_ver);

                root.appendChild(rowRoot);
            }

            //输出文件
            outputml(doc, strFilePath + strFileName);
        } catch (Exception e) {
            LogService.getLogger(StdDispatchManager.class).error(e.getMessage());
        }

        return listMetaDta;
    }

    /**
     * 创建数据元文件
     *
     * @param strFilePath
     * @param strFileName
     * @param metaDataList
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createMetaDataFile(String strFilePath, String strFileName, List<MetaData> metaDataList) {
        //创建数据元ML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_metadata");
            doc.appendChild(root);

            //创建数据元文件列信息
            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"inner_version|S", "code|S", "nullable|N", "dataset_id|N", "format|S", "column_name|S", "type|S",
                    "dict_code|S", "version|S", "primary_key|N", "valid|N", "name|S", "de_code|S", "definition|S", "id|N", "column_type|S", "dict_value_type|N"};
            root.appendChild(getColumnElement(doc, colRoot, strColumn));

            //创建数据元明细信息
            for (int i = 0; i < metaDataList.size(); i++) {
                MetaData metaData = metaDataList.get(i);

                Element rowRoot = doc.createElement("row");
                switch (metaData.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        break;
                }

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent(metaData.getDataSet().getInnerVersion().getVersion());
                rowRoot.appendChild(row_inner);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(metaData.getCode());
                rowRoot.appendChild(row_code);

                Element row_isnull = doc.createElement("nullable");
                row_isnull.setTextContent(metaData.isNullable() ? "1" : "0");
                rowRoot.appendChild(row_isnull);

                Element row_dataset = doc.createElement("dataset_id");
                row_dataset.setTextContent(String.valueOf(metaData.getDataSetId()));
                rowRoot.appendChild(row_dataset);

                Element row_format = doc.createElement("format");
                row_format.setTextContent(metaData.getFormat());
                rowRoot.appendChild(row_format);

                Element row_col = doc.createElement("column_name");
                row_col.setTextContent(metaData.getColumnName());
                rowRoot.appendChild(row_col);

                Element row_type = doc.createElement("type");
                row_type.setTextContent(metaData.getType());
                rowRoot.appendChild(row_type);

                Element row_dict = doc.createElement("dict_code");
                row_dict.setTextContent(String.valueOf(metaData.getDict_code()));
                rowRoot.appendChild(row_dict);

                Element row_ver = doc.createElement("version");
                row_ver.setTextContent("");
                rowRoot.appendChild(row_ver);

                Element row_key = doc.createElement("primary_key");
                row_key.setTextContent(metaData.isPrimaryKey() ? "1" : "0");
                rowRoot.appendChild(row_key);

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_name = doc.createElement("name");
                row_name.setTextContent(metaData.getName());
                rowRoot.appendChild(row_name);

                Element row_de_code = doc.createElement("de_code");
                row_de_code.setTextContent(metaData.getCode());
                rowRoot.appendChild(row_de_code);

                Element row_def = doc.createElement("definition");
                row_def.setTextContent(metaData.getDefinition());
                rowRoot.appendChild(row_def);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(metaData.getId()));
                rowRoot.appendChild(row_id);

                Element row_colType = doc.createElement("column_type");
                if (metaData.getColumnLength().length() == 0) {
                    row_colType.setTextContent(metaData.getColumnType());
                } else {
                    row_colType.setTextContent(metaData.getColumnType() + "(" + metaData.getColumnLength() + ")");
                }
                rowRoot.appendChild(row_colType);

                Element row_dictValueType = doc.createElement("dict_value_type");
                row_dictValueType.setTextContent("");
                rowRoot.appendChild(row_dictValueType);

                root.appendChild(rowRoot);
            }

            outputml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }

    /**
     * 创建字典文件
     *
     * @param strFilePath
     * @param strFileName
     * @param dict
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<DictEntry> createDictFile(String strFilePath, String strFileName, List<Dict> dict) {
        //创建字典ML，返回字典值 List
        List<DictEntry> listEntry = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_dict");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"valid|N", "inner_version|S", "ref_standard|S", "code|S", "name|S", "publisher|S", "version|S", "id|N"};
            //创建数据元文件列信息
            root.appendChild(getColumnElement(doc, colRoot, strColumn));

            for (int i = 0; i < dict.size(); i++) {
                Dict xDict = dict.get(i);

                Element rowRoot = doc.createElement("row");
                switch (xDict.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        listEntry.addAll(Arrays.asList(xDict.getDictEntries()));
                        break;
                }

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent(xDict.getInnerVersionId());
                rowRoot.appendChild(row_inner);

                Element row_ref = doc.createElement("ref_standard");
                row_ref.setTextContent(xDict.getStdVersion());
                rowRoot.appendChild(row_ref);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(xDict.getCode());
                rowRoot.appendChild(row_code);

                Element row_name = doc.createElement("name");
                row_name.setTextContent(xDict.getName());
                rowRoot.appendChild(row_name);

                Element row_pub = doc.createElement("publisher");
                row_pub.setTextContent(xDict.getAuthor());
                rowRoot.appendChild(row_pub);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(xDict.getId()));
                rowRoot.appendChild(row_id);

                Element row_version = doc.createElement("version");
                row_version.setTextContent("");
                rowRoot.appendChild(row_version);

                root.appendChild(rowRoot);

            }
            outputml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
        return listEntry;
    }

    /**
     * 创建字典值ML文件
     *
     * @param strFilePath
     * @param strFileName
     * @param dictEntry
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createDictEntryFile(String strFilePath, String strFileName, List<DictEntry> dictEntry) {
        //创建字典值ML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_dict_item");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"valid|S", "inner_version|S", "dict_id|N", "code|S", "value|S", "version|S", "id|N"};

            //创建数据元文件列信息
            root.appendChild(getColumnElement(doc, colRoot, strColumn));
            for (int i = 0; i < dictEntry.size(); i++) {
                DictEntry xDictEntry = dictEntry.get(i);
                Element rowRoot = doc.createElement("row");
                switch (xDictEntry.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        break;
                }

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent(xDictEntry.getStdVersion());
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("dict_id");
                row_dict.setTextContent(String.valueOf(xDictEntry.getDictId()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(xDictEntry.getCode());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("value");
                row_value.setTextContent(xDictEntry.getValue());
                rowRoot.appendChild(row_value);

                Element row_version = doc.createElement("version");
                row_version.setTextContent("");
                rowRoot.appendChild(row_version);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(xDictEntry.getId()));
                rowRoot.appendChild(row_id);

                root.appendChild(rowRoot);
            }
            outputml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }

    /**
     * 创建文档的表结构
     *
     * @param doc
     * @param colRoot
     * @param strColumn
     * @return
     */
    public Element getColumnElement(Document doc, Element colRoot, String[] strColumn) {
        for (int i = 0; i < strColumn.length; i++) {
            String[] strClomunSplit = strColumn[i].split("\\|");
            Element colElement = doc.createElement("col");
            colElement.setAttribute("name", strClomunSplit[0]);
            colElement.setAttribute("type", strClomunSplit[1]);

            colRoot.appendChild(colElement);
        }

        return colRoot;
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

    /**
     * 创建差异化版本文件，并返回文件路径
     * <p>
     * Sand 去除标准来源在版本差异中的体现，标准来源不需要版本化。
     *
     * @param sourceVersionId
     * @author Sand
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map CreateDiffVersionFile(String sourceVersionId, String targetVersionId) {
        Map resultMap = new HashMap<>();

        if (cdaVersionManager == null)
            cdaVersionManager = ServiceFactory.getService(Services.CDAVersionManager);
        if (xStdDiffer == null)
            xStdDiffer = ServiceFactory.getService(Services.StdDiffer);
        if (xStdVersionFileManager == null)
            xStdVersionFileManager = ServiceFactory.getService(Services.StdVersionFileManager);

        //获取标准版本信息
        List<CDAVersion> listVersion = new ArrayList<CDAVersion>();
        CDAVersion sourceVersion = cdaVersionManager.getVersionById(sourceVersionId);
        listVersion.add(sourceVersion);

        CDAVersion targetVersion = cdaVersionManager.getVersionById(targetVersionId);

        String strFilePath = getFileSystemPath();

        String splitMark = System.getProperty("file.separator");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strFileName = format.format(new Date()) + ".zip";
        String strMLFilePath = strFilePath + splitMark + "xml" + splitMark + format.format(new Date()) + splitMark;

        File targetFile = new File(strMLFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //创建版本文件
        String strVersionFileName = "std_inner_version.xml";
        createStandardVersionFile(strMLFilePath, strVersionFileName, listVersion);

        //创建数据集文件
        String strDataSetFileName = "std_dataset.xml";
        String strSourceDSTableName = sourceVersion.getDataSetTableName();
        String strTargetDSTableName = targetVersion.getDataSetTableName();
        List<DataSet> dataSets = new ArrayList<>();

        //获取新增数据集
        List<DataSet> listNewDataSet = xStdDiffer.getNewOrRemoveDataSetList(strSourceDSTableName, strTargetDSTableName, "1");
        dataSets.addAll(listNewDataSet);

        //获取删除数据集
        List<DataSet> listRemoveDataSet = xStdDiffer.getNewOrRemoveDataSetList(strTargetDSTableName, strSourceDSTableName, "3");
        dataSets.addAll(listRemoveDataSet);

        //获取修改数据集
        List<DataSet> listUpdateDataSet = xStdDiffer.getUpdateDataSetList(strSourceDSTableName, strTargetDSTableName);
        dataSets.addAll(listUpdateDataSet);
        createDataSetFile(strMLFilePath, strDataSetFileName, dataSets);

        //创建数据元文件
        String strMetaDataFileName = "std_metadata.xml";
        String strSourceMDTName = sourceVersion.getMetaDataTableName();
        String strSourceDictTable = sourceVersion.getDictTableName();
        String strTargetMDTName = targetVersion.getMetaDataTableName();
        String strTargetDictTable = targetVersion.getDictTableName();
        List<MetaData> metaDatas = new ArrayList<>();
        List<MetaData> listNewMetaData = xStdDiffer.getNewOrRemoveMetaDataList(strSourceMDTName, strSourceDictTable, strTargetMDTName, "1");
        metaDatas.addAll(listNewMetaData);

        List<MetaData> listRemoveMetaData = xStdDiffer.getNewOrRemoveMetaDataList(strTargetMDTName, strTargetDictTable, strSourceMDTName, "3");
        metaDatas.addAll(listRemoveMetaData);

        List<MetaData> listUpdateMetaData = xStdDiffer.getUpdateMetaDataList(strSourceMDTName, strSourceDictTable, strTargetMDTName);
        metaDatas.addAll(listUpdateMetaData);

        createMetaDataFile(strMLFilePath, strMetaDataFileName, metaDatas);

        //创建字典文档
        String strDictFileName = "std_dict.xml";
        String strSourceDTName = sourceVersion.getDictTableName();
        String strTargetDTName = targetVersion.getDictTableName();
        List<Dict> dicts = new ArrayList<>();
        List<Dict> listNewDict = xStdDiffer.getNewOrRemoveDictList(strSourceDTName, strTargetDTName, "1");
        dicts.addAll(listNewDict);

        List<Dict> listRemoveDict = xStdDiffer.getNewOrRemoveDictList(strTargetDTName, strSourceDTName, "3");
        dicts.addAll(listRemoveDict);

        List<Dict> listUpdateDict = xStdDiffer.getUpdateDictList(strSourceDTName, strTargetDTName);
        dicts.addAll(listUpdateDict);

        createDictFile(strMLFilePath, strDictFileName, dicts);

        //创建字典值文档
        String strEntryFileName = "std_dict_item.xml";
        String strSourceDEName = sourceVersion.getDictEntryTableName();

        String strTargetDEName = targetVersion.getDictEntryTableName();
        List<DictEntry> dictEntries = new ArrayList<>();

        List<DictEntry> listNewDictEntry = xStdDiffer.getNewOrRemoveDictEntryList(strSourceDEName, strSourceDictTable, strTargetDEName, "1");
        dictEntries.addAll(listNewDictEntry);
        List<DictEntry> listRemoveDictEntry = xStdDiffer.getNewOrRemoveDictEntryList(strTargetDEName, strSourceDictTable, strSourceDEName, "3");
        dictEntries.addAll(listRemoveDictEntry);
        List<DictEntry> listUpdateDictEntry = xStdDiffer.getUpdateDictEntryList(strSourceDEName, strSourceDictTable, strTargetDEName);
        dictEntries.addAll(listUpdateDictEntry);
        createDictEntryFile(strMLFilePath, strEntryFileName, dictEntries);

        // 创建CDA文件
        String strCDAFileName = "std_cda.xm";
        String strSourceCDAName = sourceVersion.getCDADocumentTableName();
        String strTargetCDAName = targetVersion.getCDADocumentTableName();
        List<CDADocument> listCDA = new ArrayList<>();

        List<CDADocument> listNewCDA = xStdDiffer.getNewOrRemoveCDAList(strSourceCDAName, strTargetCDAName, "1");
        listCDA.addAll(listNewCDA);
        List<CDADocument> listRemoveCDA = xStdDiffer.getNewOrRemoveCDAList(strTargetCDAName, strSourceCDAName, "3");
        listCDA.addAll(listRemoveCDA);
        List<CDADocument> listUpdateCDA = xStdDiffer.getUpdateCDAList(strSourceCDAName, strTargetCDAName);
        listCDA.addAll(listUpdateCDA);
        createCDAFile(strMLFilePath, strCDAFileName, listCDA);

        // 创建关联关系文件
        String strRelationFileName = "std_cda_dataset_relationship.xml";
        String strSourceRelationName = sourceVersion.getCDADatasetRelationshipTableName();
        String strTargetRelationName = targetVersion.getCDADatasetRelationshipTableName();
        List<CDADatasetRelationship> listRelation = new ArrayList<>();

        List<CDADatasetRelationship> listNewRelation = xStdDiffer.getNewOrRemoveRelationshipList(strSourceRelationName, strTargetRelationName, "1");
        listRelation.addAll(listNewRelation);
        List<CDADatasetRelationship> listRemoveRelation = xStdDiffer.getNewOrRemoveRelationshipList(strTargetRelationName, strSourceRelationName, "3");
        listRelation.addAll(listRemoveRelation);

        createCDADatasetRelationshipFile(strMLFilePath, strRelationFileName, listRelation);

        String strPwd = UUID.randomUUID().toString();
        try {

            Zipper zipper = new Zipper();
            File resourcesFile = new File(strMLFilePath);

            zipper.zipFile(resourcesFile, strFilePath + splitMark + "zip" + splitMark + strFileName, strPwd);
            resultMap.put("filepath", strFilePath + splitMark + "zip" + splitMark + strFileName);
            resultMap.put("password", strPwd);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
            return null;
        }

        StdVersionFileInfo fileInfo = new StdVersionFileInfo();
        fileInfo.setFileName(strFileName);
        fileInfo.setFilePath(splitMark + "zip" + splitMark);
        fileInfo.setFile_pwd(strPwd);
        fileInfo.setSourceVersionId(sourceVersionId);
        fileInfo.setTargetVersionId(targetVersionId);

        fileInfo.setCreateDTime(new Date());
        fileInfo.setCreateUser("sys");
        xStdVersionFileManager.SaveVersionFileInfo(fileInfo);

        return resultMap;
    }

    /**
     * 创建CDA文档
     *
     * @param strFilePath
     * @param strFileName
     * @param listCDA
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createCDAFile(String strFilePath, String strFileName, List<CDADocument> listCDA) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_cda");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");

            String[] strColumn = {"id|S", "name|S", "code|S", "source_id|S",
                    "print_out|S", "schema_path|S", "description|S",
                    "version|S", "inner_version|S"};

            //创建数据元文件列信息
            root.appendChild(getColumnElement(doc, colRoot, strColumn));
            for (int i = 0; i < listCDA.size(); i++) {
                CDADocument info = listCDA.get(i);
                Element rowRoot = doc.createElement("row");
                switch (info.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        break;
                }

                Element row_id = doc.createElement("id");
                row_id.setTextContent(info.getId());
                rowRoot.appendChild(row_id);

                Element row_name = doc.createElement("name");
                row_name.setTextContent(info.getName());
                rowRoot.appendChild(row_name);

                Element row_codet = doc.createElement("code");
                row_codet.setTextContent(info.getCode());
                rowRoot.appendChild(row_codet);

                Element row_source_id = doc.createElement("source_id");
                row_source_id.setTextContent(info.getSource_id());
                rowRoot.appendChild(row_source_id);

                Element row_print_out = doc.createElement("print_out");
                row_print_out.setTextContent(info.getPrintOut());
                rowRoot.appendChild(row_print_out);

                Element row_schema_path = doc.createElement("schema_path");
                row_schema_path.setTextContent(info.getSchema());
                rowRoot.appendChild(row_schema_path);

                Element row_description = doc.createElement("description");
                row_description.setTextContent(info.getDescription());
                rowRoot.appendChild(row_description);

                Element row_vresion = doc.createElement("version");
                row_vresion.setTextContent("");
                rowRoot.appendChild(row_vresion);

                Element row_innerversion = doc.createElement("inner_version");
                row_innerversion.setTextContent(info.getVersionCode());
                rowRoot.appendChild(row_innerversion);

                root.appendChild(rowRoot);
            }
            outputml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }

    /**
     * 创建CDA文档
     *
     * @param strFilePath
     * @param strFileName
     * @param listRelastion
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public void createCDADatasetRelationshipFile(String strFilePath, String strFileName, List<CDADatasetRelationship> listRelastion) {
        //创建字典值ML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "std_cda_dataset_relationship");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"id|S", "cda_id|S", "dataset_id|N"};

            root.appendChild(getColumnElement(doc, colRoot, strColumn));
            for (int i = 0; i < listRelastion.size(); i++) {
                CDADatasetRelationship info = listRelastion.get(i);
                Element rowRoot = doc.createElement("row");
                switch (info.getOperationType()) {
                    case "1":
                        rowRoot.setAttribute("type", "add");
                        break;
                    case "2":
                        rowRoot.setAttribute("type", "update");
                        break;
                    case "3":
                        rowRoot.setAttribute("type", "delete");
                        break;
                    default:
                        rowRoot.setAttribute("type", "add");
                        break;
                }

                Element row_id = doc.createElement("id");
                row_id.setTextContent(info.getId());
                rowRoot.appendChild(row_id);

                Element row_cda_id = doc.createElement("cda_id");
                row_cda_id.setTextContent(info.getCDAId());
                rowRoot.appendChild(row_cda_id);

                Element row_dataset_id = doc.createElement("dataset_id");
                row_dataset_id.setTextContent(info.getDataset_id());
                rowRoot.appendChild(row_dataset_id);

                root.appendChild(rowRoot);
            }
            outputml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(StdDispatchManager.class).error(ex.getMessage());
        }
    }
}