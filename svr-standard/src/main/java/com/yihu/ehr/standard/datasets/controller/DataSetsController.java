package com.yihu.ehr.standard.datasets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.datasets.service.*;
import com.yihu.ehr.standard.dict.service.Dict;
import com.yihu.ehr.standard.dict.service.DictForInterface;
import com.yihu.ehr.standard.dict.service.DictManager;
import com.yihu.ehr.standard.standardsource.service.StandardSourceManager;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:59
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/std/dataset")
@Api(protocols = "https", value = "std/dataset", description = "标准数据集", tags = {"标准数据集", "标准数据元"})
public class DataSetsController extends BaseController {

    @Autowired
    private DictManager dictManager;

    @Autowired
    private DataSetManager dataSetManager;

    @Autowired
    private StandardSourceManager xStandardSourceManager;

    @Autowired
    private CDAVersionManager xcdaVersionManager;

    @Autowired
    private MetaDataManager metaDataManager;


    /**
     * 数据集版本号查询的方法
     *
     * @return
     */
    @RequestMapping("/searchVersion")
    @ApiOperation(value = "数据集版本号查询的方法")
    public Object searchVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        Result result = new Result();
        try {
            CDAVersion[] cdaVersions = xcdaVersionManager.getVersionList();
            List<String> cdaVersionslist = new ArrayList<String>();
            for (CDAVersion xcdaVersion : cdaVersions) {
                cdaVersionslist.add(xcdaVersion.getVersion());
            }
            result.setObj(cdaVersionslist);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    /**
     * 查询数据集的方法
     *
     * @param codename
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchDataSets")
    @ApiOperation(value = "查询数据集的方法")
    public Object searchDataSets(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "codename", value = "搜索值", defaultValue = "")
            @RequestParam(value = "codename") String codename,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        String strErrMessage = "";
        if (version == null || version == "") {
            strErrMessage += "请选择版本号!";
        }
        if (strErrMessage != "") {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result;
        }

        cdaVersion.setVersion(version);

        List<DataSetModel> dataSet = null;
        Integer totalCount = null;
        try {
            dataSet = dataSetManager.searchDataSetList(codename, page, rows, cdaVersion);

            totalCount = dataSetManager.searchDataSetInt(cdaVersion);

            if (dataSet == null) {
                result.setSuccessFlg(false);

            } else {
                if (rows == 0)
                    rows = 1;
                result = getResult(dataSet, totalCount, page, rows);
                result.setSuccessFlg(true);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

    /**
     * 删除数据集信息的方法
     *
     * @param dataSetId
     * @return
     */
    @RequestMapping("/deleteDataSet")
    @ApiOperation(value = "删除数据集信息")
    public Object deleteDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        Result result = new Result();
        if (dataSetId == 0) {
            result.setSuccessFlg(false);
            return result;
        }

        try {
            int iResult = metaDataManager.removeMetaDataBySetId(version, dataSetId);
            if (iResult >= 0) {
                iResult = dataSetManager.deleteDataSet(dataSetId, version);
                if (iResult >= 0)
                    result.setSuccessFlg(true);
                else
                    result.setSuccessFlg(false);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {

            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/getDataSet")
    @ApiOperation(value = "获取数据集信息")
    public Object getDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "versionCode", value = "版本", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode) {
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(versionCode);
        Result result = new Result();
        if (dataSetId == 0) {

            result.setSuccessFlg(false);

            return result;
        }

        try {
            DataSet dataSet = dataSetManager.getDataSet(dataSetId, cdaVersion);
            DataSetModel dataSetModel = dataSetManager.getDataSet(dataSet);

            if (dataSet == null) {
                result.setSuccessFlg(false);
            } else {
                result.setSuccessFlg(true);
                result.setObj(dataSetModel);
            }
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;

    }

    /**
     * 数据集信息修改的方法
     *
     * @param id
     * @param code
     * @param name
     * @param type
     * @param refStandard
     * @param summary
     * @return
     */
    @RequestMapping("/saveDataSet")
    @ApiOperation(value = "保存数据集信息")
    public Object saveDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "refStandard", value = "标准来源", defaultValue = "")
            @RequestParam(value = "refStandard") String refStandard,
            @ApiParam(name = "summary", value = "描述", defaultValue = "")
            @RequestParam(value = "summary") String summary,
            @ApiParam(name = "versionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode) {
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(versionCode);
        Result result = new Result();
        String strErrorMsg = "";
        try {
            if (code == null || code == "") {
                strErrorMsg += "代码不能为空！";
            }
            if (name == null || name == "") {
                strErrorMsg += "名称不能为空！";
            }

            if (refStandard == null || refStandard == "") {
                strErrorMsg += "标准来源不能为空！";
            }

            if (cdaVersion == null || cdaVersion.getVersion() == "") {
                strErrorMsg += "标准版本不能为空！";
            }
            DataSet dataSet = new DataSet();
            //新增时代码不能重复
            if (id == 0) {
                DataSet dataSetCode = (DataSet) dataSetManager.getDataSet(code, cdaVersion.getVersion());
                if (dataSetCode != null) {
                    strErrorMsg += "代码不能重复！";
                }
            } else {
                //修改时代码不能重复
                dataSet = (DataSet) dataSetManager.getDataSet(code, cdaVersion.getVersion());
                if (dataSet != null && dataSet.getId() != id) {
                    strErrorMsg += "代码不能重复！";
                } else {
                    dataSet = dataSetManager.getDataSet(id, cdaVersion);
                }
            }

            if (strErrorMsg != "") {
                result.setSuccessFlg(false);
                result.setErrorMsg(strErrorMsg);
                return result;
            }
            dataSet.setInnerVersion(cdaVersion);

            dataSet.setCode(code);
            dataSet.setName(name);
            dataSet.setReference(refStandard);
            dataSet.setSummary(summary);

            boolean bo = dataSetManager.saveDataSet(dataSet);
            if (bo) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ex.getMessage());
        }
        return result;
    }

    /**
     * 查询数据源的方法
     *
     * @param id
     * @param version
     * @param metaDataCode
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/searchMetaData")
    @ApiOperation(value = "查询数据元")
    public Object searchMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "搜索值", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "metaDataCode", value = "数据元代码", defaultValue = "")
            @RequestParam(value = "metaDataCode") String metaDataCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        List<MetaDataModel> metaDataModel = new ArrayList<>();
        CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();
        if (id == null || id.equals(0) || id.equals("") || version == null || version.equals(0) || version.equals("")) {
            result.setSuccessFlg(false);
            return result;
        }

        DataSet dataSetModel = new DataSet();
        cdaVersion.setVersion(version);
        dataSetModel.setId(id);
        dataSetModel.setInnerVersion(cdaVersion);
        dataSetModel.setCode(metaDataCode);
        dataSetModel.setName(metaDataCode);

        Integer totalCount = null;
        try {
            metaDataModel = metaDataManager.searchMetaDataList(dataSetModel, page, rows);

            totalCount = metaDataManager.searchDataSetInt(dataSetModel);
            result.setSuccessFlg(true);
            result = getResult(metaDataModel, totalCount, page, rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    /**
     * 删除数据源的方法
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteMetaData")
    @ApiOperation(value = "删除数据元")
    public Object deleteMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        Result result = new Result();
        String strErrMessage = "";
        if (ids == null || ids.equals("")) {
            strErrMessage += "请选择数据元!";
        }
        if (version == null || version.equals("")) {
            strErrMessage += "请选择标准版本!";
        }

        if (!strErrMessage.equals("")) {
            result.setErrorMsg(strErrMessage);
            result.setSuccessFlg(false);
            return result;
        }

        try {

            int res = metaDataManager.deleteMetaDatas(ids, version);
            if (res > -1) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "/getMetaData", produces = "text/html;charset=UTF-8")
    @ApiOperation(value = "获取数据元")
    public Object getMetaData(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "metaDataId", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "metaDataId") long metaDataId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        CDAVersion cdaVersion = new CDAVersion();

        Result result = new Result();
        String strErrMessage = "";
        if (dataSetId == 0) {
            strErrMessage += "请先选择数据集!";
        }
        if (metaDataId == 0) {
            strErrMessage += "请先选择数据元!";
        }

        if (version == null || version.equals(0) || version.equals("")) {
            strErrMessage += "请先选择标准版本!";
        }
        if (strErrMessage != "") {
            result.setSuccessFlg(false);
            result.setErrorMsg(strErrMessage);
            return result;
        }

        DataSet dataSet = new DataSet();
        dataSet.setId(dataSetId);
        cdaVersion.setVersion(version);
        dataSet.setInnerVersion(cdaVersion);

        List<MetaDataForInterface> elementList = null;
        try {
            List<MetaData> metaDataList = metaDataManager.getMetaDatas(dataSet, metaDataId);
            if (metaDataList.size() > 0) {
                elementList = new ArrayList<>();
                for (int i = 0; i < metaDataList.size(); i++) {
                    MetaData metaData = (MetaData) metaDataList.get(i);
                    MetaDataForInterface info = new MetaDataForInterface();

                    info.setDatasetId(String.valueOf(metaData.getDataSetId()));
                    info.setCode(metaData.getCode());
                    info.setInnerCode(metaData.getInnerCode());
                    info.setName(metaData.getName());
                    info.setType(metaData.getType());
                    info.setFormatType(metaData.getFormat());
                    info.setDefinition(metaData.getDefinition());
                    info.setNullable(metaData.isNullable() ? "1" : "0");
                    info.setColumnType(metaData.getColumnType());
                    info.setColumnName(metaData.getColumnName());
                    info.setColumnLength(metaData.getColumnLength());
                    info.setPrimaryKey(metaData.isPrimaryKey() ? "1" : "0");
                    info.setHashCode(String.valueOf(metaData.getHashCode()));
                    info.setId(String.valueOf(metaData.getId()));
                    info.setDictId(String.valueOf(metaData.getDictId()));
                    info.setDictName(metaData.getDictName());
                    elementList.add(info);
                }
                result.setSuccessFlg(true);
                result.setDetailModelList(elementList);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

    @RequestMapping(value = "/updataMetaSet", produces = "text/html;charset=UTF-8")
    @ApiOperation(value = "更新数据元")
    public Object updataMetaSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "info", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "info") MetaDataForInterface info) {
        CDAVersion cdaVersion = new CDAVersion();
        Result result = new Result();

        try {
            String strErrMessage = "";
            if (info.getVersion() == null || info.getVersion().equals(0) || info.getVersion().equals("")) {
                strErrMessage += "请先选择标准版本!";
            }
            if (info.getDatasetId() == null || info.getDatasetId().equals(0) || info.getDatasetId().equals("")) {
                strErrMessage += "请先选择数据集!";
            }

            if (info.getCode() == null || info.getCode().equals(0) || info.getCode().equals("")) {
                strErrMessage += "代码不能为空!";
            }
            if (info.getName() == null || info.getName().equals(0) || info.getName().equals("")) {
                strErrMessage += "名称不能为空!";
            }

            if (info.getInnerCode() == null || info.getInnerCode().equals(0) || info.getInnerCode().equals("")) {
                strErrMessage += "内部代码不能为空!";
            }

            if (info.getColumnName() == null || info.getColumnName().equals(0) || info.getColumnName().equals("")) {
                strErrMessage += "字段名称不能为空!";
            }

            if (info.getId().equals("0")) {
                if (metaDataManager.getCountByCode(info.getVersion(), info.getInnerCode(), info.getDatasetId()) > 0) {
                    strErrMessage += "代码不能重复!";
                }

                if (metaDataManager.getCountByColumnName(info.getVersion(), info.getColumnName(), info.getDatasetId()) > 0) {
                    strErrMessage += "字段名不能重复!";
                }
            } else {
                //if ()
            }

            if (!strErrMessage.equals("")) {
                result.setErrorMsg(strErrMessage);
                result.setSuccessFlg(false);
                return result;
            }

            DataSet dataSetModel = new DataSet();
            MetaData metaData = new MetaData();

            cdaVersion.setVersion(info.getVersion());
            dataSetModel.setInnerVersion(cdaVersion);
            dataSetModel.setId(Long.parseLong(info.getDatasetId()));

            metaData.setId(Long.parseLong(info.getId()));
            metaData.setDataSet(dataSetModel);
            metaData.setCode(info.getCode());
            metaData.setName(info.getName());
            metaData.setInnerCode(info.getInnerCode());
            metaData.setType(info.getType());
            metaData.setDictId(info.getDictId() == "" ? 0 : Long.parseLong(info.getDictId()));
            metaData.setFormat(info.getFormatType());
            metaData.setDefinition(info.getDefinition());
            metaData.setColumnName(info.getColumnName());
            metaData.setColumnLength(info.getColumnLength());
            metaData.setColumnType(info.getColumnType());
            if (info.getPrimaryKey().equals("1")) {
                metaData.setPrimaryKey(true);
            } else {
                metaData.setPrimaryKey(false);
            }
            if (info.getNullable().equals("1")) {
                metaData.setNullable(true);
            } else {
                metaData.setNullable(false);
            }

            int res = metaDataManager.saveMetaData(dataSetModel, metaData);

            if (res > 0) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }


    /**
     * 检验字典查询的方法
     *
     * @param version
     * @param key
     * @return
     */
    @RequestMapping(value = "/getMetaDataDict")
    @ApiOperation(value = "检验字典查询的方法")
    public Object getMetaDataDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "key", value = "查询值", defaultValue = "")
            @RequestParam(value = "key") String key) {
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(version);
        String strResult = "[]";
        Dict[] xDicts = null;

        try {

            xDicts = dictManager.getDictLists(cdaVersion, key);
            if (xDicts != null) {
                DictForInterface[] infos = new DictForInterface[xDicts.length];
                int i = 0;
                for (Dict xDict : xDicts) {
                    DictForInterface info = new DictForInterface();

                    info.setId(String.valueOf(xDict.getId()));
                    info.setCode(xDict.getCode());
                    info.setName(xDict.getName());
                    info.setAuthor(xDict.getAuthor());
                    info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
                    info.setCreateDate(String.valueOf(xDict.getCreateDate()));
                    info.setDescription(xDict.getDescription());
                    info.setStdVersion(xDict.getStdVersion());
                    info.setHashCode(String.valueOf(xDict.getHashCode()));
                    info.setInnerVersionId(xDict.getInnerVersion());

                    infos[i] = info;
                    i++;
                }

                List<DictForInterface> xDictForInterfaces = Arrays.asList(infos);

                ObjectMapper objectMapper = new ObjectMapper();
                strResult = objectMapper.writeValueAsString(xDictForInterfaces);

            }
        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
        }

        return strResult;
    }

    @RequestMapping("/validatorMetadata")
    @ApiOperation(value = "验证数据元")
    public Object validatorMetadata(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "datasetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "datasetId") String datasetId,
            @ApiParam(name = "searchNm", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "metaDataCodeMsg", value = "查询类型：metaDataCodeMsg 代码，fieldNameMsg 名称", defaultValue = "")
            @RequestParam(value = "metaDataCodeMsg") String metaDataCodeMsg) {

        if (metaDataCodeMsg.equals("metaDataCodeMsg")) {
            if (metaDataManager.getCountByCode(version, searchNm, datasetId) > 0) {
                return getSuccessResult(false);
            }
        }
        if (metaDataCodeMsg.equals("fieldNameMsg")) {
            if (metaDataManager.getCountByColumnName(version, searchNm, datasetId) > 0) {
                return getSuccessResult(false);
            }
        }

        return getSuccessResult(true);
    }

    /**
     * 将CDA归属的数据集信息转换的XML信息
     *
     * @param setId       数据集ID
     * @param versionCode 版本号
     * @return xml信息
     */
    @RequestMapping("/getXMLInfoByDataSetId")
    @ApiOperation(value = "将CDA归属的数据集信息转换的XML信息")
    public Object getXMLInfoByDataSetId(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "versionCode", value = "版本号", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode,
            @ApiParam(name = "setId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "setId") String setId) {
        Result result = new Result();
        String strResult = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root>";
        try {
            List<String> listIds = Arrays.asList(setId.split(","));
            DataSet[] xDataSets = dataSetManager.getDataSetByIds(listIds, versionCode);
            if (xDataSets.length > 0) {
                for (int i = 0; i < xDataSets.length; i++) {
                    String strCode = xDataSets[i].getCode();
                    strResult += "<" + strCode + ">";
                    List<MetaData> xMetaDatas = metaDataManager.getMetaDataList(xDataSets[i]);
                    if (xMetaDatas.size() > 0) {
                        for (int j = 0; j < xMetaDatas.size(); j++) {
                            String strColumnName = xMetaDatas.get(j).getColumnName();
                            strResult += "<" + strColumnName + "></" + strColumnName + ">";
                        }
                    }
                    strResult += "</" + strCode + ">";
                }
            }


        } catch (Exception ex) {
            LogService.getLogger(DataSetsController.class).error(ex.getMessage());
        }
        strResult += "</root>";
        //  result=result.replaceAll("<","&lt;").replaceAll(">","&gt;");
        result.setSuccessFlg(true);
        result.setObj(strResult);
        return result;
    }

//    public void exportToExcel(){
//        //todo：test 导出测试
//        List<String> ids = new ArrayList<>();
//        ids.add("1");
//        ids.add("2");
//        ids.add("3");
//        XDataSet[] dataSets = dataSetManager.getDataSetByIds(ids, xcdaVersionManager.getLatestVersion().getVersion());
//        dataSetManager.exportToExcel("E:/workspaces/excel/testExport.xls",dataSets);
//    }
//
//    public void importFromExcel(){
//        //todo：test导入测试
//        dataSetManager.importFromExcel("E:/workspaces/excel/测试excel导入.xls", xcdaVersionManager.getLatestVersion());
//    }


    /**
     * 标准来源下拉框的查询
     *
     * @param version
     * @return
     */
    @RequestMapping("getStdSourceList")
    @ApiOperation(value = "标准来源下拉框的查询")
    public Object getStdSourceList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) {
//        Result result = new Result();
//        String strJson = "";
//        try {
//            if (version == null || version == "") {
//                return missParameter("version_code");
//            }
//
//            StandardSource[] xStandardSources = xStandardSourceManager.getSourceList();
//
//            if (xStandardSources == null) {
//                return failed(orCode.GetStandardSourceFailed);
//            }
//            List<StandardSourceForInterface> resultInfos = GetStandardSourceForInterface(xStandardSources);
//
//            ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//            strJson = objectMapper.writeValueAsString(resultInfos);
//        } catch (Exception ex) {
//            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
//            return failed(ErrorCode.GetStandardSourceFailed);
//        }
//        RestEcho echo = new RestEcho().success();
//        echo.putResultToList(strJson);
//
//        return echo;
        return "";
    }


//    public List<StandardSourceForInterface> GetStandardSourceForInterface(XStandardSource[] xStandardSources) {
//        List<StandardSourceForInterface> results = new ArrayList<>();
//        for (XStandardSource xStandardSource : xStandardSources) {
//            StandardSourceForInterface info = new StandardSourceForInterface();
//            info.setId(xStandardSource.getId());
//            info.setCode(xStandardSource.getCode());
//            info.setName(xStandardSource.getName());
//            info.setSourceType(xStandardSource.getSourceType().getCode());
//            info.setDescription(xStandardSource.getDescription());
//            results.add(info);
//        }
//
//        return results;
//    }
}