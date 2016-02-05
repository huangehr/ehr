package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.dict.service.Dict;
import com.yihu.ehr.standard.dict.service.DictForInterface;
import com.yihu.ehr.standard.dict.service.DictManager;
import com.yihu.ehr.standard.standardsource.service.StandardSourceService;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cdadict")
@Api(protocols = "https", value = "cdadict", description = "标准字典", tags = {"标准字典", "标准字典项"})
public class DictController extends BaseController{
    private final String VERSION_NOT_FINT = "没有找到对应版本！";
    private final String CODE_NO_NULL = "代码不能为空！";
    private final String NAME_NO_NULL = "名称不能为空！";
    private final String CODE_NOT_UNIQUE = "codeNotUnique";
    private final String SAVE_FAILED = "保存失败！";
    private final String DEL_FAILED = "删除失败！";
    private final String DATA_LOAD_FAILED = "数据加载失败！";
    private final String DATA_NOT_FIND = "没有发现数据！";
    @Autowired
    private CDAVersionManager cdaVersionManager;
    @Autowired
    private StandardSourceService standardSourceManager;
    @Autowired
    private DictManager dictManager;


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典")
    public Result saveDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "dictId", value = "编号", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "baseDict", value = "继承字典", defaultValue = "")
            @RequestParam(value = "baseDict") Long baseDict,
            @ApiParam(name = "stdSource", value = "标准来源", defaultValue = "")
            @RequestParam(value = "stdSource") String stdSource,
            @ApiParam(name = "stdVersion", value = "标准版本", defaultValue = "")
            @RequestParam(value = "stdVersion") String stdVersion,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "用户编号", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            if (code == null || code.equals(""))
                return failed(CODE_NO_NULL);
            if (name == null || name.equals(""))
                return failed(NAME_NO_NULL);

            Dict xDict = new Dict();
            if(dictId!=0){
                xDict = dictManager.getDict(dictId, xcdaVersion);
                if(!code.equals(xDict.getCode())){
                    if(dictManager.isDictCodeExist(xcdaVersion,code)){
                        result.setErrorMsg(CODE_NOT_UNIQUE);
                        return result;
                    }
                }
            }
            else if(dictManager.isDictCodeExist(xcdaVersion,code)){
                result.setErrorMsg(CODE_NOT_UNIQUE);
                return result;
            }
            dictManager.setDictValues(
                    xDict, dictId, code, name, userId, standardSourceManager.getSourceBySingleId(stdSource), baseDict,
                    new Date(), description, stdVersion, xcdaVersion);

            dictManager.saveDict(xDict);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(SAVE_FAILED);
        }
        return result;
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    public Result deleteDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "dictId", value = "编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict dict = dictManager.createDict(xcdaVersion);
            dict.setId(dictId);
            int resultItem = dictManager.removeDict(dict);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DEL_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "根据CdaVersion查询相应版本的字典数据")
    public Result getCdaDictList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "searchNm", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict[] xDicts = dictManager.getDictListForInter(page, rows, xcdaVersion, searchNm);
            if (xDicts != null) {
                List ls = dictManager.dictTransfer(xDicts);
                Integer totalCount = dictManager.getDictListInt(xcdaVersion, searchNm);
                result = getResult(ls, totalCount, page, rows);
            }
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "根据CdaVersion及字典ID查询相应版本的字典详细信息")
    public Result getCdaDictInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict xDict = dictManager.getDict(dictId, xcdaVersion);

            if (xDict != null) {
                DictForInterface info = dictManager.dictTransfer(new Dict[]{xDict}).get(0);
                result.setSuccessFlg(true);
                result.setObj(info);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(DATA_NOT_FIND);
            }
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/listFilterCur", method = RequestMethod.GET)
    @ApiOperation(value = "根据输入条件，查询字典List(过滤掉当前字典)")
    public Result getCdaBaseDictList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") String dictId,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict[] xDicts = dictManager.getBaseDictList(xcdaVersion, dictId);
            List<DictForInterface> ls = dictManager.dictTransfer(xDicts);
            result.setSuccessFlg(true);
            result.setDetailModelList(ls);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/pageFilterCur", method = RequestMethod.GET)
    @ApiOperation(value = "根据输入条件，查询字典List(过滤掉当前字典)")
    public Result searchCdaBaseDictList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "param", value = "查询值", defaultValue = "")
            @RequestParam(value = "param") String param,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict[] xDicts = dictManager.getDictListForInter(page, rows,xcdaVersion, param);
            List<DictForInterface> ls = dictManager.dictTransfer(xDicts);
            Integer totalCount = dictManager.getDictListInt(xcdaVersion, param);
            result = getResult(ls, totalCount, page, rows);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    /*************************************************************************/
    /************   以下新增                                    *************/
    /*************************************************************************/
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典 map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "metaDataId", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "metaDataId") Long metaDataId) {

        return dictManager.getDictMapByIds(version, dataSetId, metaDataId);
    }
}
