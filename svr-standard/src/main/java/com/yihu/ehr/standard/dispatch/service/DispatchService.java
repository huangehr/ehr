package com.yihu.ehr.standard.dispatch.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.datasets.service.BaseDataSet;
import com.yihu.ehr.standard.datasets.service.BaseMetaData;
import com.yihu.ehr.standard.dict.service.BaseDict;
import com.yihu.ehr.standard.dict.service.BaseDictEntry;
import com.yihu.ehr.standard.document.service.CDADocument;
import com.yihu.ehr.standard.document.service.CDADocumentService;
import com.yihu.ehr.standard.document.service.CDADataSetRelationship;
import com.yihu.ehr.standard.document.service.CDADataSetRelationshipManager;
import com.yihu.ehr.standard.version.service.CDAVersion;
import com.yihu.ehr.standard.version.service.CDAVersionService;
import com.yihu.ehr.standard.datasets.service.DataSetService;
import com.yihu.ehr.standard.datasets.service.MetaDataService;
import com.yihu.ehr.standard.dict.service.DictEntryService;
import com.yihu.ehr.standard.dict.service.DictService;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
public class DispatchService {

    private static final String[] DICT_ENTRY_TITLE = new String []{"valid", "inner_version", "dict_id", "code", "value", "version", "id"};
    @Autowired
    VersionFileService versionFileService;
    @Autowired
    DataSetService dataSetService;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    CDAVersionService cdaVersionService;
    @Autowired
    DictService dictService;
    @Autowired
    DictEntryService dictEntryService;
    @Autowired
    CDADocumentService cdaDocumentService;
    @Autowired
    CDADataSetRelationshipManager cdaDataSetRelationshipManager;

    private Class getServiceEntity(String version){
        return cdaDocumentService.getServiceEntity(version);
    }

    @Autowired
    FastDFSUtil fastDFSUtil;
    public static  Map<String, String> typeMapping = new HashMap<>();

    static {
        typeMapping.put("1", "add");
        typeMapping.put("2", "update");
        typeMapping.put("3", "delete");
    }


    /**
     * 下发标准  被动调用，生成标准文件并返回文件的byte[]
     * 已生成全版本标准文件，还有差异化版本文件未生成
     *
     * @param strSourceVersionId 将下发的标准版本
     * @return Map 1.filepath 文件路径  2.password 文件夹密码
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map sendStandard(String strSourceVersionId) throws Exception {
        return sendStandard(strSourceVersionId, "");
    }


    /**
     * 下发标准  被动调用，生成标准文件并返回文件的byte[]
     * 已生成全版本标准文件，还有差异化版本文件未生成
     *
     * @param strSourceVersionId 将下发的标准版本
     * @param stdTargetVersionId 当前使用的版本
     * @return Map 1.filepath 文件路径  2.password 文件夹密码
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map sendStandard(String strSourceVersionId, String stdTargetVersionId)
        throws Exception{
        Map map = null;
        VersionFileInfo fileInfo = null;
        fileInfo = versionFileService.getVersionFileInfo(strSourceVersionId, stdTargetVersionId);

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
            map = createFullVersionFile(strSourceVersionId);
//                } else {
            //差异化版本下发
//                    map = CreateDiffVersionFile(strSourceVersionId, stdTargetVersionId);
//                }

            /************20151110 CMS 暂不考虑差异化版本更新问题，版本更新均为全版本下发 End*************/
        }
        return map;
    }


    public String getFileSystemPath() {
        String strPath = System.getProperty("java.io.tmpdir");
        return strPath;
    }


    private Document createDocument() throws ParserConfigurationException {

        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }


    private Element appendChild(Element parent, Element child, String content){
        child.setTextContent(content);
        parent.appendChild(child);
        return parent;
    }


    public Element getVersionColumnElementRoot(Document doc) {
        String[] strColumn = {"id|S", "code|S", "comment|S", "valid|N", "type|N", "valid_date|D","author|S"};
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
     * 创建文档的表结构
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


    private void outputXml(Document doc, String fileName) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进

        OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
    }

    /**
     * 创建标准版本XML
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createStandardVersionFile(String strFilePath, String strFileName, List<CDAVersion> cdaVersion)
            throws Exception {
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_inner_version");
        doc.appendChild(root);

        //创建版本文件列信息
        root.appendChild(getVersionColumnElementRoot(doc));

        //创建版本详细信息
        for (CDAVersion version : cdaVersion) {
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), version.getVersion());
            appendChild(rowRoot, doc.createElement("code"), version.getVersion());
            appendChild(rowRoot, doc.createElement("comment"), "");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("type"), "1");
            appendChild(rowRoot, doc.createElement("valid_date"), String.valueOf(version.getCommitTime()));
            appendChild(rowRoot, doc.createElement("author"), version.getAuthor()==""?"健康之路":version.getAuthor());
            appendChild(rowRoot, doc.createElement("id"), version.getVersion());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    /**
     * 创建数据集文件
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<BaseMetaData> createDataSetFile(String strFilePath, String strFileName, List<BaseDataSet> dataSet, String version)
            throws Exception {
        //创建数据集XML 返回数据集IDList
        List<BaseMetaData> listMetaDta = new ArrayList<>();
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dataset");
        doc.appendChild(root);

        //创建数据集文件列信息
        Element colRoot = doc.createElement("metadata");

        String[] strColumn = {"summary|S", "valid|N", "inner_version|S", "ref_standard|S", "code|S", "catalog|S", "name|S", "publisher|S", "key_word|S", "id|N", "lang|S", "version|S"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //创建数据集详细信息
        for (BaseDataSet xDataSet : dataSet) {
            Element rowRoot = doc.createElement("row");
            appendChild(rowRoot, doc.createElement("summary"), "");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("inner_version"), xDataSet.getStdVersion());
            appendChild(rowRoot, doc.createElement("ref_standard"), String.valueOf(xDataSet.getReference()));
            appendChild(rowRoot, doc.createElement("code"), xDataSet.getCode());
            appendChild(rowRoot, doc.createElement("catalog"), String.valueOf(xDataSet.getCatalog()));
            appendChild(rowRoot, doc.createElement("name"), xDataSet.getName());
            appendChild(rowRoot, doc.createElement("key_word"), "");
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(xDataSet.getId()));
            appendChild(rowRoot, doc.createElement("lang"), String.valueOf(xDataSet.getLang()));
            appendChild(rowRoot, doc.createElement("version"), "");

            root.appendChild(rowRoot);
        }

        //输出文件
        outputXml(doc, strFilePath + strFileName);
        return listMetaDta;
    }


    /**
     * 创建数据元文件
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createMetaDataFile(String strFilePath, String strFileName, String version, List<BaseMetaData> metaDataList)
            throws Exception{
        //创建数据元XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_metadata");
        doc.appendChild(root);

        //创建数据元文件列信息
        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"inner_version|S", "code|S", "nullable|N", "dataset_id|N", "format|S", "column_name|S", "type|S",
                "dict_code|S", "version|S", "primary_key|N", "valid|N", "name|S", "de_code|S", "definition|S", "id|N", "column_type|S", "dict_value_type|N"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //创建数据元明细信息
        for (BaseMetaData metaData : metaDataList) {
            Element rowRoot = doc.createElement("row");

            appendChild(rowRoot, doc.createElement("inner_version"), version);
            appendChild(rowRoot, doc.createElement("code"), metaData.getInnerCode());
            appendChild(rowRoot, doc.createElement("nullable"), metaData.isNullable() ? "1" : "0");
            appendChild(rowRoot, doc.createElement("dataset_id"), String.valueOf(metaData.getDataSetId()));
            appendChild(rowRoot, doc.createElement("format"), metaData.getFormat());
            appendChild(rowRoot, doc.createElement("column_name"), metaData.getColumnName());
            appendChild(rowRoot, doc.createElement("type"), metaData.getType());
            appendChild(rowRoot, doc.createElement("dict_code"), String.valueOf(metaData.getDictCode()));
            appendChild(rowRoot, doc.createElement("version"), version);
            appendChild(rowRoot, doc.createElement("primary_key"), metaData.isPrimaryKey() ? "1" : "0");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("name"), metaData.getName());
            appendChild(rowRoot, doc.createElement("de_code"), metaData.getCode());
            appendChild(rowRoot, doc.createElement("definition"), metaData.getDefinition());
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(metaData.getId()));
            appendChild(rowRoot, doc.createElement("de_code"), metaData.getCode());
            appendChild(rowRoot, doc.createElement("column_type"),
                    metaData.getColumnLength().length() == 0
                            ? metaData.getColumnType()
                            : metaData.getColumnType() + "(" + metaData.getColumnLength() + ")");
            appendChild(rowRoot, doc.createElement("dict_value_type"), "");

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    /**
     * 创建字典文件
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<BaseDictEntry> createDictFile(String strFilePath, String strFileName, List<BaseDict> dicts, String sourceVersionId)
        throws Exception{
        //创建字典XML，返回字典值 List
        List<BaseDictEntry> listEntry = new ArrayList<>();
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dict");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|N", "inner_version|S", "ref_standard|S", "code|S", "name|S", "publisher|S", "version|S", "id|N"};
        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        for (BaseDict xDict : dicts) {
            Element rowRoot = doc.createElement("row");

            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("inner_version"), xDict.getInnerVersion());
            appendChild(rowRoot, doc.createElement("ref_standard"), xDict.getStdVersion());
            appendChild(rowRoot, doc.createElement("code"), xDict.getCode());
            appendChild(rowRoot, doc.createElement("name"), xDict.getName());
            appendChild(rowRoot, doc.createElement("publisher"), xDict.getAuthor());
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(xDict.getId()));
            appendChild(rowRoot, doc.createElement("version"), "");

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
        return listEntry;
    }


    private Element appendChilds(Document doc, Element parent, String[] childs, String[] contents){
        for (int i=0; i<childs.length; i++){
            appendChild(parent, doc.createElement(childs[i]), contents[i]);
        }
        return parent;
    }

    /**
     * 创建字典值XML文件
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createDictEntryFile(String strFilePath, String strFileName, String version, List<BaseDictEntry> dictEntry)
        throws Exception{
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dict_item");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|S", "inner_version|S", "dict_id|N", "code|S", "value|S", "version|S", "id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (BaseDictEntry xDictEntry : dictEntry) {
            Element rowRoot = doc.createElement("row");
            String type = typeMapping.get(xDictEntry.getOperationType());
            rowRoot.setAttribute("type", type==null ? "add" : type);

            appendChilds(
                    doc,
                    rowRoot,
                    DICT_ENTRY_TITLE,
                    new String[]{"1", version, String.valueOf(xDictEntry.getDictId()), xDictEntry.getCode(),
                            xDictEntry.getValue(), "", String.valueOf(xDictEntry.getId())});

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    public void copyFile(File sourcefile,File targetFile) throws IOException {

        //新建文件输入流并对它进行缓冲
        FileInputStream input=new FileInputStream(sourcefile);
        BufferedInputStream inbuff=new BufferedInputStream(input);

        //新建文件输出流并对它进行缓冲
        FileOutputStream out=new FileOutputStream(targetFile);
        BufferedOutputStream outbuff=new BufferedOutputStream(out);

        //缓冲数组
        byte[] b=new byte[1024*5];
        int len=0;
        while((len=inbuff.read(b))!=-1){
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
     * 创建版本文件，并返回文件路径
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map createFullVersionFile(String sourceVersionId)
        throws Exception{
        Map resultMap = new HashMap<>();

        //创建标准版本 数据集 数据元 字典 字典值 5个XML文件 ，并生成zip压缩包
        String strFilePath = getFileSystemPath();

        String splitMark=System.getProperty("file.separator");
        strFilePath += splitMark+"StandardFiles";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strFileName = format.format(new Date()) + ".zip";
        String strXMLFilePath = strFilePath + splitMark+"xml" +splitMark+ format.format(new Date()) + splitMark;
        String strZIPFilePath = strFilePath + splitMark+"zip"+splitMark;
        File zipFile = new File(strZIPFilePath);

        File targetFile = new File(strXMLFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!zipFile.exists()) {
            zipFile.mkdirs();
        }

        //获取标准版本信息
        List<CDAVersion> listVersion = new ArrayList<CDAVersion>();
        CDAVersion sourceVersion = cdaVersionService.retrieve(sourceVersionId);
        listVersion.add(sourceVersion);

        //创建文档
        createFiles(strXMLFilePath, listVersion, sourceVersionId);

        //源文件路径
        String strCDAFilePath =  strFilePath + splitMark+"xml" +splitMark+sourceVersion.getVersion()+splitMark;
        File file = new File(strCDAFilePath);
        //判断文件夹是否存在
        if (file.exists()) {
            //目标文件路径
            String strCDAFileZipPath = strXMLFilePath+"CDA_File"+splitMark;
            File fileCDAZip = new File(strCDAFileZipPath);
            if(!fileCDAZip.exists()) {
                fileCDAZip.mkdirs();
            }

            File[] fileCDA=file.listFiles();
            for (int i = 0; i < fileCDA.length; i++) {
                if(fileCDA[i].isFile()){
                    //复制文件
                    copyFile(fileCDA[i],new File(strCDAFileZipPath+fileCDA[i].getName()));
                }
            }
        }

        //生成Zip文件
        Zipper zipper = new Zipper();
        File resourcesFile = new File(strXMLFilePath);
        String strPwd = UUID.randomUUID().toString();
        zipper.zipFile(resourcesFile, strZIPFilePath + strFileName, strPwd);

        //将文件上传到服务器中
        ObjectNode msg = fastDFSUtil.upload(strZIPFilePath + strFileName, "");

        resultMap.put(FastDFSUtil.GroupField, msg.get(FastDFSUtil.GroupField).asText());//setFilePath
        resultMap.put(FastDFSUtil.RemoteFileField, msg.get(FastDFSUtil.RemoteFileField).asText());//setFileName
        resultMap.put("password", strPwd);

        if(!versionFileService.delete(sourceVersionId,"")) {
            return null;
        }
        VersionFileInfo fileInfo = new VersionFileInfo();
        fileInfo.setFileName(msg.get(FastDFSUtil.RemoteFileField).asText());
        fileInfo.setFilePath(msg.get(FastDFSUtil.GroupField).asText());
        fileInfo.setFile_pwd(strPwd);
        fileInfo.setSourceVersionId(sourceVersionId);
        fileInfo.setTargetVersionId("");

        fileInfo.setCreateDTime(new Date());
        fileInfo.setCreateUser("sys");
        versionFileService.saveVersionFileInfo(fileInfo);

        return resultMap;
    }



    /**
     * 文件重命名
     *
     * @param path    文件目录
     * @param oldFilePath 原来的文件名
     * @param newname 新文件名
     */
    public void renameFile(String path, String oldFilePath, String newname) {
        String splitMark = System.getProperty("file.separator");
        if (!oldFilePath.equals(path + splitMark + newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(oldFilePath);
            File newfile = new File(path + splitMark + newname);
            if (!oldfile.exists()) {
                LogService.getLogger(DispatchService.class).error(oldFilePath+"：重命名文件不存在");
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                LogService.getLogger(DispatchService.class).error(path + splitMark + newname+"文件名称不存在");
            else {
                oldfile.renameTo(newfile);
            }
        } else {

            LogService.getLogger(DispatchService.class).error("newFileName:"+path +splitMark + newname+"；oldFileName:"+oldFilePath+"新文件名和旧文件名相同...");
        }
    }


    /**
     *创建CDA数据文档，并且将CDA文档下载到相应的位置
     * @param strFilePath
     * @param strFileName
     * @param listCDA
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createCDAFile(String strFilePath, String strFileName, List<CDADocument> listCDA) throws Exception{
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_cda");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");

        String[] strColumn = {"id|S", "name|S", "code|S", "source_id|S",
                "print_out|S", "schema_path|S", "description|S",
                "version|S", "inner_version|S"};

        String strPath = System.getProperty("java.io.tmpdir");
        String splitMark = System.getProperty("file.separator");
        strPath += splitMark+"StandardFiles";

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listCDA.size(); i++) {
            CDADocument info = listCDA.get(i);
            Element rowRoot = doc.createElement("row");
            String type = typeMapping.get(info.getType());
            if(type==null)
                type = "add";
            rowRoot.setAttribute("type", type);

            appendChild(rowRoot, doc.createElement("id"), info.getId());
            appendChild(rowRoot, doc.createElement("name"), info.getName());
            appendChild(rowRoot, doc.createElement("code"), info.getCode());
            appendChild(rowRoot, doc.createElement("source_id"), info.getSourceId());
            appendChild(rowRoot, doc.createElement("print_out"), info.getPrintOut());
            appendChild(rowRoot, doc.createElement("schema_path"), info.getSchema());
            appendChild(rowRoot, doc.createElement("description"), info.getDescription());
            appendChild(rowRoot, doc.createElement("version"), "");
            appendChild(rowRoot, doc.createElement("inner_version"), info.getVersionCode());

            root.appendChild(rowRoot);

            String strXMLFilePath = strPath + splitMark + "xml" + splitMark + info.getVersionCode();
            File file = new File(strXMLFilePath);
            if(!file.exists()) {
                file.mkdirs();
            }
            String fileGroup = info.getFileGroup();
            String schemePath=info.getSchema();

            if(!fileGroup.equals("") && !schemePath.equals("")) {
                String localFileName = fastDFSUtil.download(fileGroup, schemePath, strXMLFilePath);
                renameFile(strXMLFilePath,localFileName,info.getId()+".xml");
                File oldFile = new File(localFileName);
                oldFile.delete();
            }
        }
        outputXml(doc, strFilePath + strFileName);
    }

    private void createFiles(String strXMLFilePath, List<CDAVersion> listVersion, String sourceVersionId)
        throws Exception{
        //创建版本文件
        createStandardVersionFile(strXMLFilePath, "std_inner_version.xml", listVersion);

        //创建数据集文件，并获取数据元信息
        List<BaseMetaData> listMateData =
                createDataSetFile(strXMLFilePath, "std_dataset.xml",
                    dataSetService.findAll(dataSetService.getServiceEntity(sourceVersionId)), sourceVersionId);

        //创建数据元文件
        createMetaDataFile(strXMLFilePath, "std_metadata.xml", sourceVersionId, listMateData);

        //创建CDA文档
        String strCDAFileName = "std_cda.xml";

        Class entityClass = getServiceEntity(sourceVersionId);
        List<CDADocument> listCDA = cdaDocumentService.search(entityClass, "id="+ sourceVersionId);
        createCDAFile(strXMLFilePath, strCDAFileName, listCDA);

        //创建关系文档
        String strRelationFileName = "std_cda_dataset_relationship.xml";
        List<CDADataSetRelationship> listRelation = cdaDataSetRelationshipManager.getCDADataSetRelationship(sourceVersionId);
        createCDADatasetRelationshipFile(strXMLFilePath, strRelationFileName, listRelation);

        //创建字典文档，并获取字典值信息
        List<BaseDictEntry> listEntry =
                createDictFile(strXMLFilePath, "std_dict.xml",
                        dictService.findAll(dictService.getServiceEntity(sourceVersionId)), sourceVersionId);

        //创建字典值文档
        createDictEntryFile(strXMLFilePath, "std_dict_item.xml", sourceVersionId, listEntry);
    }


    /**
     * 创建CDA与数据集的关联关系文档
     *
     * @param strFilePath
     * @param strFileName
     * @param listRelastion
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createCDADatasetRelationshipFile(String strFilePath, String strFileName, List<CDADataSetRelationship> listRelastion) throws Exception{
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_cda_dataset_relationship");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"id|S", "cda_id|S", "dataset_id|N"};

        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listRelastion.size(); i++) {
            CDADataSetRelationship info = listRelastion.get(i);
            Element rowRoot = doc.createElement("row");

            appendChild(rowRoot, doc.createElement("id"), info.getId());
            appendChild(rowRoot, doc.createElement("cda_id"), info.getCdaId());
            appendChild(rowRoot, doc.createElement("dataset_id"), info.getDataSetId());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }

}