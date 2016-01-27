package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.dict.service.*;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Lincl on 2015/8/12.
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
    private StandardSourceManager standardSourceManager;
    @Autowired
    private DictEntryManager dictEntryManager;
    @Autowired
    private DictManager dictManager;


    /**
     * 新增或更新字典数据。
     *
     * @return
     */
    @RequestMapping("saveDict")
    @ApiOperation(value = "保存字典")
    public Object saveDict(
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

    @RequestMapping("deleteDict")
    @ApiOperation(value = "删除字典")
    public Object deleteDict(
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


    @RequestMapping("getCdaDictList")
    @ApiOperation(value = "根据CdaVersion查询相应版本的字典数据")
    public Object getCdaDictList(
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

    @RequestMapping(value = "/getCdaDictInfo")
    @ApiOperation(value = "根据CdaVersion及字典ID查询相应版本的字典详细信息")
    public Object getCdaDictInfo(
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





    @RequestMapping("getCdaBaseDictList")
    @ApiOperation(value = "根据输入条件，查询字典List(过滤掉当前字典)")
    public Object getCdaBaseDictList(
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


    @RequestMapping("searchCdaBaseDictList")
    @ApiOperation(value = "根据输入条件，查询字典List(过滤掉当前字典)")
    public Object searchCdaBaseDictList(
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

    @RequestMapping("getLastCdaVersion")
    @ApiOperation(value = "查询最新版本的CdaVersion，用于初始化查询字典数据")
    public Object getLastCdaVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
//        try {
//            String strJson = "";
//
//            XCDAVersion cdaVersion = cdaVersionManager.getLatestVersion();
//
//            if (cdaVersion != null) {
//
//                CDAVersionForInterface info = new CDAVersionForInterface();
//
//                info.setVersion(cdaVersion.getVersion());
//                info.setAuthor(cdaVersion.getAuthor());
//                info.setBaseVersion(cdaVersion.getBaseVersion());
//                info.setCommitTime(cdaVersion.getCommitTime());
//
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                strJson = objectMapper.writeValueAsString(info);
//            }
//
//            RestEcho echo = new RestEcho().success();
//            echo.putResultToList(strJson);
//
//            return echo;
//
//        } catch (Exception ex) {
//            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
//            return failed(ErrorCode.GetCDAVersionListFailed);
//        }
        return "";
    }

    @RequestMapping("saveDictEntry")
    @ApiOperation(value = "保存字典项")
    public Object saveDictEntry(
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
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(SAVE_FAILED);
        }
        return result;
    }

    @RequestMapping("deleteDictEntry")
    @ApiOperation(value = "删除字典项")
    public Object deleteDictEntry(
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
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DEL_FAILED);
        }
        return result;
    }


    @RequestMapping("deleteDictEntryList")
    @ApiOperation(value = "删除字典关联的所有字典项")
    public Object deleteDictEntryList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersion", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getVersion(cdaVersion);
            if (xcdaVersion == null)
                return failed(VERSION_NOT_FINT);
            List<String> ids = new ArrayList<>();
            ids.add(id);
            int resultFlag = dictEntryManager.delDictEntryList(xcdaVersion, ids);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DEL_FAILED);
        }
        return result;
    }


    @RequestMapping("searchDictEntryList")
    @ApiOperation(value = "查询字典项列表")
    public Object searchDictEntryList(
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
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }

    @RequestMapping("getDictEntry")
    @ApiOperation(value = "获取字典项")
    public Object getDictEntry(
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
            LogService.getLogger(DictController.class).error(ex.getMessage());
            result.setSuccessFlg(false);
            result.setErrorMsg(DATA_LOAD_FAILED);
        }
        return result;
    }


    @RequestMapping("getCdaVersionList")
    @ApiOperation(value = "查询CdaVersion用于下拉框赋值(请调用cdaVersion/getVersionList)")
    public Object getCdaVersionList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion) {
//        try {
//            String strJson = "";
//
//            CDAVersion[] cdaVersions = cdaVersionManager.getVersionList();
//
//            if (cdaVersions != null) {
//                CDAVersionForInterface[] infos = new CDAVersionForInterface[cdaVersions.length];
//                int i = 0;
//                for (XCDAVersion xcdaVersion : cdaVersions) {
//                    CDAVersionForInterface info = new CDAVersionForInterface();
//
//                    info.setVersion(xcdaVersion.getVersion() + ',' + xcdaVersion.getVersionName());
//                    info.setAuthor(xcdaVersion.getAuthor());
//                    info.setBaseVersion(xcdaVersion.getBaseVersion());
//                    info.setCommitTime(xcdaVersion.getCommitTime());
//
//                    infos[i] = info;
//                    i++;
//                }
//                ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
//                strJson = objectMapper.writeValueAsString(infos);
//            }
//
//            RestEcho echo = new RestEcho().success();
//            echo.putResultToList(strJson);
//
//            return echo;
//
//        } catch (Exception ex) {
//            LogService.getLogger(StdManagerRestController.class).error(ex.getMessage());
//            return failed(ErrorCode.GetCDAVersionListFailed);
//        }
        return "";
    }


    @RequestMapping("getStdSourceList")
    @ApiOperation(value = "查询StdSource用于下拉框赋值")
    public Object getStdSourceList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersionCode", value = "版本编号", defaultValue = "")
            @RequestParam(value = "strVersionCode") String strVersionCode) {
        String strJson = "";
//        try {
//            if (strVersionCode == null || strVersionCode.equals("")) {
//                return missParameter("version_code");
//            }
//
//            XStandardSource[] xStandardSources = xStandardSourceManager.getSourceList();
//
//            if (xStandardSources == null) {
//                return failed(ErrorCode.GetStandardSourceFailed);
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
//        return results;
//    }

//    public void exportToExcel(){
//        //todo：test 导出测试
//        XDict[] dicts = dictManager.getDictList(0, 0,cdaVersionManager.getLatestVersion());
//        dictManager.exportToExcel("E:/workspaces/excel/testExport.xls", dicts);
//    }

//    public void importFromExcel(){
//        //todo：test导入测试
//        dictManager.importFromExcel("E:/workspaces/excel/测试excel导入.xls", cdaVersionManager.getLatestVersion());
//
//    }

}
