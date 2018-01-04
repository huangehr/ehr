package com.yihu.ehr.adaption.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.adaption.feignclient.DispatchLogClient;
import com.yihu.ehr.adaption.feignclient.StandardDispatchClient;
import com.yihu.ehr.adaption.model.*;
import com.yihu.ehr.adaption.model.OrgDict;
import com.yihu.ehr.adaption.model.OrgDictItem;
import com.yihu.ehr.adaption.model.OrgMetaData;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.standard.MDispatchLog;
import com.yihu.ehr.standard.model.DispatchLog;
import com.yihu.ehr.standard.service.DispatchLogService;
import com.yihu.ehr.standard.service.DispatchService;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/1
 */
@Service
public class AdapterInfoSendService {
    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    private OrgDataSetService orgDataSetService;
    @Autowired
    private OrgMetaDataService orgMetaDataService;
    @Autowired
    private OrgDictService orgDictService;
    @Autowired
    private OrgDictItemService orgDictItemService;
    @Autowired
    private OrgAdapterPlanService orgAdapterPlanService;
    @Autowired
    private AdapterDataSetService adapterDataSetService;
    @Autowired
    private AdapterDictService adapterDictService;
    @Autowired
    private DispatchLogService dispatchLogService;
    @Autowired
    private DispatchService dispatchService;

    public String getFileSystemPath() {
        String strPath = System.getProperty("java.io.tmpdir");
        return strPath;
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

    private void outputXml(Document doc, String fileName) throws Exception{
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");//设置文档的换行与缩进

        OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
    }

    private Document createDocument() throws ParserConfigurationException {

        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private Element appendChild(Element parent, Element child, String content){
        child.setTextContent(content);
        parent.appendChild(child);
        return parent;
    }

    /*
    * 生成数据集文件
    * @param listDataSet 数据集列表
    * @param strFilePath 文件保存路径
    * */
    public void createDataSetFile(List<OrgDataSet> listDataSet, String strFilePath) throws Exception{
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "org_dataset");
        doc.appendChild(root);

        //创建数据集文件列信息
        Element colRoot = doc.createElement("metadata");

        String[] strColumn = {"summary|S", "valid|N", "inner_version|S", "ref_standard|S", "code|S", "catalog|S", "name|S", "publisher|S", "key_word|S", "id|N", "lang|S", "version|S"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //创建数据集详细信息
        for (int i = 0; i < listDataSet.size(); i++) {
            OrgDataSet xDataSet = listDataSet.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("summary"), "");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("inner_version"), "");
            appendChild(rowRoot, doc.createElement("ref_standard"), "");
            appendChild(rowRoot, doc.createElement("code"), xDataSet.getCode());
            appendChild(rowRoot, doc.createElement("catalog"), "");
            appendChild(rowRoot, doc.createElement("name"), xDataSet.getName());
            appendChild(rowRoot, doc.createElement("key_word"), "");
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(xDataSet.getId()));
            appendChild(rowRoot, doc.createElement("lang"), "");
            appendChild(rowRoot, doc.createElement("version"), "");

            root.appendChild(rowRoot);
        }
        //输出文件
        outputXml(doc, strFilePath + "org_dataset.xml");
    }

    /*
     * 生成数据元文件
     * @param listElement 数据元列表
     * @param strFilePath 文件保存路径
     * */
    public void createElementFile(List<OrgMetaData> listElement, String strFilePath) throws Exception{
        //创建数据元XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "org_metadata");
        doc.appendChild(root);

        //创建数据元文件列信息
        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"inner_version|S", "code|S", "nullable|N", "dataset_id|N", "format|S", "column_name|S", "type|S",
                "dict_code|S", "version|S", "primary_key|N", "valid|N", "name|S", "de_code|S", "definition|S", "id|N", "column_type|S", "dict_value_type|N"};
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        //创建数据元明细信息
        for (int i = 0; i < listElement.size(); i++) {
            OrgMetaData metaData = listElement.get(i);

            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("inner_version"), "");
            appendChild(rowRoot, doc.createElement("code"), metaData.getCode());
            appendChild(rowRoot, doc.createElement("nullable"), "");
            appendChild(rowRoot, doc.createElement("dataset_id"), metaData.getOrgDataSet().toString());
            appendChild(rowRoot, doc.createElement("format"), "");
            appendChild(rowRoot, doc.createElement("column_name"), "");
            appendChild(rowRoot, doc.createElement("type"), "");
            appendChild(rowRoot, doc.createElement("dict_code"), "");
            appendChild(rowRoot, doc.createElement("version"), "");
            appendChild(rowRoot, doc.createElement("primary_key"), "");
            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("name"), metaData.getName());
            appendChild(rowRoot, doc.createElement("de_code"), "");
            appendChild(rowRoot, doc.createElement("definition"), metaData.getDescription());
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(metaData.getId()));
            appendChild(rowRoot, doc.createElement("dict_value_type"), "");
            appendChild(rowRoot, doc.createElement("column_type"),
                    metaData.getColumnLength()==null|| metaData.getColumnLength().equals( 0)
                            ? metaData.getColumnType()
                            : metaData.getColumnType() + "(" + metaData.getColumnLength() + ")" );

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + "org_metadata.xml");
    }

    /*
    * 生成字典文件
    * @param listDict 字典列表
    * @param strFilePath 文件保存路径
    * */
    public void createDictFile(List<OrgDict> listDict, String strFilePath) throws Exception{

        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "org_dict");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|N", "inner_version|S", "ref_standard|S", "code|S", "name|S", "publisher|S", "version|S", "id|N"};
        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));

        for (int i = 0; i < listDict.size(); i++) {
            OrgDict xDict = listDict.get(i);

            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("inner_version"), "");
            appendChild(rowRoot, doc.createElement("ref_standard"), "");
            appendChild(rowRoot, doc.createElement("code"), xDict.getCode());
            appendChild(rowRoot, doc.createElement("name"), xDict.getName());
            appendChild(rowRoot, doc.createElement("publisher"), "");
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(xDict.getId()));
            appendChild(rowRoot, doc.createElement("version"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + "org_dict.xml");
    }

    /*
    * 生成字典项文件
    * @param listDictEntry 字典项列表
    * @param strFilePath 文件保存路径
    * */
    public void createDictEntryFile(List<OrgDictItem> listDictEntry, String strFilePath) throws Exception{
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "org_dict_item");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"valid|S", "inner_version|S", "dict_id|N", "code|S", "value|S", "version|S", "id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listDictEntry.size(); i++) {
            OrgDictItem xDictEntry = listDictEntry.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("valid"), "1");
            appendChild(rowRoot, doc.createElement("inner_version"), "");
            appendChild(rowRoot, doc.createElement("dict_id"), String.valueOf(xDictEntry.getOrgDict()));
            appendChild(rowRoot, doc.createElement("code"), xDictEntry.getCode());
            appendChild(rowRoot, doc.createElement("value"), xDictEntry.getName());
            appendChild(rowRoot, doc.createElement("version"), "");
            appendChild(rowRoot, doc.createElement("id"), String.valueOf(xDictEntry.getId()));
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");
            appendChild(rowRoot, doc.createElement("valid"), "");

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + "org_dict_item.xml");
    }

    /*
   * 创建机构标准文件
   * @param strOrgCode 机构代码
   * @strFilePath 文件路径
   * */
    public void createOrgStandardFile(String strOrgCode, String strFilePath) throws Exception{

        File targetFile = new File(strFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //生成数据集文件
        List<OrgDataSet> listDataSet = orgDataSetService.findByField("organization", strOrgCode);
        createDataSetFile(listDataSet, strFilePath);

        //生成数据元文件
        List<OrgMetaData> listElement = orgMetaDataService.findByField("organization", strOrgCode);
        createElementFile(listElement, strFilePath);

        //生成字典文件
        List<OrgDict> listDict = orgDictService.findByField("organization", strOrgCode);
        createDictFile(listDict, strFilePath);

        //生成字典项文件
        List<OrgDictItem> listDictEntry = orgDictItemService.findByField("organization", strOrgCode);
        createDictEntryFile(listDictEntry, strFilePath);
    }

    public String createAllMappingFile(String versionCode,String strOrgCode,String strXMLFilePath,String splitMark) throws Exception{

        //根据标准版本ID和机构ID获取映射版本信息，未找到信息返回false
        Map<String, Object> mapKey = new HashMap<>();
        mapKey.put("versioncode", versionCode);
        mapKey.put("orgcode", strOrgCode);
        List<OrgAdapterPlan> listPlan =
                orgAdapterPlanService.findByFields(
                        new String[]{"version", "org"},
                        new Object[]{versionCode, strOrgCode}
                );
        if (listPlan.size() < 1)
            return "未找到适配方案！";

        //根据机构Code 获取机构标准信息
        createOrgStandardFile(strOrgCode, strXMLFilePath + "OrgStandard" + splitMark);

        OrgAdapterPlan planInfo = listPlan.get(0);
        //根据映射版本信息获取 映射信息
        createAdapterFile(planInfo.getId().toString(), versionCode, strXMLFilePath + "StandardAdapter" + splitMark, strOrgCode);

        return "";
    }


    /*
    * 创建数据集映射文件
    * @param listDataSetMapping 数据集映射信息
    * @param strFilePath 文件路径
    * */
    public void createDataSetMapping(List<DataSetMappingInfo> listDataSetMapping, String strFilePath) throws Exception{
        String strFileName = "adapter_dataset.xml";
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "adapter_dataset");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"id|N", "std_dataset_id|N", "org_dataset_id|N",
                "std_dataset_code|S", "org_dataset_code|S",
                "std_dataset_name|S", "org_dataset_name|S",
                "scheme_id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listDataSetMapping.size(); i++) {
            DataSetMappingInfo xInfo = listDataSetMapping.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), xInfo.getId());
            appendChild(rowRoot, doc.createElement("std_dataset_id"), xInfo.getStdSetId());
            appendChild(rowRoot, doc.createElement("org_dataset_id"), String.valueOf(xInfo.getOrgSetId()));
            appendChild(rowRoot, doc.createElement("std_dataset_code"), xInfo.getStdSetCode());
            appendChild(rowRoot, doc.createElement("org_dataset_code"), xInfo.getOrgSetCode());
            appendChild(rowRoot, doc.createElement("std_dataset_name"), xInfo.getStdSetName());
            appendChild(rowRoot, doc.createElement("org_dataset_name"), xInfo.getOrgSetName());
            appendChild(rowRoot, doc.createElement("scheme_id"), xInfo.getPlanId());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    /*
    * 创建数据元映射文件
    * @param listElementMapping 数据元映射关系列表
    * @Parma strFilePath 文件路径
    *
    * */
    public void createMetaDataMapping(List<MetadataMappingInfo> listElementMapping, String strFilePath) throws Exception{
        String strFileName = "adapter_metadata.xml";
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "adapter_metadata");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"id|N", "adapter_dataset_id|N", "std_metadata_id|N", "org_metadata_id|N",
                "std_metadata_code|S", "org_metadata_code|S",
                "std_metadata_name|S", "org_metadata_name|S",
                "org_dict_data_type|S", "scheme_id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listElementMapping.size(); i++) {
            MetadataMappingInfo xInfo = listElementMapping.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), xInfo.getId());
            appendChild(rowRoot, doc.createElement("adapter_dataset_id"), xInfo.getAdapterDataSetId());
            appendChild(rowRoot, doc.createElement("std_metadata_id"), xInfo.getStdMetadataId());
            appendChild(rowRoot, doc.createElement("org_metadata_id"), String.valueOf(xInfo.getOrgMetadataId()));
            appendChild(rowRoot, doc.createElement("std_metadata_code"), xInfo.getStdMetadataCode());
            appendChild(rowRoot, doc.createElement("org_metadata_code"), xInfo.getOrgMetadataCode());
            appendChild(rowRoot, doc.createElement("org_metadata_code"), xInfo.getOrgMetadataCode());
            appendChild(rowRoot, doc.createElement("org_dict_data_type"), xInfo.getOrgDictDataType());
            appendChild(rowRoot, doc.createElement("std_metadata_name"), xInfo.getStdMetadataName());
            appendChild(rowRoot, doc.createElement("org_metadata_name"), xInfo.getOrgMetadataName());
            appendChild(rowRoot, doc.createElement("scheme_id"), xInfo.getPlanId());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }



    /*
   * 创建字典映射文件
   * @param listInfo 字典映射关系列表
   * @Parma strFilePath 文件路径
   *
   * */
    public void createDictMapping(List<DictMappingInfo> listInfo, String strFilePath) throws Exception{
        String strFileName = "adapter_dict.xml";
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "adapter_dict");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"id|N", "std_dict_id|N", "org_dict_id|N",
                "std_dict_code|S", "org_dict_code|S",
                "std_dict_name|S", "org_dict_name|S",
                "scheme_id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listInfo.size(); i++) {
            DictMappingInfo xInfo = listInfo.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), xInfo.getId());
            appendChild(rowRoot, doc.createElement("std_dict_id"), xInfo.getStdDictId());
            appendChild(rowRoot, doc.createElement("org_dict_id"), String.valueOf(xInfo.getOrgDictId()));
            appendChild(rowRoot, doc.createElement("std_dict_code"), xInfo.getStdDictCode());
            appendChild(rowRoot, doc.createElement("org_dict_code"), xInfo.getOrgDictCode());
            appendChild(rowRoot, doc.createElement("std_dict_name"), xInfo.getStdDictName());
            appendChild(rowRoot, doc.createElement("org_dict_name"), xInfo.getOrgDictName());
            appendChild(rowRoot, doc.createElement("scheme_id"), xInfo.getPlanId());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }


    /*
    * 创建字典项映射文件
    * @param listInfo 字典项映射关系列表
    * @Parma strFilePath 文件路径
    *
    * */
    public void createDictEntryMapping(List<DictEntryMappingInfo> listInfo, String strFilePath) throws Exception{
        String strFileName = "adapter_dict_item.xml";
        //创建字典值XML
        Document doc = createDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", "adapter_dict_item");
        doc.appendChild(root);

        Element colRoot = doc.createElement("metadata");
        String[] strColumn = {"id|N", "adapter_dict_id|N", "std_dict_item_id|N", "org_dict_item_id|N",
                "std_dict_item_code|S", "std_dict_item_value|S",
                "org_dict_item_code|S", "org_dict_item_value|S",
                "scheme_id|N"};

        //创建数据元文件列信息
        root.appendChild(getColumnElement(doc, colRoot, strColumn));
        for (int i = 0; i < listInfo.size(); i++) {
            DictEntryMappingInfo xInfo = listInfo.get(i);
            Element rowRoot = doc.createElement("row");
            rowRoot.setAttribute("type", "add");

            appendChild(rowRoot, doc.createElement("id"), xInfo.getId());
            appendChild(rowRoot, doc.createElement("adapter_dict_id"), xInfo.getAdapterDictId());
            appendChild(rowRoot, doc.createElement("std_dict_item_id"), String.valueOf(xInfo.getStdDictEntryId()));
            appendChild(rowRoot, doc.createElement("org_dict_item_id"), xInfo.getOrgDictEntryId());
            appendChild(rowRoot, doc.createElement("std_dict_item_code"), xInfo.getStdDictEntryCode());
            appendChild(rowRoot, doc.createElement("std_dict_item_value"), xInfo.getStdDictEntryValue());
            appendChild(rowRoot, doc.createElement("org_dict_item_code"), xInfo.getOrgDictEntryCode());
            appendChild(rowRoot, doc.createElement("org_dict_item_value"), xInfo.getOrgDictEntryValue());
            appendChild(rowRoot, doc.createElement("scheme_id"), xInfo.getPlanId());

            root.appendChild(rowRoot);
        }
        outputXml(doc, strFilePath + strFileName);
    }

    /*
    * 创建适配文件信息
    * @param strAdapterId 适配方案ID
    *
    * */
    public void createAdapterFile(String strPlanId, String versionCode, String strFilePath,String strOrgCode) throws Exception{

        File targetFile = new File(strFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //生成数据集映射文件
        Map mapResult = adapterDataSetService.getDataSetMappingInfo(strPlanId, versionCode,strOrgCode);
        if(mapResult==null){
            throw new NullPointerException("数据集映射信息为空！");
        }
        List<DataSetMappingInfo> listDataSetMapping = (List<DataSetMappingInfo>) mapResult.get("datasetlist");
        createDataSetMapping(listDataSetMapping, strFilePath);

        //生成数据元映射文件
        List<MetadataMappingInfo> listElementMapping = (List<MetadataMappingInfo>) mapResult.get("elementlist");
        createMetaDataMapping(listElementMapping, strFilePath);

        //生成字典映射文件
        Map dictResultMap = adapterDictService.getDictMappingInfo(strPlanId, versionCode, strOrgCode);
        if(dictResultMap==null){
            throw new NullPointerException("字典映射信息为空！");
        }
        List<DictMappingInfo> listDictMapping = (List<DictMappingInfo>) dictResultMap.get("dictlist");
        createDictMapping(listDictMapping, strFilePath);

        //生成字典项映射文件
        List<DictEntryMappingInfo> listDictEntryMapping = (List<DictEntryMappingInfo>) dictResultMap.get("dictentrylist");
        createDictEntryMapping(listDictEntryMapping, strFilePath);
    }

    public Map getAdapterFileInfo(String versionCode, String strOrgCode) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String strFilePath = getFileSystemPath();
            String splitMark = System.getProperty("file.separator");
            strFilePath += splitMark+"StandardFiles";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String strFileName = format.format(new Date()) + ".zip";
            String strXMLFilePath = strFilePath + splitMark + "standardadapter" + splitMark + "xml" + splitMark + format.format(new Date()) + splitMark;
            String strZIPFilePath = strFilePath + splitMark + "standardadapter" + splitMark + "zip" + splitMark;
            File zipFile = new File(strZIPFilePath);

            File targetFile = new File(strXMLFilePath);

            //如果目的路径不存在，则新建
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            if (!zipFile.exists()) {
                zipFile.mkdirs();
            }

            String strErrorMsg = createAllMappingFile(versionCode, strOrgCode, strXMLFilePath, splitMark);
            if(strErrorMsg!="") {
                resultMap.put("ErrorMsg", strErrorMsg);
                resultMap.put("IsSuccess", "false");

                return resultMap;
            }

            //生成Zip文件
            Zipper zipper = new Zipper();
            File resourcesFile = new File(strXMLFilePath);
            String strPwd = UUID.randomUUID().toString();
            zipper.zipFile(resourcesFile, strZIPFilePath + strFileName, strPwd);

            //将文件上传到服务器中
            ObjectNode msg = fastDFSUtil.upload(strZIPFilePath + strFileName, "");

            resultMap.put(FastDFSUtil.GROUP_NAME, msg.get(FastDFSUtil.GROUP_NAME).asText());//setFilePath
            resultMap.put(FastDFSUtil.REMOTE_FILE_NAME, msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText());//setFileName
            resultMap.put("password", strPwd);
            resultMap.put("ErrorMsg", "");
            resultMap.put("IsSuccess", "true");

            resourcesFile.deleteOnExit();

        } catch (Exception ex) {
            resultMap.put("ErrorMsg", ex.getMessage());
            resultMap.put("IsSuccess", "false");
        }

        return resultMap;
    }


    public Map<String, Object> getStandardAndMappingInfo(String versionCode, String strOrgCode) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();
        DispatchLog dispatchLog = null;
        List ls = dispatchLogService.findByFields(
                new String[]{"stdVersionId", "orgId"},
                new Object[]{versionCode, strOrgCode});
        if (ls.size() > 0) {
            dispatchLog = (DispatchLog)ls.get(0);
        }
        if(dispatchLog!=null) {
            resultMap.put(FastDFSUtil.GROUP_NAME,dispatchLog.getFileGroup());//setFilePath
            resultMap.put(FastDFSUtil.REMOTE_FILE_NAME, dispatchLog.getFilePath());//setFileName
            resultMap.put("password", dispatchLog.getPassword());
            resultMap.put("ErrorMsg", "");
            resultMap.put("IsSuccess", "true");
            return resultMap;
        }
        return null;
    }


    /**
     * 创建采集标准和适配方案信息
     * @param versionCode 版本号
     * @param strOrgCode 机构编号
     *@return Map<String, Object> password:压缩密码 IsSuccess：是否成功  ErrorMsg：错误消息 groupName:
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Map<String, Object> createStandardAndMappingInfo(String versionCode, String strOrgCode) throws Exception{
        Map<String, Object> resultMap = new HashMap<>();

        String strFilePath = getFileSystemPath();
        String splitMark = System.getProperty("file.separator");
        strFilePath += splitMark+"StandardFiles";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strFileName = format.format(new Date()) + ".zip";
        String strXMLFilePath = strFilePath + splitMark + "standardadapter" + splitMark + "xml" + splitMark + format.format(new Date()) + splitMark;
        String strZIPFilePath = strFilePath + splitMark + "standardadapter" + splitMark + "zip" + splitMark;
        File zipFile = new File(strZIPFilePath);

        File targetFile = new File(strXMLFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        if (!zipFile.exists()) {
            zipFile.mkdirs();
        }

        //根据版本号获取采集标准文件
        Map<String, Object> standardMap = dispatchService.sendStandard(versionCode);

        if (standardMap == null) {
            standardMap = new HashMap<>();
            standardMap.put("ErrorMsg", "未找到采集标准!");
            standardMap.put("IsSuccess", "false");
            return standardMap;
        }

        String group = (String) standardMap.get(FastDFSUtil.GROUP_NAME);
        String remoteFile = (String) standardMap.get(FastDFSUtil.REMOTE_FILE_NAME);
        String password = (String) standardMap.get("password");

        String strLocalFileName = fastDFSUtil.download(group, remoteFile, strFilePath + splitMark + "standardadapter" + splitMark + "xml" + splitMark);
        //将采集标准文件解压到适配方案路径下
        Zipper zipper = new Zipper();
        File standardFile = new File(strLocalFileName);
        String ss =standardFile.getName();
        File standardFileXml= zipper.unzipFile(standardFile,strXMLFilePath,password);

        //生成适配映射方案信息
        String strErrorMsg = createAllMappingFile(versionCode, strOrgCode, strXMLFilePath, splitMark);
        if(strErrorMsg!="")
        {
            resultMap.put("ErrorMsg", strErrorMsg);
            resultMap.put("IsSuccess", "false");

            return resultMap;
        }

        //生成Zip文件
        //    Zipper zipper = new Zipper();
        File resourcesFile = new File(strXMLFilePath);
        String strPwd = UUID.randomUUID().toString();
        zipper.zipFile(resourcesFile, strZIPFilePath + strFileName, strPwd);

        //将文件上传到服务器中
        ObjectNode msg = fastDFSUtil.upload(strZIPFilePath + strFileName, "");

        resultMap.put(FastDFSUtil.GROUP_NAME, msg.get(FastDFSUtil.GROUP_NAME).asText());//setFilePath
        resultMap.put(FastDFSUtil.REMOTE_FILE_NAME, msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText());//setFileName
        resultMap.put("password", strPwd);
        resultMap.put("ErrorMsg", "");
        resultMap.put("IsSuccess", "true");

        dispatchLogService.delete(versionCode, strOrgCode);

        DispatchLog logInfo = new DispatchLog();
        logInfo.setOrgId(strOrgCode);
        logInfo.setStdVersionId(versionCode);
        logInfo.setDispatchTime(new Date());
        logInfo.setFileGroup(msg.get(FastDFSUtil.GROUP_NAME).asText());
        logInfo.setFilePath(msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText());
        logInfo.setPassword(strPwd);
        ObjectMapper objectMapper = new ObjectMapper();
        String model = objectMapper.writeValueAsString(logInfo);
        dispatchLogService.save(logInfo);

        return resultMap;
    }
}
