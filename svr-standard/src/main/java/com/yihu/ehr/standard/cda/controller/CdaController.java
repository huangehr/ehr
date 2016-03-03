package com.yihu.ehr.standard.cda.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.standard.cda.service.*;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping(ApiVersion.Version1_0+"/std")
@RestController
@Api(protocols = "https", value = "cda", description = "cda管理", tags = {"cda管理"})
public class CdaController extends BaseRestController{

    @Autowired
    private CDADocumentManager cdaDocumentManager;
    @Autowired
    private CDAManager cdaManager;
    @Autowired
    private CdaDataSetRelationshipManager cdaDatasetRelationshipManager;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @RequestMapping(value = "/CDADocuments" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    public List<MCDADocument> GetCDADocuments(
            @ApiParam(name = "version_code", value = "版本号")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "code", value = "code")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "type")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<CDADocument> cdaDocuments = cdaDocumentManager.getDocumentList(versionCode,code,name,type,page,rows);
        int resultCount = cdaDocumentManager.getDocumentCount(versionCode,code,name,type);
        pagedResponse(request, response, (long) resultCount, page, rows);
        return (List<MCDADocument>)convertToModels(cdaDocuments, new ArrayList<MCDADocument>(cdaDocuments.size()), MCDADocument.class, "");
    }

    @RequestMapping(value = "/CDADocuments/ids" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    public List<MCDADocument> getCDADocumentById(
            @ApiParam(name = "ids", value = "版本号")
            @RequestParam(value = "ids") String[] ids,
            @ApiParam(name = "version_code", value = "version_code")
            @RequestParam(value = "version_code") String version) {
        List<CDADocument> cdaDocuments = cdaDocumentManager.getDocumentList(ids,version);
        List<MCDADocument> documentModels = (List<MCDADocument>)convertToModels(cdaDocuments, new ArrayList<MCDADocument>(cdaDocuments.size()), MCDADocument.class, "");
        return documentModels;
    }


    @RequestMapping(value = "/cda_data_set_relationships" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    public List<MCdaDataSetRelationship> getCDADataSetRelationships(
            @ApiParam(name = "cda_Id", value = "cda_Id")
            @RequestParam(value = "cda_Id") String cdaId,
            @ApiParam(name = "version_code", value = "版本号")
            @RequestParam(value = "version_code") String versionCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows,
            HttpServletRequest request,
            HttpServletResponse response) {
        List<CdaDataSetRelationship> relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(cdaId,versionCode,page,rows);
        int resultCount = cdaDatasetRelationshipManager.getRelationshipCountByCdaId(cdaId,versionCode);
        pagedResponse(request, response, (long) resultCount, page, rows);
        return (List<MCdaDataSetRelationship>)convertToModels(relations, new ArrayList<MCdaDataSetRelationship>(relations.size()), MCdaDataSetRelationship.class, "");
    }


    @ApiOperation(value = "保存CDADocuments")
    @RequestMapping(value = "/cda_documents",method = RequestMethod.POST)
    public MCDADocument saveCDADocuments(
            @ApiParam(name = "json_date", value = "json_date")
            @RequestParam(value = "json_date") String cdaDocumentJsonData) throws Exception {
        CDADocument cdaDocument = new ObjectMapper().readValue(cdaDocumentJsonData, CDADocument.class);
        cdaDocument.setId(getObjectId(BizObject.STANDARD));
        cdaDocument.setCreateDate(new Date());
        cdaDocumentManager.saveDocument(cdaDocument);
        return convertToModel(cdaDocument,MCDADocument.class);
    }


    @ApiOperation(value = "修改CDADocuments")
    @RequestMapping(value = "/cda_documents",method = RequestMethod.PUT)
    public MCDADocument updateCDADocuments(
            @ApiParam(name = "json_date", value = "json_date")
            @RequestParam(value = "json_date") String cdaDocumentJsonData) throws Exception {
        CDADocument cdaDocument = new ObjectMapper().readValue(cdaDocumentJsonData, CDADocument.class);
        cdaDocument.setUpdateDate(new Date());
        cdaDocumentManager.saveDocument(cdaDocument);
        return convertToModel(cdaDocument,MCDADocument.class);
    }

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = "cda_documents",method = RequestMethod.DELETE)
    public boolean deleteCDADocuments(
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String[] ids,
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode) {
        cdaDocumentManager.deleteDocument(ids,versionCode);
        return true;
    }

    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，再新增信息
     * @param dataSetIds 关联的数据集
     * @param cdaId  cda文档 ID
     * @param versionCode 版本号
     * @param xmlInfo xml 文件内容
     * @return 操作结果
     */
    @ApiOperation(value = "保存CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.POST)
    public boolean saveCDADataSetRelationship(
            @ApiParam(name = "data_set_ids", value = "data_set_ids")
            @RequestParam(value = "data_set_ids") String[] dataSetIds,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId,
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "xml_info", value = "xml_info")
            @RequestParam(value = "xml_info") String xmlInfo) throws Exception {
        return cdaManager.SaveDataSetRelationship(dataSetIds,cdaId,versionCode, xmlInfo);
    }

    @ApiOperation(value = "根基id删除CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.DELETE)
    public boolean deleteCDADataSetRelationship(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "ids", value = "ids")
            @RequestParam(value = "ids") String[] ids) {
        return cdaDatasetRelationshipManager.deleteRelationshipById(versionCode, ids);
    }


    /**
     * 根据cdaId获取cda和dataSet关系
     * @param cdaId
     * @return
     */
    @RequestMapping(value = "/cda_data_set_relationships/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    public List<MCdaDataSetRelationship> getCDADataSetRelationshipByCDAId(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId) {
        List<CdaDataSetRelationship> relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(versionCode,cdaId,0,0);
        return (List<MCdaDataSetRelationship>)convertToModels(relations,new ArrayList<MCdaDataSetRelationship>(relations.size()),MCdaDataSetRelationship.class,"");
    }


    /*
    * 判断文件是否存在*/
    @RequestMapping(value = "/file/existence/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    public boolean FileExists(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId) {
        return cdaDocumentManager.isFileExists(cdaId, versionCode);
    }

    /**
     * 生成CDA文件
     *
     * @param cdaId
     * @param versionCode
     * @return
     */
    @RequestMapping("/createCDASchemaFile")
    @ResponseBody
    public boolean createCDASchemaFile(
            @ApiParam(name = "versionCode", value = "versionCode")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "cda_id", value = "cda_id")
            @RequestParam(value = "cda_id") String cdaId) throws Exception {
        return cdaDocumentManager.createCDASchemaFile(cdaId, versionCode);
    }

    /**
     * 获取cda文档的XML文件信息。
     * <p>
     * 从服务器的临时文件路径中读取配置文件，并以XML形式返回。
     *
     * @param cdaId
     * @param versionCode
     * @return XML信息
     * @version 1.0.1 将临时目录转移至fastDFS。
     */
    @RequestMapping("/getCdaXmlFileInfo")
    @ResponseBody
    public Object getCdaXmlFileInfo(String cdaId, String versionCode) throws Exception {
        String strXmlInfo = "";
        String strPath = System.getProperty("java.io.tmpdir");
        strPath += "StandardFiles";

        String splitMark = System.getProperty("file.separator");
        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "downfile" + splitMark;

        List<String> listIds = new ArrayList<>();
        listIds.add(cdaId);

        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(new String[]{cdaId},versionCode);
        String strFileGroup = "";
        String strSchemePath = "";
        if (xcdaDocuments.size() > 0) {
            strFileGroup = xcdaDocuments.get(0).getFileGroup();
            strSchemePath = xcdaDocuments.get(0).getSchema();
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
