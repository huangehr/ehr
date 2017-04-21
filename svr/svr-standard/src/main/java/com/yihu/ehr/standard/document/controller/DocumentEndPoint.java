package com.yihu.ehr.standard.document.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSet;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.standard.commons.ExtendEndPoint;
import com.yihu.ehr.standard.document.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "cda document", description = "CDA文档服务")
public class DocumentEndPoint extends ExtendEndPoint<MCDADocument> {

    @Autowired
    private CDADocumentService cdaDocumentService;

    @Autowired
    private CDADataSetRelationshipManager cdaDatasetRelationshipManager;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    private Class getServiceEntity(String version) {
        return cdaDocumentService.getServiceEntity(version);
    }

    private Class getRelationshipServiceEntity(String version) {
        return cdaDatasetRelationshipManager.getRelationshipServiceEntity(version);
    }

    @RequestMapping(value = ServiceApi.Standards.Documents, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda文档列表")
    public Collection<MCDADocument> GetCDADocuments(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) {
        Class entityClass = getServiceEntity(version);

        List ls = cdaDocumentService.search(entityClass, fields, filters, sorts, page, size);

        pagedResponse(request, response, cdaDocumentService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<>(ls.size()), MCDADocument.class, fields);
    }

    @ApiOperation(value = "获取CDADocuments")
    @RequestMapping(value = ServiceApi.Standards.Document, method = RequestMethod.GET)
    public MCDADocument getCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号")
            @PathVariable(value = "id") String id
    ) throws Exception {

        Class entityClass = getServiceEntity(version);
        if(cdaDocumentService.retrieve(id, entityClass)==null)
            throw  errNotFound("cda文档", id);

        return getModel(cdaDocumentService.retrieve(id, entityClass));
    }


    @RequestMapping(value = ServiceApi.Standards.DocumentList, method = RequestMethod.GET)
    public Map<String,MCDADocument> getCDADocumentsList(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "idList") String[] idList
    ) throws Exception {

        Class entityClass = getServiceEntity(version);
        Map<String,MCDADocument>map=new HashMap<>();
        for(int i=0;i<idList.length;i++) {
            if (cdaDocumentService.retrieve(idList[i], entityClass) == null)
                throw errNotFound("cda文档", idList[i]);

            map.put(idList[i], getModel(cdaDocumentService.retrieve(idList[i], entityClass)));
        }
        return map;
    }


    //todo： 动态实体改版完后   归并到GetCDADocuments方法中
    @RequestMapping(value = "/std/CDADocuments/ids", method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    @Deprecated
    public Collection<MCDADocument> getCDADocumentByIds(
            @ApiParam(name = "ids", value = "版本号")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String version) {

        Class entityClass = getServiceEntity(version);
        List<CDADocument> list = cdaDocumentService.search(entityClass, "id=" + ids);
        return convertToModels(list, new ArrayList<>(list.size()), MCDADocument.class, "");
    }


    @ApiOperation(value = "新增CDADocuments")
    @RequestMapping(value = ServiceApi.Standards.Documents, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MCDADocument saveCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestBody String model) throws Exception {

        Class entityClass = getServiceEntity(version);
        BaseCDADocument cdaDocument = (BaseCDADocument) toEntity(model, entityClass);
        cdaDocument.setId(getObjectId(BizObject.STANDARD));
        cdaDocument.setCreateDate(new Date());
        cdaDocumentService.saveCdaDocument(cdaDocument);
        return getModel(cdaDocument);
    }


    @ApiOperation(value = "修改CDADocuments")
    @RequestMapping(value = ServiceApi.Standards.Document, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MCDADocument updateCDADocuments(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "model", value = "文档json数据模型")
            @RequestBody String model
            ) throws Exception {

        Class entityClass = getServiceEntity(version);
        if(cdaDocumentService.retrieve(id, entityClass)==null)
            throw  errNotFound("cda文档", id);

        BaseCDADocument cdaDocument = (BaseCDADocument) toEntity(model, entityClass);
        cdaDocument.setId(id);
        cdaDocument.setUpdateDate(new Date());
        cdaDocumentService.save(cdaDocument);
        return getModel(cdaDocument);
    }

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = ServiceApi.Standards.Documents, method = RequestMethod.DELETE)
    public boolean deleteCDADocuments(
            @ApiParam(name = "ids", value = "文档编号集")
            @RequestParam(value = "ids") String[] ids,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode) {

        cdaDocumentService.deleteDocument(ids, versionCode);
        return true;
    }

    /***********************************************************************************************************/
    /************** document data set relationship   ***********************************************************/
    /***********************************************************************************************************/
    @RequestMapping(value = ServiceApi.Standards.DataSetRelationships, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    public Collection<MCdaDataSetRelationship> getCDADataSetRelationships(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) {
        Class entityClass = getRelationshipServiceEntity(version);
        List relations = cdaDatasetRelationshipManager.getCDADataSetRelationships(entityClass, fields, filters, sorts, size, page);
        pagedResponse(request, response, cdaDocumentService.getCount(entityClass, filters), page, size);
        return convertToModels(relations, new ArrayList<>(relations.size()), MCdaDataSetRelationship.class, fields);

    }

    @RequestMapping(value = "/std/cda_data_set_relationships/cda_id", method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    public Collection<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "document_Id", value = "文档编号")
            @RequestParam(value = "document_Id") String cdaId) {
        Class entityClass = getRelationshipServiceEntity(version);
        List relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(entityClass, cdaId);
        return convertToModels(relations, new ArrayList<>(relations.size()), MCdaDataSetRelationship.class, "");
    }


    @RequestMapping(value = ServiceApi.Standards.DocumentDataSet, method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取cda和数据集关系列表（包含数据集code）")
    public Collection<MCdaDataSet> getCDADataSetByCDAId(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "document_Id", value = "文档编号")
            @RequestParam(value = "document_Id") String cdaId) {
        List<MCdaDataSet> list =  cdaDatasetRelationshipManager.getCDADataSetByCDAId(version, cdaId);
        return list;
    }

    @RequestMapping(value = ServiceApi.Standards.DocumentDataSetList, method = RequestMethod.GET)
    Map<String,List<MCdaDataSet>> getCDADataSetByCDAIdList(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "document_Id") String[] cdaDocumentId){
        Map<String,List<MCdaDataSet>> map=new HashMap<>();
        for(int i=0;i<cdaDocumentId.length;i++){
            map.put(cdaDocumentId[i],cdaDatasetRelationshipManager.getCDADataSetByCDAId(version, cdaDocumentId[i]));
        }
        return map;
    }

    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，再新增信息
     *
     * @param dataSetIds  关联的数据集
     * @param cdaId       cda文档 ID
     * @param versionCode 版本号
     * @param xmlInfo     xml 文件内容
     * @return 操作结果
     */
    @ApiOperation(value = "保存CDADataSetRelationship")
    @RequestMapping(value = ServiceApi.Standards.DataSetRelationships, method = RequestMethod.POST)
    public boolean saveCDADataSetRelationship(
            @ApiParam(name = "data_set_ids", value = "data_set_ids")
            @RequestParam(value = "data_set_ids") String[] dataSetIds,
            @ApiParam(name = "document_Id", value = "文档编号")
            @RequestParam(value = "document_Id") String cdaId,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "xml_info", value = "xml_info")
            @RequestParam(value = "xml_info") String xmlInfo) throws Exception {
        return cdaDatasetRelationshipManager.SaveDataSetRelationship(dataSetIds, cdaId, versionCode, xmlInfo);
    }

    @ApiOperation(value = "根基id删除CDADataSetRelationship")
    @RequestMapping(value = ServiceApi.Standards.DataSetRelationships, method = RequestMethod.DELETE)
    public boolean deleteCDADataSetRelationship(
            @ApiParam(name = "version", value = "versionCode")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "ids", value = "DataSetRelationship编号集")
            @RequestParam(value = "ids") String[] ids) {

        return cdaDatasetRelationshipManager.deleteRelationshipById(versionCode, ids);
    }

    /***********************************************************************************************************/
    /************** document file  ****************************************************************************/
    /***********************************************************************************************************/

    @RequestMapping(value = ServiceApi.Standards.DocumentFileExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断文件是否存在")
    public boolean FileExists(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId) {

        return cdaDocumentService.isFileExists(cdaId, versionCode);
    }

    @RequestMapping(value = ServiceApi.Standards.DocumentCreateFile, method = RequestMethod.POST)
    @ApiOperation(value = "生成CDA文件")
    public boolean createCDASchemaFile(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId) throws Exception {

        return cdaDocumentService.createCDASchemaFile(cdaId, versionCode);
    }

    /**
     * 获取cda文档的XML文件信息。
     * <p/>
     * 从服务器的临时文件路径中读取配置文件，并以XML形式返回。
     *
     * @param cdaId
     * @param versionCode
     * @return XML信息
     * @version 1.0.1 将临时目录转移至fastDFS。
     */
    @RequestMapping(value = ServiceApi.Standards.DocumentGetFile, method = RequestMethod.GET)
    @ApiOperation(value = "获取cda文档的XML文件信息。")
    public String getCdaXmlFileInfo(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version") String versionCode,
            @ApiParam(name = "id", value = "文档编号")
            @PathVariable(value = "id") String cdaId) throws Exception {

        String strXmlInfo = "";
        String strFileGroup = "";
        String strSchemePath = "";

        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";

        String splitMark = System.getProperty("file.separator");
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "downfile" + splitMark;

        List<String> listIds = new ArrayList<>();
        listIds.add(cdaId);
        Class entityClass = getServiceEntity(versionCode);
        BaseCDADocument cdaDocument = cdaDocumentService.retrieve(cdaId, entityClass);


        if (cdaDocument != null) {
            strFileGroup = cdaDocument.getFileGroup();
            strSchemePath = cdaDocument.getSchema();
        } else {
            return "";
        }
        File files = new File(strXMLFilePath);
        if (!files.exists()) {
            files.mkdirs();
        }

        String strLocalFileName = strXMLFilePath + "\\" + strSchemePath.replaceAll("/", "_");
        File localFile = new File(strLocalFileName);
        if (localFile.exists() && localFile.isFile()) {
            localFile.delete();
        }
        if (!strFileGroup.equals("") && !strSchemePath.equals("")) {
            strLocalFileName = fastDFSUtil.download(strFileGroup, strSchemePath, strXMLFilePath);

            File file = new File(strLocalFileName);
            FileReader fr = new FileReader(file);
            BufferedReader bReader = new BufferedReader(fr);
            strXmlInfo = bReader.readLine();
        } else {
            strXmlInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root></root>";
        }
        return strXmlInfo;
    }
}
