package com.yihu.ehr.standard.datasets.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.datasets.service.*;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/std/dataset")
@Api(protocols = "https", value = "std/dataset", description = "标准数据集", tags = {"标准数据集"})
public class DataSetsController extends BaseController {

    @Autowired
    private DataSetManager dataSetManager;
    @Autowired
    private MetaDataManager metaDataManager;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集的方法")
    public Result searchDataSets(
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


    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public boolean deleteDataSet(
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        try {
            int iResult = metaDataManager.removeMetaDataBySetId(version, dataSetId);
            if (iResult >= 0)
                iResult = dataSetManager.deleteDataSet(dataSetId, version);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public Result getDataSet(
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "versionCode", value = "版本", defaultValue = "")
            @RequestParam(value = "versionCode") String versionCode) {

        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(versionCode);
        Result result = new Result();
        try {
            DataSet dataSet = dataSetManager.getDataSet(dataSetId, cdaVersion);
            DataSetModel dataSetModel = dataSetManager.getDataSet(dataSet);
            result.setSuccessFlg(true);
            result.setObj(dataSetModel);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }


    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ApiOperation(value = "保存数据集信息")
    public Result saveDataSet(
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
                DataSet dataSetCode = dataSetManager.getDataSet(code, cdaVersion.getVersion());
                if (dataSetCode != null) {
                    strErrorMsg += "代码不能重复！";
                }
            } else {
                //修改时代码不能重复
                dataSet = dataSetManager.getDataSet(code, cdaVersion.getVersion());
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
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
        }
        return result;
    }



    @RequestMapping("/getXMLInfoByDataSetId")
    @ApiOperation(value = "将CDA归属的数据集信息转换的XML信息")
    public Result getXMLInfoByDataSetId(
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



    /*************************************************************************/
    /************   以下新增                                    *************/
    /*************************************************************************/
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        return dataSetManager.getDataSetMapByIds(
                ids==null || ids.trim().length()==0? new String[]{} : ids.split(","),
                version);
    }

    @RequestMapping(value = "/metamap", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元 id-name : map集")
    public Map getMetaDataMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String dataSetIds,
            @ApiParam(name = "medaIds", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "medaIds") String metaIds) {

        return dataSetManager.getMetaDataMapByIds(
                metaIds==null || metaIds.trim().length()==0? new String[]{} : metaIds.split(","),
                version,
                dataSetIds==null || dataSetIds.trim().length()==0? new String[]{} : dataSetIds.split(","));
    }


}