package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@EnableFeignClients
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion)
@Api(value = "system_dict", description = "系统字典接口，用于系统全局字典管理", tags = {"系统字典接口"})
public class SystemDictController extends BaseRestController {
    @Autowired
    private SystemDictService systemDictService;

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/system_dict", method = RequestMethod.POST)
    public Object createDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "reference", value = "", defaultValue = "")
            @RequestParam(value = "reference") String reference,
            @ApiParam(name = "userId", value = "创建人", defaultValue = "")
            @RequestParam(value = "userId") String userId) {
        boolean isExist = systemDictService.isDictExists(name);
        if(isExist) throw new ApiException(ErrorCode.InvalidCreateSysDict, name);

        SystemDict systemDict = systemDictService.createDict(name, reference, userId);
        return convertToModel(systemDict, MSystemDict.class, null);
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/system_dict/{dict_id}", method = RequestMethod.DELETE)
    public Object deleteDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId) {
        systemDictService.deleteDict(dictId);
        return "success";
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/system_dict/{dict_id}", method = RequestMethod.PUT)
    public Object updateDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        SystemDict systemDict = systemDictService.getDict(dictId);
        systemDict.setName(name);
        systemDictService.updateDict(systemDict);

        return systemDict;
    }

    /**
     * * 1-1 根据查询条件查询系统字典。
     * {
     * "name"  : "App分类"
     *
     * @param name
     * @param phoneticCode
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation(value = "查询字典列表")
    @RequestMapping(value = "/system_dict/search", method = RequestMethod.GET)
    public Object searchSystemDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "phoneticCode", value = "首字母全拼", defaultValue = "")
            @RequestParam(value = "phoneticCode") String phoneticCode,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("name", name);
        conditionMap.put("phoneticCode", phoneticCode);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);

        List<SystemDict> systemDicts = systemDictService.searchDict(conditionMap);
        Integer totalCount = systemDictService.dictCount(conditionMap);
        return getResult(systemDicts, totalCount, page, rows);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry", method = RequestMethod.POST)
    public Object createDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog) {
        SystemDict systemDict = systemDictService.getDict(dictId);
        if (systemDict == null) {
            return null;
        }

        if (systemDictService.isDictContainEntry(dictId, code)) {
            return null;
        }

        int nextSort;
        if (sort != null) {
            nextSort = sort;
        } else {
            nextSort = systemDictService.getNextSort(dictId);
        }

        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode(code);
        systemDictEntry.setValue(value);
        systemDictEntry.setSort(nextSort);
        systemDictEntry.setCatalog(catalog);
        systemDictService.createDictEntry(systemDictEntry);

        return systemDictEntry;
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {
        SystemDict systemDict = systemDictService.getDict(dictId);
        if (systemDict == null) {
            return null;
        }

        if (!systemDictService.isDictContainEntry(dictId, code)) {
            return null;
        }

        systemDictService.deleteDictEntry(dictId, code);
        return "success";
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/{code}", method = RequestMethod.PUT)
    public Object updateDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog) {
        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode(code);
        systemDictEntry.setValue(value);
        systemDictEntry.setSort(sort);
        systemDictEntry.setCatalog(catalog);
        systemDictService.saveSystemDictEntry(systemDictEntry);
        return systemDictEntry;
    }

    @ApiOperation(value = "字典项搜索")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/search", method = RequestMethod.GET)
    public Object searchDictEntries(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("dictId", dictId);

        List<SystemDictEntry> systemDictEntryList = null;
        if (page != null && rows != null){
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);
            systemDictEntryList = systemDictService.searchEntryList(conditionMap);
        } else {
            systemDictEntryList = systemDictService.getDictEntries(dictId, value);
        }

        return getResult(systemDictEntryList, systemDictEntryList.size(), page, rows);
    }
}
