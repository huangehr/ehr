package com.yihu.ehr.standard.datasets.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.datasets.service.*;
import com.yihu.ehr.standard.dict.service.Dict;
import com.yihu.ehr.standard.dict.service.DictForInterface;
import com.yihu.ehr.standard.dict.service.DictManager;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/std/metadata")
@Api(protocols = "https", value = "std/dataset", description = "标准数据元", tags = {"标准数据元"})
public class MetaDataController extends BaseController {

    @Autowired
    private DictManager dictManager;
    @Autowired
    private MetaDataManager metaDataManager;


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    public Result searchMetaData(
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

        Result result = new Result();
        if (id == null || id.equals(0) || id.equals("") || version == null || version.equals(0) || version.equals("")) {
            result.setSuccessFlg(false);
            return result;
        }
        try {
            List<MetaDataModel> metaDataModel = new ArrayList<>();
            CDAVersion cdaVersion = new CDAVersion();
            DataSet dataSetModel = new DataSet();
            cdaVersion.setVersion(version);
            dataSetModel.setId(id);
            dataSetModel.setInnerVersion(cdaVersion);
            dataSetModel.setCode(metaDataCode);
            dataSetModel.setName(metaDataCode);
            metaDataModel = metaDataManager.searchMetaDataList(dataSetModel, page, rows);
            Integer totalCount = metaDataManager.searchDataSetInt(dataSetModel);
            result.setSuccessFlg(true);
            result = getResult(metaDataModel, totalCount, page, rows);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    public Result deleteMetaData(
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


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    public Result getMetaData(
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


    @RequestMapping(value = "/info", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据元")
    public Result updataMetaSet(
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


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "检验字典查询的方法")
    public String getMetaDataDict(
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
            LogService.getLogger(MetaDataController.class).error(ex.getMessage());
        }

        return strResult;
    }


    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元")
    public boolean validatorMetadata(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "datasetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "datasetId") String datasetId,
            @ApiParam(name = "searchNm", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "metaDataCodeMsg", value = "查询类型：metaDataCodeMsg 代码，fieldNameMsg 名称", defaultValue = "")
            @RequestParam(value = "metaDataCodeMsg") String metaDataCodeMsg) {

        try {
            if (metaDataCodeMsg.equals("metaDataCodeMsg")) {
                if (metaDataManager.getCountByCode(version, searchNm, datasetId) > 0) {
                    return false;
                }
            }
            if (metaDataCodeMsg.equals("fieldNameMsg")) {
                if (metaDataManager.getCountByColumnName(version, searchNm, datasetId) > 0) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}