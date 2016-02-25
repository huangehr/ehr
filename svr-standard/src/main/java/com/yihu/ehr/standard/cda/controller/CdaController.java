package com.yihu.ehr.standard.cda.controller;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.cda.service.*;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
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
    public Object GetCDADocuments(String strVersion,String code,String name,String type,Integer page,Integer rows) {
        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(strVersion,code,name,type,page,rows);
        int resultCount = cdaDocumentManager.getDocumentCount(strVersion,code,name,type);
        return getResult(xcdaDocuments, resultCount);
    }

    @RequestMapping(value = "/CDADocuments/ids" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取cda列表")
    public List<CDADocument> getCDADocumentById(String[] ids, String strVersion) {
        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(ids,strVersion);
        return xcdaDocuments;
    }


    @RequestMapping(value = "/cda_data_set_relationships" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取getCDADataSetRelationship列表")
    public Envelop getCDADataSetRelationships(
            @ApiParam(required = true, name = "cda_Id", value = "用户名")
            @RequestParam(value = "cda_Id", required = true) String cdaId,
            @ApiParam(required = true, name = "version_code", value = "用户密码，以RSA加密")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        List<CdaDataSetRelationship> relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(cdaId,versionCode,page,rows);
        int resultCount = cdaDatasetRelationshipManager.getRelationshipCountByCdaId(cdaId,versionCode);
        return getResult(relations,resultCount);
    }


    @ApiOperation(value = "保存CDADocuments")
    @RequestMapping(value = "/cda_documents",method = RequestMethod.POST)
    public Object saveCdaInfo(String cdaDocumentJsonData) throws Exception {
        return cdaManager.saveCdaInfo(cdaDocumentJsonData);
    }

    @ApiOperation(value = "删除CDADocuments")
    @RequestMapping(value = "cda_documents",method = RequestMethod.DELETE)
    public boolean deleteCdaInfo(String[] ids,String versionCode) {
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
    public boolean saveCDADataSetRelationship(String[] dataSetIds,String cdaId,String versionCode,String xmlInfo) throws Exception {
        return cdaManager.SaveDataSetRelationship(dataSetIds,cdaId,versionCode, xmlInfo);
    }

    @ApiOperation(value = "删除CDADataSetRelationship")
    @RequestMapping(value = "/cda_data_set_relationships",method = RequestMethod.DELETE)
    public Object deleteCDADataSetRelationship(String[] relationIds, String versionCode) {
        return cdaDatasetRelationshipManager.deleteRelationshipById(versionCode, relationIds);
    }


    /**
     * 根据cdaId获取cda和dataSet关系
     * @param cdaId
     * @return
     */
    @RequestMapping(value = "/cda_data_set_relationships/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cda_id获取getCDADataSetRelationship列表")
    public List<CdaDataSetRelationship> getCDADataSetRelationshipByCDAId(String cdaId, String versionCode) {
        List<CdaDataSetRelationship> relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(cdaId,versionCode,0,0);
        return relations;
    }


    /*
    * 判断文件是否存在*/
    @RequestMapping(value = "/file/existence/cda_id" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据cdaId和versionCode判断文件是否存在")
    public boolean FileExists(String cdaId, String versionCode) {
        return cdaDocumentManager.isFileExists(cdaId, versionCode);
    }

    /**
     * 生成CDA文件
     *
     * @param strCdaId
     * @param versionCode
     * @return
     */
    @RequestMapping("/createCDASchemaFile")
    @ResponseBody
    public boolean createCDASchemaFile(String strCdaId, String versionCode) throws Exception {
        return cdaDocumentManager.createCDASchemaFile(strCdaId, versionCode);
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
