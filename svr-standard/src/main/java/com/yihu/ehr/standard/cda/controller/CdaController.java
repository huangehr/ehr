package com.yihu.ehr.standard.cda.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.standard.cda.service.CDAForInterface;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cda")
@Api(protocols = "https", value = "cda", description = "cda", tags = {"cda"})
public class CdaController{

//    @Resource(name = Services.CDADocumentManager)
//    private XCDADocumentManager xcdaDocumentManager;
//
//    @Resource(name = Services.DataSetRelationshipManager)
//    private XCdaDatasetRelationshipManager xCdaDatasetRelationshipManager;
//
//    @Resource(name = Services.CDATypeManager)
//    private XCDATypeManager xcdaTypeManager;
//
//    @Resource(name = Services.DataSetManager)
//    private XDataSetManager xDataSetManager;

//    private static   String host = "http://"+ ResourceProperties.getProperty("serverip")+":"+ResourceProperties.getProperty("port");
//    private static   String username = ResourceProperties.getProperty("username");
//    private static   String password = ResourceProperties.getProperty("password");


    @RequestMapping("GetCdaListByKey")
    @ApiOperation(value = "查询cda列表")
    public Object GetCdaListByKey(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strKey", value = "搜索值", defaultValue = "")
            @RequestParam(value = "strKey") String strKey,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion,
            @ApiParam(name = "strType", value = "类型", defaultValue = "")
            @RequestParam(value = "strType") String strType,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows ) {
        Result result = new Result();
//
//        if (strVersion == null) {
//            result.setSuccessFlg(false);
//            result.setErrorMsg("请选择标准版本!");
//            return result;
//        }
//        try {
//            XCDADocument[] xcdaDocuments = xcdaDocumentManager.getDocumentList(strVersion, strKey, strType, page, rows);
//
//            if (xcdaDocuments == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(ErrorCode.GetCDAInfoFailed.toString());
//                return result;
//            }
//
//            List<CDAForInterface> resultInfos = GetCDAForInterface(xcdaDocuments);
//
//            int resultCount = xcdaDocumentManager.getDocumentCount(strVersion, strKey,strType);
//            if (rows == 0)
//                rows = 1;
//            result = getResult(resultInfos, resultCount, page, rows);
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.GetCDAInfoFailed.toString());
//        }
        return result.toJson();
    }

    @RequestMapping("getCDAInfoById")
    @ApiOperation(value = "通过编号获取cda")
    public Object getCDAInfoById(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strId", value = "编号", defaultValue = "")
            @RequestParam(value = "strId") String strId,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {
        Result result = new Result();

//        try {
//            String strErrorMsg = "";
//
//            if (strVersion == null || strVersion == "") {
//                strErrorMsg += "请选择标准版本!";
//            }
//
//            if (strId == null || strId == "") {
//                strErrorMsg += "请选择将要编辑的CDA!";
//            }
//
//            if (strErrorMsg != "") {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//                return result;
//            }
//
//            List<String> listId = Arrays.asList(strId.split(","));
//            XCDADocument[] xcdaDocuments = xcdaDocumentManager.getDocumentList(strVersion, listId);
//
//            if (xcdaDocuments == null) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(ErrorCode.GetCDAInfoFailed.toString());
//                return result;
//            }
//
//            List<CDAForInterface> resultInfos = GetCDAForInterface(xcdaDocuments);
//
//            result.setSuccessFlg(true);
//            result.setObj(resultInfos.get(0));
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.GetCDAInfoFailed.toString());
//        }
        return result;
    }

//    public List<CDAForInterface> GetCDAForInterface(XCDADocument[] xcdaDocuments) {
//        List<CDAForInterface> infos = new ArrayList<>();
//        for (XCDADocument xcdaDocument : xcdaDocuments) {
//            CDAForInterface info = new CDAForInterface();
//            info.setId(xcdaDocument.getId());
//            info.setCode(xcdaDocument.getCode());
//            info.setName(xcdaDocument.getName());
//            info.setDescription(xcdaDocument.getDescription());
//            info.setPrintOut(xcdaDocument.getPrintOut());
//            info.setSourceId(xcdaDocument.getSourceId());
//            info.setSchema(xcdaDocument.getSchema());
//            info.setVersionCode(xcdaDocument.getVersionCode());
//            info.setTypeId(xcdaDocument.getTypeId());
//            infos.add(info);
//        }
//        return infos;
//    }

    @RequestMapping("getRelationByCdaId")
    @ApiOperation(value = "通过编号获取关联列表")
    public Object getRelationByCdaId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaId", value = "编号", defaultValue = "")
            @RequestParam(value = "cdaId") String cdaId,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode,
            @ApiParam(name = "strkey", value = "查询值", defaultValue = "")
            @RequestParam(value = "strkey") String strkey,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        Result result = new Result();

//        try {
//            List<CdaDatasetRelationshipForInterface> listResult = new ArrayList<>();
//            XCdaDatasetRelationship[] relations = xCdaDatasetRelationshipManager.getRelationshipByCdaId(cdaId, strVersionCode, strkey, page, rows);
//            if (relations != null) {
//                for (XCdaDatasetRelationship info : relations) {
//                    CdaDatasetRelationshipForInterface _res = new CdaDatasetRelationshipForInterface();
//                    _res.setId(info.getId());
//                    _res.setDatasetId(info.getDataSetId());
//                    _res.setDataset_code(info.getDataSetCode());
//                    _res.setDataset_name(info.getDataSetName());
//                    _res.setSummary(info.getSummary());
//
//                    listResult.add(_res);
//                }
//            }
//            int resultCount = xCdaDatasetRelationshipManager.getRelationshipCountByCdaId(cdaId, strVersionCode, strkey);
//            result = getResult(listResult, resultCount, page, rows);
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//            result.setSuccessFlg(false);
//            result.setErrorMsg("关系获取失败!");
//        }
        return result;
    }

    @RequestMapping("getALLRelationByCdaId")
    @ApiOperation(value = "通过编号获取所有关联")
    public Object getALLRelationByCdaId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaId", value = "编号", defaultValue = "")
            @RequestParam(value = "cdaId") String cdaId,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();

//        try {
//            List<CdaDatasetRelationshipForInterface> listResult = new ArrayList<>();
//            XCdaDatasetRelationship[] relations = xCdaDatasetRelationshipManager.getRelationshipByCdaId(cdaId, strVersionCode);
//            if (relations != null) {
//                for (XCdaDatasetRelationship info : relations) {
//                    CdaDatasetRelationshipForInterface _res = new CdaDatasetRelationshipForInterface();
//                    _res.setId(info.getId());
//                    _res.setDatasetId(info.getDataSetId());
//                    _res.setDataset_code(info.getDataSetCode());
//                    _res.setDataset_name(info.getDataSetName());
//                    _res.setSummary(info.getSummary());
//
//                    listResult.add(_res);
//                }
//            }
//
//            result = getResult(listResult, 1, 1, 1);
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//            result.setSuccessFlg(false);
//            result.setErrorMsg("关系获取失败!");
//        }
        return result;
    }

    @RequestMapping("SaveCdaInfo")
    @ApiOperation(value = "保存cda信息")
    public Object SaveCdaInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "info", value = "cda信息模型", defaultValue = "")
            @RequestParam(value = "info") CDAForInterface info) {
        Result result = new Result();
//        try {
//            XCDADocument cdaInfo = new CDADocument();
//            String strErrorMsg = "";
//            if (info.getCode() == null || info.getCode() == "") {
//                strErrorMsg += "代码不能为空！";
//            }
//            if (info.getName() == null || info.getName() == "") {
//                strErrorMsg += "名称不能为空！";
//            }
//
//            if (info.getSourceId() == null || info.getSourceId() == "") {
//                strErrorMsg += "标准来源不能为空！";
//            }
//
//            if (info.getVersionCode() == null || info.getVersionCode() == "") {
//                strErrorMsg += "标准版本不能为空！";
//            }
//
//            if (info.getId() == null || info.getId().equals("")) {
//                if (xcdaDocumentManager.isDocumentExist(info.getVersionCode(), info.getCode())) {
//                    strErrorMsg += "代码不能重复！";
//                }
//                cdaInfo.setCreateUser(info.getUser());
//            } else {
//                if (xcdaDocumentManager.isDocumentExist(info.getVersionCode(), info.getCode(), info.getId())) {
//                    strErrorMsg += "代码不能重复！";
//                } else {
//                    cdaInfo = xcdaDocumentManager.getDocument(info.getVersionCode(), info.getId());
//                    cdaInfo.setUpdateUser(info.getUser());
//                }
//            }
//
//            if (strErrorMsg != "") {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//                return result;
//            }
//
//            cdaInfo.setCode(info.getCode());
//            cdaInfo.setName(info.getName());
//            cdaInfo.setSchema(info.getSchema());
//            cdaInfo.setSourceId(info.getSourceId());
//            cdaInfo.setVersionCode(info.getVersionCode());
//            cdaInfo.setDescription(info.getDescription());
//            cdaInfo.setTypeId(info.getTypeId());
//
//            int iResult = xcdaDocumentManager.saveDocument(cdaInfo);
//
//            if (iResult >= 1) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("CDA保存失败!");
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("CDA保存失败!");
//
//        }
        return result;
    }

    @RequestMapping("deleteCdaInfo")
    @ApiOperation(value = "删除cda信息")
    public Object deleteCdaInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();
//        try {
//            String strErrorMsg = "";
//            if (strVersionCode == null || strVersionCode == "") {
//                strErrorMsg += "标准版本不能为空!";
//            }
//            if (ids == null || ids == "") {
//                strErrorMsg += "请先选择将要删除的CDA！";
//            }
//
//            if (strErrorMsg != "") {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//            }
//
//            List<String> listIds = Arrays.asList(ids.split(","));
//            int iReault = xcdaDocumentManager.deleteDocument(strVersionCode, listIds);
//            if (iReault >= 0) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("CDA删除失败!");
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("CDA删除失败!");
//        }
        return result;
    }

    /**
     * 保存CDA信息
     * 1.先删除CDA数据集关联关系信息与cda文档XML文件，在新增信息
     * @param strDatasetIds 关联的数据集
     * @param strCdaId  cda文档 ID
     * @param strVersionCode 版本号
     * @param xmlInfo xml 文件内容
     * @return 操作结果
     */
    @RequestMapping("SaveRelationship")
    @ApiOperation(value = "保存CDA信息,1.先删除CDA数据集关联关系信息与cda文档XML文件，在新增信息")
    public Object SaveRelationship(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strDatasetIds", value = "关联的数据集", defaultValue = "")
            @RequestParam(value = "strDatasetIds") String strDatasetIds,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode,
            @ApiParam(name = "strCdaId", value = "cda文档 ID", defaultValue = "")
            @RequestParam(value = "strCdaId") String strCdaId,
            @ApiParam(name = "xmlInfo", value = "文件内容", defaultValue = "")
            @RequestParam(value = "xmlInfo") String xmlInfo) {
        Result result = new Result();
//        try {
//            String strErrorMsg = "";
//            if (strVersionCode == null || strVersionCode == "") {
//                strErrorMsg += "标准版本不能为空!";
//            }
//            if (strCdaId == null || strCdaId == "") {
//                strErrorMsg += "请先选择CDA!";
//            }
//
//            if (strErrorMsg != "") {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(strErrorMsg);
//                return result;
//            }
//
//            List<String> listCdaId = Arrays.asList(strCdaId.split(","));
//            int iDelRes = xCdaDatasetRelationshipManager.deleteRelationshipByCdaId(strVersionCode, listCdaId);
//
//            if (iDelRes < 0) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("关系保存失败!");
//
//                return result;
//            }
//
//            List<String> listIds = new ArrayList<>();
//            listIds.add(strCdaId);
//            XCDADocument[] xcdaDocuments = xcdaDocumentManager.getDocumentList(strVersionCode, listIds);
//            if (xcdaDocuments.length <= 0) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("请先选择CDA！");
//                return result;
//            }
//            if (xcdaDocuments[0].getFileGroup() != null && !xcdaDocuments[0].getFileGroup().equals("") && xcdaDocuments[0].getSchema() != null && !xcdaDocuments[0].getSchema().equals("")) {
//                FastDFSUtil.delete(xcdaDocuments[0].getFileGroup(), xcdaDocuments[0].getSchema());
//            }
//
//            if (strDatasetIds == null || strDatasetIds == "") {
//                result.setSuccessFlg(true);
//                result.setErrorMsg("关系保存成功!");
//                return result;
//
//            }
//
//            strDatasetIds = strDatasetIds.substring(0, strDatasetIds.length() - 1);
//
//            List<String> datasetIds = Arrays.asList(strDatasetIds.split(","));
//
//            XCdaDatasetRelationship[] infos = new CdaDatasetRelationship[datasetIds.size()];
//            for (int i = 0; i < infos.length; i++) {
//                String datasetId = datasetIds.get(i);
//                XCdaDatasetRelationship info = new CdaDatasetRelationship();
//                info.setCdaId(strCdaId);
//                info.setDatasetId(datasetId);
//                info.setVersionCode(strVersionCode);
//                infos[i] = info;
//            }
//
//            int iResult = xCdaDatasetRelationshipManager.addRelationship(infos);
//            if (iResult < 0) {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("关系保存失败!");
//            }
//
//            String strFilePath = SaveCdaFile(xmlInfo, strVersionCode, strCdaId);
//            //将文件上传到服务器中
//            ObjectNode msg = FastDFSUtil.upload(strFilePath, "");
//
//            String strFileGroup = msg.get(FastDFSUtil.GroupField).asText();//setFilePath
//            String strSchemePath = msg.get(FastDFSUtil.RemoteFileField).asText();//setFileName
//
//            File file = new File(strFilePath);
//            // 路径为文件且不为空则进行删除
//            if (file.isFile() && file.exists()) {
//                file.delete();
//            }
//
//            boolean bRes = SaveXmlFilePath(strCdaId, strVersionCode, strFileGroup, strSchemePath);
//            if (bRes) {
//                result.setSuccessFlg(true);
//                result.setErrorMsg("关系保存成功!");
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("关系保存失败!");
//            }
//
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("关系保存失败!");
//        }
        return result;
    }


    @RequestMapping("DeleteRelationship")
    @ApiOperation(value = "删除数据集关联信息")
    public Object DeleteRelationship(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();
//        try {
//            List<String> relationIds = Arrays.asList(ids.split(","));
//
//            int iResult = xCdaDatasetRelationshipManager.deleteRelationshipById(strVersionCode, relationIds);
//            if (iResult >= 0) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("关系删除失败!");
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("关系删除失败!");
//        }

        return result;
    }

    @RequestMapping("/FileExists")
    @ApiOperation(value = "判断文件是否存在")
    public String FileExists(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strCdaId", value = "编号集", defaultValue = "")
            @RequestParam(value = "strCdaId") String strCdaId,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        //1：已存在文件
//        if (xcdaDocumentManager.isFileExists(strCdaId, strVersionCode)) {
//            return "true";
//        } else {
////            return "false";
//        }
        return "";
    }

    /**
     * 生成CDA文件
     *
     * @param strCdaId
     * @param strVersionCode
     * @return
     */
    @RequestMapping("/createCDASchemaFile")
    @ApiOperation(value = "生成CDA文件")
    public String createCDASchemaFile(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strCdaId", value = "编号集", defaultValue = "")
            @RequestParam(value = "strCdaId") String strCdaId,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();

//        try {
//            int iResult = xcdaDocumentManager.createCDASchemaFile(strCdaId, strVersionCode);
//            if (iResult >= 0) {
//                result.setSuccessFlg(true);
//            } else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg("CDA文档创建失败!");
//            }
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//
//            result.setSuccessFlg(false);
//            result.setErrorMsg("CDA文档创建失败!");
//        }

        return result.toJson();
    }

//    @RequestMapping("/TestFileSend")
//    @ResponseBody
//    public void TestFileSend(String strVersion)
//    {
//        try {
//            XStdDispatchManager sendManager = ServiceFactory.getService(Services.StdDispatchManager);
//
//            sendManager.SendStandard(strVersion);
//        }
//        catch (Exception ex)
//        {
//            int t=0;
//        }
//    }

    /*
    * 根据CDA ID 获取数据集信息
    * @param strVersionCode 版本号
    * @param strCdaId CDAID
    * @return Envelop
    * */
    @RequestMapping("/getDatasetByCdaId")
    @ApiOperation(value = "根据CDA ID 获取数据集信息")
    public Object getDatasetByCdaId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strCdaId", value = "编号集", defaultValue = "")
            @RequestParam(value = "strCdaId") String strCdaId,
            @ApiParam(name = "strVersionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();
//        try {
//            XCdaDatasetRelationship[] relations = xCdaDatasetRelationshipManager.getRelationshipByCdaId(strCdaId, strVersionCode);
//            List<XDataSet> datasetList = new ArrayList<>();
//            for (XCdaDatasetRelationship info : relations) {
//                datasetList.add(info.getDataset());
//            }
//
//            List<DataSetForInterface> dataSetModels = new ArrayList<>();
//
//            for (XDataSet dataSet : datasetList) {
//
//                DataSetForInterface info = new DataSetForInterface();
//                info.setId(String.valueOf(dataSet.getId()));
//                info.setCode(dataSet.getCode());
//                info.setName(dataSet.getName());
//
//                dataSetModels.add(info);
//            }
//
//            if (dataSetModels == null) {
//                result.setSuccessFlg(false);
//                return result.toJson();
//            } else {
//
//                result = getResult(dataSetModels, 1, 1, 1);
//                result.setSuccessFlg(true);
//                return result.toJson();
//            }
//        } catch (Exception ex) {
//            result.setSuccessFlg(false);
//            return result.toJson();
//        }
        return "";
    }

    @RequestMapping("/validatorCda")
    @ApiOperation(value = "验证cda")
    public String validatorCda(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "versionCode", value = "版本代码", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode) {
//        boolean documentExist = xcdaDocumentManager.isDocumentExist(versionCode, code);
//        if (documentExist) {
//            return getSuccessResult(true).toJson();
//        }
//
//        return getSuccessResult(false).toJson();
        return "";
    }

//    /**
//     * 将String 保存为XML文件
//     *
//     * @param fileInfo 文件信息
//     * @return 返回 文件路径
//     */
//    public String SaveCdaFile(String fileInfo, String versionCode, String cdaId) {
//        fileInfo = fileInfo.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//        String strPath = System.getProperty("java.io.tmpdir");
//        String splitMark = System.getProperty("file.separator");
//        strPath += splitMark+"StandardFiles";
//        //文件路径
//        String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "createfile" + splitMark + cdaId + ".xml";
//
//        File file = new File(strXMLFilePath);
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//        }
//
//        try {
//            FileWriter fw = new FileWriter(file, true);
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write(fileInfo);
//            bw.flush();
//            bw.close();
//            fw.close();
//        } catch (IOException ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//        }
//
//        return strXMLFilePath;
//    }

//    public boolean SaveXmlFilePath(String cdaId, String versionCode, String fileGroup, String filePath) {
//        boolean result = true;
//
//        try {
//
//            List<String> listIds = new ArrayList<>();
//            listIds.add(cdaId);
//            XCDADocument[] xcdaDocuments = xcdaDocumentManager.getDocumentList(versionCode, listIds);
//            if (xcdaDocuments.length <= 0) {
//                LogService.getLogger(CdaController.class).error("未找到CDA");
//                return false;
//            }
//
//            xcdaDocuments[0].setFileGroup(fileGroup);
//            xcdaDocuments[0].setSchema(filePath);
//            xcdaDocuments[0].setVersionCode(versionCode);
//            int iRes = xcdaDocumentManager.saveDocument(xcdaDocuments[0]);
//            if (iRes < 0) {
//                return false;
//            }
//        } catch (Exception ex) {
//            result = false;
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//        }
//
//        return result;
//    }

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
    @ApiOperation(value = "获取cda文档的XML文件信息。从服务器的临时文件路径中读取配置文件，并以XML形式返回。")
    public Object getCdaXmlFileInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaId", value = "编号集", defaultValue = "")
            @RequestParam(value = "cdaId") String cdaId,
            @ApiParam(name = "versionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode) {
        Result result = new Result();
//        String strXmlInfo = "";
//
//        try {
//            String strPath = System.getProperty("java.io.tmpdir");
//            strPath += "StandardFiles";
//
//            String splitMark = System.getProperty("file.separator");
//            String strXMLFilePath = strPath + splitMark + "xml" + splitMark + versionCode + splitMark + "downfile" + splitMark;
//
//            List<String> listIds = new ArrayList<>();
//            listIds.add(cdaId);
//
//            XCDADocument[] xcdaDocuments = xcdaDocumentManager.getDocumentList(versionCode, listIds);
//            String strFileGroup = "";
//            String strSchemePath = "";
//            if (xcdaDocuments.length > 0) {
//                strFileGroup = xcdaDocuments[0].getFileGroup();
//                strSchemePath = xcdaDocuments[0].getSchema();
//            } else {
//                return "";
//            }
//
//            File files = new File(strXMLFilePath);
//            if (!files.exists()) {
//                files.mkdirs();
//            }
//
//            String strLocalFileName = strXMLFilePath + "\\" + strSchemePath.replaceAll("/", "_");
//            File localFile = new File(strLocalFileName);
//            if (localFile.exists() && localFile.isFile()) {
//                localFile.delete();
//            }
//            if (!strFileGroup.equals("") && !strSchemePath.equals("")) {
//                strLocalFileName = FastDFSUtil.download(strFileGroup, strSchemePath, strXMLFilePath);
//
//                File file = new File(strLocalFileName);
//                FileReader fr = new FileReader(file);
//                BufferedReader bReader = new BufferedReader(fr);
//                strXmlInfo = bReader.readLine();
//            } else {
//                strXmlInfo = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root></root>";
//            }
//
//
//        } catch (Exception ex) {
//            LogService.getLogger(CdaController.class).error(ex.getMessage());
//        }
//
//        result.setSuccessFlg(true);
//        result.setObj(strXmlInfo);
        return result;
    }

    @RequestMapping("/getOrgType")
    @ApiOperation(value = "获取机构类型")
    public Object getOrgType(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
        Result result = new Result();
//        try {
//            String url = "/rest/v1.0/conDict/orgType";
//            Map<String, Object> params = new HashMap<>();
//            params.put("type","Govement");
//            String _res = HttpClientUtil.doGet(host + url, params, username, password);
//            result.setSuccessFlg(true);
//            result.setObj(_res);
//        }
//        catch (Exception ex)
//        {
//
//        }
        return result;
    }
}
