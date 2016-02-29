package com.yihu.ehr.standard.dispatch.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionService;
import com.yihu.ehr.standard.datasets.service.DataSetService;
import com.yihu.ehr.standard.datasets.service.IDataSet;
import com.yihu.ehr.standard.datasets.service.IMetaData;
import com.yihu.ehr.standard.datasets.service.MetaDataService;
import com.yihu.ehr.standard.dict.service.DictEntryService;
import com.yihu.ehr.standard.dict.service.DictService;
import com.yihu.ehr.standard.dict.service.IDict;
import com.yihu.ehr.standard.dict.service.IDictEntry;
import com.yihu.ehr.util.compress.Zipper;
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
 * ��׼�·���Ϣ����
 *
 * @author AndyCai
 * @version 1.0
 * @created 23-7��-2015 15:51:39
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

    public static  Map<String, String> typeMapping = new HashMap<>();

    static {
        typeMapping.put("1", "add");
        typeMapping.put("2", "update");
        typeMapping.put("3", "delete");
    }


    /**
     * �·���׼  �������ã����ɱ�׼�ļ��������ļ���byte[]
     * ������ȫ�汾��׼�ļ������в��컯�汾�ļ�δ����
     *
     * @param strSourceVersionId ���·��ı�׼�汾
     * @return Map 1.filepath �ļ�·��  2.password �ļ�������
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map sendStandard(String strSourceVersionId) throws Exception {
        return sendStandard(strSourceVersionId, "");
    }


    /**
     * �·���׼  �������ã����ɱ�׼�ļ��������ļ���byte[]
     * ������ȫ�汾��׼�ļ������в��컯�汾�ļ�δ����
     *
     * @param strSourceVersionId ���·��ı�׼�汾
     * @param stdTargetVersionId ��ǰʹ�õİ汾
     * @return Map 1.filepath �ļ�·��  2.password �ļ�������
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

            /************20151110 CMS �ݲ����ǲ��컯�汾�������⣬�汾���¾�Ϊȫ�汾�·� Start*************/
                            /*�����ж�Ϊ���컯�汾�����ж�*/

            // if (stdTargetVersionId == null || stdTargetVersionId == "") {
            //ȫ�汾�·�
            map = createFullVersionFile(strSourceVersionId);
//                } else {
            //���컯�汾�·�
//                    map = CreateDiffVersionFile(strSourceVersionId, stdTargetVersionId);
//                }

            /************20151110 CMS �ݲ����ǲ��컯�汾�������⣬�汾���¾�Ϊȫ�汾�·� End*************/
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
     * �����ĵ��ı�ṹ
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
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");//�����ĵ��Ļ���������

        OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
    }

    /**
     * ������׼�汾XML
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createStandardVersionFile(String strFilePath, String strFileName, List<CDAVersion> cdaVersion)
            throws Exception {
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_inner_version");
        doc.appendChild(root);

        //�����汾�ļ�����Ϣ
        root.appendChild(getVersionColumnElementRoot(doc));

        //�����汾��ϸ��Ϣ
        for (CDAVersion version : cdaVersion) {
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), version.getVersion());
            appendChild(rowRoot, doc.createElement("code"), version.getVersion());
            appendChild(rowRoot, doc.createElement("comment"), "");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("type"), "1");
            appendChild(rowRoot, doc.createElement("valid_date"), String.valueOf(version.getCommitTime()));
            appendChild(rowRoot, doc.createElement("author"), version.getAuthor()==""?"����֮·":version.getAuthor());
            appendChild(rowRoot, doc.createElement("id"), version.getVersion());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    /**
     * �������ݼ��ļ�
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<IMetaData> createDataSetFile(String strFilePath, String strFileName, List<IDataSet> dataSet)
            throws Exception {
        //�������ݼ�XML �������ݼ�IDList
        List<IMetaData> listMetaDta = new ArrayList<>();
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dataset");
        doc.appendChild(root);

        //�������ݼ��ļ�����Ϣ
        Element colRoot = doc.createElement("metadata");

        String[] strColumn = {"summary|S", "valid|N", "inner_version|S", "ref_standard|S", "code|S", "catalog|S", "name|S", "publisher|S", "key_word|S", "id|N", "lang|S", "version|S"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //�������ݼ���ϸ��Ϣ
        for (IDataSet xDataSet : dataSet) {
            Element rowRoot = doc.createElement("row");
            String type = typeMapping.get(xDataSet.getOperationType());
            if(type==null){
                type = "add";
                listMetaDta.addAll(
                        metaDataService.search(
                                metaDataService.getServiceEntity(xDataSet.getStdVersion()), "dataSetId=" + xDataSet.getId()));
            }
            rowRoot.setAttribute("type", type);

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

        //����ļ�
        outputXml(doc, strFilePath + strFileName);
        return listMetaDta;
    }


    /**
     * ��������Ԫ�ļ�
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createMetaDataFile(String strFilePath, String strFileName, String version, List<IMetaData> metaDataList)
            throws Exception{
        //��������ԪXML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_metadata");
        doc.appendChild(root);

        //��������Ԫ�ļ�����Ϣ
        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"inner_version|S", "code|S", "nullable|N", "dataset_id|N", "format|S", "column_name|S", "type|S",
                "dict_code|S", "version|S", "primary_key|N", "valid|N", "name|S", "de_code|S", "definition|S", "id|N", "column_type|S", "dict_value_type|N"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //��������Ԫ��ϸ��Ϣ
        for (IMetaData metaData : metaDataList) {
            Element rowRoot = doc.createElement("row");
            String type = typeMapping.get(metaData.getOperationType());
            rowRoot.setAttribute("type", type==null ? "add" : type);

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
     * �����ֵ��ļ�
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<IDictEntry> createDictFile(String strFilePath, String strFileName, List<IDict> dicts)
        throws Exception{
        //�����ֵ�XML�������ֵ�ֵ List
        List<IDictEntry> listEntry = new ArrayList<>();
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dict");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|N", "inner_version|S", "ref_standard|S", "code|S", "name|S", "publisher|S", "version|S", "id|N"};
        //��������Ԫ�ļ�����Ϣ
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        for (IDict xDict : dicts) {
            Element rowRoot = doc.createElement("row");
            String type = typeMapping.get(xDict.getOperationType());
            if(type==null){
                type = "add";
                listEntry.addAll(
                        dictEntryService.search(
                                dictEntryService.getServiceEntity(xDict.getStdVersion()), "dictId="+xDict.getId()));
            }
            rowRoot.setAttribute("type", type);

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
     * �����ֵ�ֵXML�ļ�
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createDictEntryFile(String strFilePath, String strFileName, String version, List<IDictEntry> dictEntry)
        throws Exception{
        //�����ֵ�ֵXML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "std_dict_item");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|S", "inner_version|S", "dict_id|N", "code|S", "value|S", "version|S", "id|N"};

        //��������Ԫ�ļ�����Ϣ
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (IDictEntry xDictEntry : dictEntry) {
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

        //�½��ļ����������������л���
        FileInputStream input=new FileInputStream(sourcefile);
        BufferedInputStream inbuff=new BufferedInputStream(input);

        //�½��ļ���������������л���
        FileOutputStream out=new FileOutputStream(targetFile);
        BufferedOutputStream outbuff=new BufferedOutputStream(out);

        //��������
        byte[] b=new byte[1024*5];
        int len=0;
        while((len=inbuff.read(b))!=-1){
            outbuff.write(b, 0, len);
        }

        //ˢ�´˻���������
        outbuff.flush();

        //�ر���
        inbuff.close();
        outbuff.close();
        out.close();
        input.close();
    }


    /**
     * �����汾�ļ����������ļ�·��
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map createFullVersionFile(String sourceVersionId)
        throws Exception{
        Map resultMap = new HashMap<>();

        //������׼�汾 ���ݼ� ����Ԫ �ֵ� �ֵ�ֵ 5��XML�ļ� ��������zipѹ����
        String strFilePath = getFileSystemPath();

        String splitMark=System.getProperty("file.separator");
        strFilePath += splitMark+"StandardFiles";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strFileName = format.format(new Date()) + ".zip";
        String strXMLFilePath = strFilePath + splitMark+"xml" +splitMark+ format.format(new Date()) + splitMark;
        String strZIPFilePath = strFilePath + splitMark+"zip"+splitMark;
        File zipFile = new File(strZIPFilePath);

        File targetFile = new File(strXMLFilePath);

        //���Ŀ��·�������ڣ����½�
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!zipFile.exists()) {
            zipFile.mkdirs();
        }

        //��ȡ��׼�汾��Ϣ
        List<CDAVersion> listVersion = new ArrayList<CDAVersion>();
        CDAVersion sourceVersion = cdaVersionService.retrieve(sourceVersionId);
        listVersion.add(sourceVersion);

        //�����ĵ�
        createFiles(strXMLFilePath, listVersion, sourceVersionId);

        //Դ�ļ�·��
        String strCDAFilePath =  strFilePath + splitMark+"xml" +splitMark+sourceVersion.getVersion()+splitMark;
        File file = new File(strCDAFilePath);
        //�ж��ļ����Ƿ����
        if (file.exists()) {
            //Ŀ���ļ�·��
            String strCDAFileZipPath = strXMLFilePath+"CDA_File"+splitMark;
            File fileCDAZip = new File(strCDAFileZipPath);
            if(!fileCDAZip.exists()) {
                fileCDAZip.mkdirs();
            }

            File[] fileCDA=file.listFiles();
            for (int i = 0; i < fileCDA.length; i++) {
                if(fileCDA[i].isFile()){
                    //�����ļ�
                    copyFile(fileCDA[i],new File(strCDAFileZipPath+fileCDA[i].getName()));
                }
            }
        }

        //����Zip�ļ�
        Zipper zipper = new Zipper();
        File resourcesFile = new File(strXMLFilePath);
        String strPwd = UUID.randomUUID().toString();
        zipper.zipFile(resourcesFile, strZIPFilePath + strFileName, strPwd);

        //���ļ��ϴ�����������
        ObjectNode msg = new FastDFSUtil().upload(strZIPFilePath + strFileName, "");

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


    private void createFiles(String strXMLFilePath, List<CDAVersion> listVersion, String sourceVersionId)
        throws Exception{
        //�����汾�ļ�
        createStandardVersionFile(strXMLFilePath, "std_inner_version.xml", listVersion);

        //�������ݼ��ļ�������ȡ����Ԫ��Ϣ
        List<IMetaData> listMateData =
                createDataSetFile(strXMLFilePath, "std_dataset.xml",
                    dataSetService.findAll(dataSetService.getServiceEntity(sourceVersionId)));

        //��������Ԫ�ļ�
        createMetaDataFile(strXMLFilePath, "std_metadata.xml", sourceVersionId, listMateData);

        //����CDA�ĵ�
//        String strCDAFileName = "std_cda.xml";
//        List<CDADocument> listCDA = Arrays.asList(xcdaDocumentManager.getDocumentList(sourceVersion.getVersion(), ""));
//        createCDAFile(strXMLFilePath, strCDAFileName, listCDA);

        //������ϵ�ĵ�
//        String strRelationFileName = "std_cda_dataset_relationship.xml";
//        List<XCdaDatasetRelationship> listRelation = Arrays.asList(xCdaDatasetRelationshipManager.getRelationshipByVersion(sourceVersion.getVersion()));
//        createCDADatasetRelationshipFile(strXMLFilePath, strRelationFileName, listRelation);

        //�����ֵ��ĵ�������ȡ�ֵ�ֵ��Ϣ
        List<IDictEntry> listEntry =
                createDictFile(strXMLFilePath, "std_dict.xml",
                        dictService.findAll(dictService.getServiceEntity(sourceVersionId)));

        //�����ֵ�ֵ�ĵ�
        createDictEntryFile(strXMLFilePath, "std_dict_item.xml", sourceVersionId, listEntry);
    }
}