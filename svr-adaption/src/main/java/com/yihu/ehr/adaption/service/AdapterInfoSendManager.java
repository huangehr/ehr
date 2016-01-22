package com.yihu.ehr.adaption.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ha.constrant.BizObject;
import com.yihu.ha.constrant.EnvironmentOptions;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.std.model.DispatchLog;
import com.yihu.ha.std.model.XDispatchLog;
import com.yihu.ha.std.model.XDispatchLogManager;
import com.yihu.ha.std.model.XStdDispatchManager;
import com.yihu.ha.util.ObjectId;
import com.yihu.ha.util.XEnvironmentOption;
import com.yihu.ha.util.compress.Zipper;
import com.yihu.ha.util.fastdfs.FastDFSUtil;
import com.yihu.ha.util.log.LogService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Resource;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AndyCai on 2015/11/30.
 */
@Service(Services.AdapterInfoSendManager)
public class AdapterInfoSendManager implements XAdapterInfoSendManager {

    @Resource(name = Services.OrgDataSetManager)
    private XOrgDataSetManager xOrgDataSetManager;

    @Resource(name = Services.OrgMetaDataManager)
    private XOrgMetaDataManager xOrgMetaDataManager;

    @Resource(name = Services.OrgDictManager)
    private XOrgDictManager xOrgDictManager;

    @Resource(name = Services.OrgDictItemManager)
    private XOrgDictItemManager xOrgDictItemManager;

    @Resource(name = Services.OrgAdapterPlanManager)
    private XOrgAdapterPlanManager xOrgAdapterPlanManager;

    @Resource(name = Services.AdapterDataSetManager)
    private XAdapterDataSetManager xAdapterDataSetManager;

    @Resource(name = Services.AdapterDictManager)
    private XAdapterDictManager xAdapterDictManager;

    @Resource(name = Services.StdDispatchManager)
    private XStdDispatchManager stdSendManager;

    @Resource(name = Services.DispatchLogManager)
    private XDispatchLogManager xDispatchLogManager;

    public String getFileSystemPath() {
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";

        return strPath;
    }

    public Map GetAdapterFileInfo(String versionCode, String strOrgCode) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String strFilePath = getFileSystemPath();

            String splitMark = System.getProperty("file.separator");
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

            String strErrorMsg = CreateAllMappingFile(versionCode,strOrgCode,strXMLFilePath,splitMark);
            if(strErrorMsg!="")
            {
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
            ObjectNode msg = FastDFSUtil.upload(strZIPFilePath + strFileName, "");

            resultMap.put(FastDFSUtil.GroupField, msg.get(FastDFSUtil.GroupField).asText());//setFilePath
            resultMap.put(FastDFSUtil.RemoteFileField, msg.get(FastDFSUtil.RemoteFileField).asText());//setFileName
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

    public String CreateAllMappingFile(String versionCode,String strOrgCode,String strXMLFilePath,String splitMark){

        //根据标准版本ID和机构ID获取映射版本信息，未找到信息返回false
        Map<String, Object> mapKey = new HashMap<>();
        mapKey.put("versioncode", versionCode);
        mapKey.put("orgcode", strOrgCode);
        List<XOrgAdapterPlan> listPlan = xOrgAdapterPlanManager.getOrgAdapterPlanByOrgCodeAndVersionCode(mapKey);
        if (listPlan.size() < 1) {

            return "未找到适配方案！";
        }

        //根据机构Code 获取机构标准信息
        CreateOrgStandardFile(strOrgCode, strXMLFilePath + "OrgStandard" + splitMark);

        XOrgAdapterPlan planInfo = listPlan.get(0);
        //根据映射版本信息获取 映射信息
        CreateAdapterFile(planInfo.getId().toString(), versionCode, strXMLFilePath + "StandardAdapter" + splitMark,strOrgCode);

        return "";
    }

    /*
    * 创建机构标准文件
    * @param strOrgCode 机构代码
    * @strFilePath 文件路径
    * */
    public void CreateOrgStandardFile(String strOrgCode, String strFilePath) {

        File targetFile = new File(strFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //生成数据集文件
        Map<String, Object> map = new HashMap<>();
        map.put("orgCode", strOrgCode);
        map.put("code", "");
        map.put("page", 0);
        map.put("rows", 0);
        List<XOrgDataSet> listDataSet = xOrgDataSetManager.searchOrgDataSet(map);
        CreateDataSetFile(listDataSet, strFilePath);

        //生成数据元文件
        List<XOrgMetaData> listElement = xOrgMetaDataManager.getAllOrgMetaData(map);
        CreateElementFile(listElement, strFilePath);

        //生成字典文件
        List<XOrgDict> listDict = xOrgDictManager.searchOrgDict(map);
        CreateDictFile(listDict, strFilePath);

        //生成字典项文件
        List<XOrgDictItem> listDictEntry = xOrgDictItemManager.getAllOrgDictItem(map);
        CreateDictEntryFile(listDictEntry, strFilePath);
    }

    /*
    * 生成数据集文件
    * @param listDataSet 数据集列表
    * @param strFilePath 文件保存路径
    * */
    public void CreateDataSetFile(List<XOrgDataSet> listDataSet, String strFilePath) {
        String strFileName = "org_dataset.xml";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "org_dataset");
            doc.appendChild(root);

            //创建数据集文件列信息
            Element colRoot = doc.createElement("metadata");

            String[] strColumn = {"summary|S", "valid|N", "inner_version|S", "ref_standard|S", "code|S", "catalog|S", "name|S", "publisher|S", "key_word|S", "id|N", "lang|S", "version|S"};
            root.appendChild(getColumnElement(doc, colRoot, strColumn));

            //创建数据集详细信息
            for (int i = 0; i < listDataSet.size(); i++) {
                XOrgDataSet xDataSet = listDataSet.get(i);
                Element rowRoot = doc.createElement("row");
                rowRoot.setAttribute("type", "add");

                Element rowData_sum = doc.createElement("summary");
                rowData_sum.setTextContent("");
                rowRoot.appendChild(rowData_sum);

                Element rowData_val = doc.createElement("valid");
                rowData_val.setTextContent("1");
                rowRoot.appendChild(rowData_val);

                Element rowData_inner = doc.createElement("inner_version");
                rowData_inner.setTextContent("");
                rowRoot.appendChild(rowData_inner);

                Element rowData_ref = doc.createElement("ref_standard");
                rowData_ref.setTextContent("");
                rowRoot.appendChild(rowData_ref);

                Element rowData_code = doc.createElement("code");
                rowData_code.setTextContent(xDataSet.getCode());
                rowRoot.appendChild(rowData_code);

                Element rowData_cat = doc.createElement("catalog");
                rowData_cat.setTextContent("");
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
                rowData_lang.setTextContent("");
                rowRoot.appendChild(rowData_lang);

                Element rowData_ver = doc.createElement("version");
                rowData_ver.setTextContent("");
                rowRoot.appendChild(rowData_ver);

                root.appendChild(rowRoot);
            }

            //输出文件
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception e) {
            LogService.getLogger(AdapterInfoSendManager.class).error(e.getMessage());
        }

    }

    /*
     * 生成数据元文件
     * @param listElement 数据元列表
     * @param strFilePath 文件保存路径
     * */
    public void CreateElementFile(List<XOrgMetaData> listElement, String strFilePath) {

        String strFileName = "org_metadata.xml";
        //创建数据元XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
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
                XOrgMetaData metaData = listElement.get(i);

                Element rowRoot = doc.createElement("row");
                rowRoot.setAttribute("type", "add");

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent("");
                rowRoot.appendChild(row_inner);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(metaData.getCode());
                rowRoot.appendChild(row_code);

                Element row_isnull = doc.createElement("nullable");
                row_isnull.setTextContent("");
                rowRoot.appendChild(row_isnull);

                Element row_dataset = doc.createElement("dataset_id");
                row_dataset.setTextContent(metaData.getOrgDataSet().toString());
                rowRoot.appendChild(row_dataset);

                Element row_format = doc.createElement("format");
                row_format.setTextContent("");
                rowRoot.appendChild(row_format);

                Element row_col = doc.createElement("column_name");
                row_col.setTextContent("");
                rowRoot.appendChild(row_col);

                Element row_type = doc.createElement("type");
                row_type.setTextContent("");
                rowRoot.appendChild(row_type);

                Element row_dict = doc.createElement("dict_code");
                row_dict.setTextContent("");
                rowRoot.appendChild(row_dict);

                Element row_ver = doc.createElement("version");
                row_ver.setTextContent("");
                rowRoot.appendChild(row_ver);

                Element row_key = doc.createElement("primary_key");
                row_key.setTextContent("");
                rowRoot.appendChild(row_key);

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_name = doc.createElement("name");
                row_name.setTextContent(metaData.getName());
                rowRoot.appendChild(row_name);

                Element row_de_code = doc.createElement("de_code");
                row_de_code.setTextContent("");
                rowRoot.appendChild(row_de_code);

                Element row_def = doc.createElement("definition");
                row_def.setTextContent(metaData.getDescription());
                rowRoot.appendChild(row_def);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(metaData.getId()));
                rowRoot.appendChild(row_id);

                Element row_colType = doc.createElement("column_type");
                if (metaData.getColumnLength()==null|| metaData.getColumnLength()== 0) {
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

            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
    * 生成字典文件
    * @param listDict 字典列表
    * @param strFilePath 文件保存路径
    * */
    public void CreateDictFile(List<XOrgDict> listDict, String strFilePath) {
        String strFileName = "org_dict.xml";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "org_dict");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"valid|N", "inner_version|S", "ref_standard|S", "code|S", "name|S", "publisher|S", "version|S", "id|N"};
            //创建数据元文件列信息
            root.appendChild(getColumnElement(doc, colRoot, strColumn));

            for (int i = 0; i < listDict.size(); i++) {
                XOrgDict xDict = listDict.get(i);

                Element rowRoot = doc.createElement("row");
                rowRoot.setAttribute("type", "add");

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent("");
                rowRoot.appendChild(row_inner);

                Element row_ref = doc.createElement("ref_standard");
                row_ref.setTextContent("");
                rowRoot.appendChild(row_ref);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(xDict.getCode());
                rowRoot.appendChild(row_code);

                Element row_name = doc.createElement("name");
                row_name.setTextContent(xDict.getName());
                rowRoot.appendChild(row_name);

                Element row_pub = doc.createElement("publisher");
                row_pub.setTextContent("");
                rowRoot.appendChild(row_pub);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(xDict.getId()));
                rowRoot.appendChild(row_id);

                Element row_version = doc.createElement("version");
                row_version.setTextContent("");
                rowRoot.appendChild(row_version);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
    * 生成字典项文件
    * @param listDictEntry 字典项列表
    * @param strFilePath 文件保存路径
    * */
    public void CreateDictEntryFile(List<XOrgDictItem> listDictEntry, String strFilePath) {
        String strFileName = "org_dict_item.xml";
        //创建字典值XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("table");
            root.setAttribute("name", "org_dict_item");
            doc.appendChild(root);

            Element colRoot = doc.createElement("metadata");
            String[] strColumn = {"valid|S", "inner_version|S", "dict_id|N", "code|S", "value|S", "version|S", "id|N"};

            //创建数据元文件列信息
            root.appendChild(getColumnElement(doc, colRoot, strColumn));
            for (int i = 0; i < listDictEntry.size(); i++) {
                XOrgDictItem xDictEntry = listDictEntry.get(i);
                Element rowRoot = doc.createElement("row");
                rowRoot.setAttribute("type", "add");

                Element row_val = doc.createElement("valid");
                row_val.setTextContent("1");
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("inner_version");
                row_inner.setTextContent("");
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("dict_id");
                row_dict.setTextContent(String.valueOf(xDictEntry.getOrgDict()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("code");
                row_code.setTextContent(xDictEntry.getCode());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("value");
                row_value.setTextContent(xDictEntry.getName());
                rowRoot.appendChild(row_value);

                Element row_version = doc.createElement("version");
                row_version.setTextContent("");
                rowRoot.appendChild(row_version);

                Element row_id = doc.createElement("id");
                row_id.setTextContent(String.valueOf(xDictEntry.getId()));
                rowRoot.appendChild(row_id);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
    * 创建适配文件信息
    * @param strAdapterId 适配方案ID
    *
    * */
    public void CreateAdapterFile(String strPlanId, String versionCode, String strFilePath,String strOrgCode) {

        File targetFile = new File(strFilePath);

        //如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        //生成数据集映射文件
        Map mapResult = xAdapterDataSetManager.getDataSetMappingInfo(strPlanId, versionCode,strOrgCode);
        List<DataSetMappingInfo> listDataSetMapping = (List<DataSetMappingInfo>) mapResult.get("datasetlist");
        CreateDataSetMapping(listDataSetMapping, strFilePath);

        //生成数据元映射文件
        List<MetadataMappingInfo> listElementMapping = (List<MetadataMappingInfo>) mapResult.get("elementlist");
        CreateMetaDataMapping(listElementMapping, strFilePath);

        //生成字典映射文件
        Map dictResultMap = xAdapterDictManager.getDictMappingInfo(strPlanId, versionCode, strOrgCode);
        List<DictMappingInfo> listDictMapping = (List<DictMappingInfo>) dictResultMap.get("dictlist");
        CreateDictMapping(listDictMapping, strFilePath);

        //生成字典项映射文件
        List<DictEntryMappingInfo> listDictEntryMapping = (List<DictEntryMappingInfo>) dictResultMap.get("dictentrylist");
        CreateDictEntryMapping(listDictEntryMapping, strFilePath);
    }

    /*
    * 创建数据集映射文件
    * @param listDataSetMapping 数据集映射信息
    * @param strFilePath 文件路径
    * */
    public void CreateDataSetMapping(List<DataSetMappingInfo> listDataSetMapping, String strFilePath) {
        String strFileName = "adapter_dataset.xml";
        //创建字典值XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
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

                Element row_val = doc.createElement("id");
                row_val.setTextContent(xInfo.getId());
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("std_dataset_id");
                row_inner.setTextContent(xInfo.getStdSetId());
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("org_dataset_id");
                row_dict.setTextContent(String.valueOf(xInfo.getOrgSetId()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("std_dataset_code");
                row_code.setTextContent(xInfo.getStdSetCode());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("org_dataset_code");
                row_value.setTextContent(xInfo.getOrgSetCode());
                rowRoot.appendChild(row_value);

                Element row_std_name = doc.createElement("std_dataset_name");
                row_std_name.setTextContent(xInfo.getStdSetName());
                rowRoot.appendChild(row_std_name);

                Element row_sorg_name = doc.createElement("org_dataset_name");
                row_sorg_name.setTextContent(xInfo.getOrgSetName());
                rowRoot.appendChild(row_sorg_name);

                Element row_scheme = doc.createElement("scheme_id");
                row_scheme.setTextContent(xInfo.getPlanId());
                rowRoot.appendChild(row_scheme);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
    * 创建数据元映射文件
    * @param listElementMapping 数据元映射关系列表
    * @Parma strFilePath 文件路径
    *
    * */
    public void CreateMetaDataMapping(List<MetadataMappingInfo> listElementMapping, String strFilePath) {
        String strFileName = "adapter_metadata.xml";
        //创建字典值XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
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

                Element row_val = doc.createElement("id");
                row_val.setTextContent(xInfo.getId());
                rowRoot.appendChild(row_val);

                Element row_mapping = doc.createElement("adapter_dataset_id");
                row_mapping.setTextContent(xInfo.getAdapterDataSetId());
                rowRoot.appendChild(row_mapping);

                Element row_inner = doc.createElement("std_metadata_id");
                row_inner.setTextContent(xInfo.getStdMetadataId());
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("org_metadata_id");
                row_dict.setTextContent(String.valueOf(xInfo.getOrgMetadataId()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("std_metadata_code");
                row_code.setTextContent(xInfo.getStdMetadataCode());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("org_metadata_code");
                row_value.setTextContent(xInfo.getOrgMetadataCode());
                rowRoot.appendChild(row_value);

                Element row_type = doc.createElement("org_dict_data_type");
                row_type.setTextContent(xInfo.getOrgDictDataType());
                rowRoot.appendChild(row_type);

                Element row_std_name = doc.createElement("std_metadata_name");
                row_std_name.setTextContent(xInfo.getStdMetadataName());
                rowRoot.appendChild(row_std_name);

                Element row_org_name = doc.createElement("org_metadata_name");
                row_org_name.setTextContent(xInfo.getOrgMetadataName());
                rowRoot.appendChild(row_org_name);

                Element row_scheme = doc.createElement("scheme_id");
                row_scheme.setTextContent(xInfo.getPlanId());
                rowRoot.appendChild(row_scheme);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
   * 创建字典映射文件
   * @param listInfo 字典映射关系列表
   * @Parma strFilePath 文件路径
   *
   * */
    public void CreateDictMapping(List<DictMappingInfo> listInfo, String strFilePath) {
        String strFileName = "adapter_dict.xml";
        //创建字典值XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
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

                Element row_val = doc.createElement("id");
                row_val.setTextContent(xInfo.getId());
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("std_dict_id");
                row_inner.setTextContent(xInfo.getStdDictId());
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("org_dict_id");
                row_dict.setTextContent(String.valueOf(xInfo.getOrgDictId()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("std_dict_code");
                row_code.setTextContent(xInfo.getStdDictCode());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("org_dict_code");
                row_value.setTextContent(xInfo.getOrgDictCode());
                rowRoot.appendChild(row_value);

                Element row_std_name = doc.createElement("std_dict_name");
                row_std_name.setTextContent(xInfo.getStdDictName());
                rowRoot.appendChild(row_std_name);

                Element row_org_name = doc.createElement("org_dict_name");
                row_org_name.setTextContent(xInfo.getOrgDictName());
                rowRoot.appendChild(row_org_name);

                Element row_scheme = doc.createElement("scheme_id");
                row_scheme.setTextContent(xInfo.getPlanId());
                rowRoot.appendChild(row_scheme);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }


    /*
    * 创建字典项映射文件
    * @param listInfo 字典项映射关系列表
    * @Parma strFilePath 文件路径
    *
    * */
    public void CreateDictEntryMapping(List<DictEntryMappingInfo> listInfo, String strFilePath) {
        String strFileName = "adapter_dict_item.xml";
        //创建字典值XML
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
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

                Element row_val = doc.createElement("id");
                row_val.setTextContent(xInfo.getId());
                rowRoot.appendChild(row_val);

                Element row_inner = doc.createElement("adapter_dict_id");
                row_inner.setTextContent(xInfo.getAdapterDictId());
                rowRoot.appendChild(row_inner);

                Element row_dict = doc.createElement("std_dict_item_id");
                row_dict.setTextContent(String.valueOf(xInfo.getStdDictEntryId()));
                rowRoot.appendChild(row_dict);

                Element row_code = doc.createElement("org_dict_item_id");
                row_code.setTextContent(xInfo.getOrgDictEntryId());
                rowRoot.appendChild(row_code);

                Element row_value = doc.createElement("std_dict_item_code");
                row_value.setTextContent(xInfo.getStdDictEntryCode());
                rowRoot.appendChild(row_value);

                Element row_std_value = doc.createElement("std_dict_item_value");
                row_std_value.setTextContent(xInfo.getStdDictEntryValue());
                rowRoot.appendChild(row_std_value);

                Element row_org_code = doc.createElement("org_dict_item_code");
                row_org_code.setTextContent(xInfo.getOrgDictEntryCode());
                rowRoot.appendChild(row_org_code);

                Element row_org_value = doc.createElement("org_dict_item_value");
                row_org_value.setTextContent(xInfo.getOrgDictEntryValue());
                rowRoot.appendChild(row_org_value);

                Element row_scheme = doc.createElement("scheme_id");
                row_scheme.setTextContent(xInfo.getPlanId());
                rowRoot.appendChild(row_scheme);

                root.appendChild(rowRoot);
            }
            outputXml(doc, strFilePath + strFileName);
        } catch (Exception ex) {
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
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

    private void outputXml(Document doc, String fileName) {
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
            LogService.getLogger(AdapterInfoSendManager.class).error(ex.getMessage());
        }
    }

    /*
    * 获取采集标准和适配方案信息
    * @param versionCode 版本号
    * @param strOrgCode 机构编号
    *@return Map<String, Object> password:压缩密码 IsSuccess：是否成功  ErrorMsg：错误消息 groupName:
    * */
    public Map<String, Object> GetStandardAndMappingInfo(String versionCode, String strOrgCode) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            String strFilePath = getFileSystemPath();

            String splitMark = System.getProperty("file.separator");
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
            Map<String, Object> standardMap = stdSendManager.SendStandard(versionCode);

            if (standardMap == null) {

                standardMap.put("ErrorMsg", "未找到采集标准!");
                standardMap.put("IsSuccess", "false");
                return standardMap;
            }

            String group = (String) standardMap.get(FastDFSUtil.GroupField);
            String remoteFile = (String) standardMap.get(FastDFSUtil.RemoteFileField);
            String password = (String) standardMap.get("password");

            String strLocalFileName = FastDFSUtil.download(group, remoteFile, strFilePath + splitMark + "standardadapter" + splitMark + "xml" + splitMark);
            //将采集标准文件解压到适配方案路径下
            Zipper zipper = new Zipper();
            File standardFile = new File(strLocalFileName);
            String ss =standardFile.getName();
            File standardFileXml= zipper.unzipFile(standardFile,strXMLFilePath,password);

//            File file_standard = new File(strXMLFilePath+"standard_file"+splitMark);
//            if(!file_standard.exists())
//            {
//                file_standard.mkdirs();
//            }
//            standardFileXml.renameTo(file_standard);
            //生成适配映射方案信息
            String strErrorMsg = CreateAllMappingFile(versionCode,strOrgCode,strXMLFilePath,splitMark);
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
            ObjectNode msg = FastDFSUtil.upload(strZIPFilePath + strFileName, "");

            resultMap.put(FastDFSUtil.GroupField, msg.get(FastDFSUtil.GroupField).asText());//setFilePath
            resultMap.put(FastDFSUtil.RemoteFileField, msg.get(FastDFSUtil.RemoteFileField).asText());//setFileName
            resultMap.put("password", strPwd);
            resultMap.put("ErrorMsg", "");
            resultMap.put("IsSuccess", "true");

            XDispatchLog logInfo = new DispatchLog();
            XEnvironmentOption environmentOption = ServiceFactory.getService(Services.EnvironmentOption);
            Object objectID = new ObjectId(Short.parseShort(environmentOption.getOption(EnvironmentOptions.AdminRegion)), BizObject.StdArchive);

            logInfo.setId(objectID.toString());
            logInfo.setOrg_id(strOrgCode);
            logInfo.setStd_version_id(versionCode);
            logInfo.setDispatch_time(new Date());
            logInfo.setFile_group(msg.get(FastDFSUtil.GroupField).asText());
            logInfo.setFile_path(msg.get(FastDFSUtil.RemoteFileField).asText());
            xDispatchLogManager.insertDispatchLog(logInfo);

        } catch (Exception ex) {

        }

        return resultMap;
    }
}
