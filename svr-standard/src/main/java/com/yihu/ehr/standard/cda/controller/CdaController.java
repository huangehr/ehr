package com.yihu.ehr.standard.cda.controller;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.cda.service.*;
import com.yihu.ehr.standard.datasets.service.DataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping(ApiVersion.Version1_0)
@RestController
@Api(protocols = "https", value = "cda", description = "cda管理", tags = {"cda管理"})
public class CdaController extends BaseRestController{

    @Autowired
    private CDADocumentManager cdaDocumentManager;
    @Autowired
    private CDAManager cdaManager;
    @Autowired
    private CdaDatasetRelationshipManager cdaDatasetRelationshipManager;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    @RequestMapping(value = "/CDADocuments" ,method = RequestMethod.GET)
    @ApiOperation(value = "根据条件获取cda列表")
    public Object GetCDADocuments(String strVersion,String code,String name,String type,Integer page,Integer rows) {
        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(strVersion,code,name,type,page,rows);
        int resultCount = cdaDocumentManager.getDocumentCount(strVersion,code,name,type);
        return getResult(xcdaDocuments, resultCount);
    }

    @RequestMapping("CDADocuments/ids")
    @ResponseBody
    public List<CDADocument> getCDAInfoById(String[] ids, String strVersion) {
        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(strVersion, ids);
        return xcdaDocuments;
    }


    @RequestMapping("cda_data_set_relationships")
    @ResponseBody
    public Envelop cdaDatasetRelationships(
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


    @RequestMapping(value = "CDADocuments",method = RequestMethod.POST)
    public Object SaveCdaInfo(String cdaDocumentJsonData) throws Exception {
        return cdaManager.SaveCdaInfo(cdaDocumentJsonData);
    }

    @RequestMapping(value = "CDADocuments",method = RequestMethod.DELETE)
    public boolean deleteCdaInfo(String versionCode,String[] ids) {
        cdaDocumentManager.deleteDocument(versionCode,ids);
        return true;
    }

    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，在新增信息
     * @param dataSetIds 关联的数据集
     * @param cdaId  cda文档 ID
     * @param versionCode 版本号
     * @param xmlInfo xml 文件内容
     * @return 操作结果
     */
    @RequestMapping("SaveDataSetRelationship")
    @ResponseBody
    public boolean SaveDataSetRelationship(String[] dataSetIds,String cdaId,String versionCode,String xmlInfo) throws Exception {
        return cdaManager.SaveDataSetRelationship(dataSetIds,cdaId,versionCode, xmlInfo);
    }

    @RequestMapping("DeleteDatasetRelationship")
    @ResponseBody
    public Object DeleteDatasetRelationship(String ids, String versionCode) {
        List<String> relationIds = Arrays.asList(ids.split(","));
        int iResult = cdaDatasetRelationshipManager.deleteRelationshipById(versionCode, relationIds);
        if (iResult >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
    * 判断文件是否存在*/
    @RequestMapping("/FileExists")
    @ResponseBody
    public String FileExists(String cdaId, String versionCode) {
        //1：已存在文件
        return cdaDocumentManager.isFileExists(cdaId, versionCode)
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
     * 根据cdaId获取cda和dataSet关系
     * @param cdaId
     * @return
     */
    @RequestMapping("/getDatasetByCdaId")
    @ResponseBody
    public List<CdaDataSetRelationship> getCdaDatasetRelationship(String cdaId) {
        List<CdaDataSetRelationship> relations = cdaDatasetRelationshipManager.getCDADataSetRelationshipByCDAId(cdaId);
        return relations;
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

        List<CDADocument> xcdaDocuments = cdaDocumentManager.getDocumentList(versionCode, listIds);
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
