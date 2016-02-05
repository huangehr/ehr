package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.dict.service.*;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cdadictitem")
@Api(protocols = "https", value = "cdadictitem", description = "标准字典项", tags = {"标准字典项"})
public class DictItemController extends BaseController{
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
    private DictEntryManager dictEntryManager;
    @Autowired
    private DictManager dictManager;



    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "保存字典项")
    public Result saveDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "值", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "desc", value = "描述", defaultValue = "")
            @RequestParam(value = "desc") String desc) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            if (dictId == 0)
                return failed("字典编号不能为空！");
            if (code == null || code.equals(""))
                return failed(CODE_NO_NULL);
            if (value == null || value.equals(""))
                return failed(NAME_NO_NULL);

            Dict xDict = dictManager.getDict(dictId, xcdaVersion);
            DictEntry xDictEntry = new DictEntry();
            if(id!=0){
                xDictEntry = dictEntryManager.getEntries(id, xDict);
                if(!code.equals(xDictEntry.getCode())){
                    if(dictEntryManager.isDictEntryCodeExist(xDict, code)){
                        result.setErrorMsg("codeNotUnique");
                        return result;
                    }
                }
            }
            else{
                if(dictEntryManager.isDictEntryCodeExist(xDict, code)){
                    result.setErrorMsg("codeNotUnique");
                    return result;
                }
            }
            dictEntryManager.setDictEntryValues(xDictEntry, id, xDict, code, value, desc);
            int resultFlag = dictEntryManager.saveEntry(xDictEntry);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictItemController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(SAVE_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public Result deleteDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "entryId", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "entryId") long entryId) {

        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);

            Dict xDict = dictManager.getDict(dictId, xcdaVersion);
            DictEntry xDictEntry = dictEntryManager.getEntries(entryId, xDict);

            int resultFlag = dictEntryManager.deleteEntry(xDictEntry);
            if (resultFlag >= 1) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(DEL_FAILED);
            }
        } catch (Exception ex) {
            LogService.getLogger(DictItemController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DEL_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/relation", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    public Result deleteDictEntryList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") String dictId) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            List<String> ids = new ArrayList<>();
            ids.add(dictId);
            int resultFlag = dictEntryManager.delDictEntryList(xcdaVersion, ids);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictItemController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DEL_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项列表")
    public Result searchDictEntryList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "searchNmEntry", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNmEntry") String searchNmEntry,
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
            Dict xDict = dictManager.getDict(dictId, xcdaVersion);
            DictEntry[] xDictEntries = dictEntryManager.getDictEntries(xDict, searchNmEntry, page, rows);
            int totalCount = dictEntryManager.getDictEntriesForInt(xDict, searchNmEntry);
            List<DictEntryForInterface> ls = dictEntryManager.dictEntryTransfer(xDictEntries);
            result = getResult(ls, totalCount, page, rows);
        } catch (Exception ex) {
            LogService.getLogger(DictItemController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    public Result getDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") Long dictId,
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(strVersionCode);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            Dict xDict = dictManager.getDict(dictId, xcdaVersion);
            DictEntry xDictEntry = dictEntryManager.getEntries(id, xDict);
            if (xDictEntry != null) {
                DictEntryForInterface info = dictEntryManager.dictEntryTransfer(new DictEntry[]{xDictEntry}).get(0);
                result.setSuccessFlg(true);
                result.setObj(info);
            }else {
                result.setSuccessFlg(false);
                result.setErrorMsg(DATA_NOT_FIND);
            }
        } catch (Exception ex) {
            LogService.getLogger(DictItemController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }

}
