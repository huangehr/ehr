package com.yihu.ehr.standard.document.service;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.standard.datasets.service.*;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * @author AndyCai
 * @version 1.0
 * @created 01-9月-2015 17:17:50
 */
@Transactional
@Service
public class CDADocumentManager extends BaseHbmService<ICDADocument> {

    @Autowired
    private CDADataSetRelationshipManager cdaDataSetRelationshipManager;

    @Autowired
    private MetaDataService metaDataService;

    @Autowired
    private DataSetService dataSetService;

//    @Autowired
//    private XCDADocumentRepository cdaDocumentRepository;


    public Class getServiceEntity(String version){
        try {
            return Class.forName("com.yihu.ehr.standard.document.service.CDADocument" + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "CDA文档", version);
        }
    }


    public Class getRelationshipServiceEntity(String version){
        try {
            return Class.forName("com.yihu.ehr.standard.document.service.CDADataSetRelationship" + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "CDA数据集关系", version);
        }
    }

    public Class getMeteDataServiceEntity(String version){
        try {
            return Class.forName("com.yihu.ehr.standard.datasets.service.MetaData" + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "数据元版本", version);
        }
    }

    public Class getDataSetServiceEntity(String version){
        try {
            return Class.forName("com.yihu.ehr.standard.datasets.service.DataSet" + version);
        } catch (ClassNotFoundException e) {
            throw new ApiException(ErrorCode.NotFoundEntity, "数据元版本", version);
        }
    }


//    public CDADocument create(CDADocument cDADocument){
//        cdaDocumentRepository.save(cDADocument);
//        return  cDADocument;
//    }


    /**
     * 同时删除多个时 CDAID用逗号隔开
     *
     * @param versionCode
     * @param ids
     */
    public boolean deleteDocument(String[] ids,String versionCode) {
        cdaDataSetRelationshipManager.deleteRelationshipByCdaIds(versionCode,ids);
        delete(ids, getServiceEntity(versionCode));
        return true;
    }


    public boolean isDocumentExist(String version, String documentCode,String documentId) {
        Class entityClass = getServiceEntity(version);
        List list;
        if(StringUtils.isEmpty(documentId)){
            list = search(entityClass,  "code="+documentCode);
        }else {
            list = search(entityClass,  "code="+documentCode+",id="+documentId);
        }
        return list.size() > 0;
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
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + strVersionCode + splitMark +"downfile"+ splitMark;
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
    public boolean createCDASchemaFile(String cdaId, String versionCode) throws Exception {
        //操作结果：0：现在失败 1：新增成功
        Class entityClass = getRelationshipServiceEntity(versionCode);
        List<CDADataSetRelationship> relationshipsList = cdaDataSetRelationshipManager.getCDADataSetRelationshipByCDAId(entityClass,cdaId);

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
            ICDADataSetRelationship dataSetRelationship = relationshipsList.get(i);
            Class dataSetClass = getDataSetServiceEntity(versionCode);
            IDataSet dataSet = dataSetService.retrieve(Long.parseLong(dataSetRelationship.getDataSetId()),dataSetClass);
            Element rowSet = doc.createElement("xs:table");
            rowSet.setAttribute("code", dataSet.getCode());
            root.appendChild(rowSet);

            //获取数据元
            Class meteClass = getMeteDataServiceEntity(versionCode);
            List<IMetaData> metaDatas = metaDataService.search(meteClass,"dataSetId="+dataSet.getId());

            for (int j = 0; j < metaDatas.size(); j++) {
                Element rowEle = doc.createElement("xs:element");
                rowEle.setAttribute("code", metaDatas.get(j).getCode());

                String strColumnType = StringUtils.isEmpty(metaDatas.get(j).getColumnType()) ? "VARCHAR" : metaDatas.get(j).getColumnType();

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