package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictManager;
import com.yihu.ehr.dict.service.common.ConventionalDict;
import com.yihu.ehr.dict.service.common.ConventionalDictEntry;
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
@Api(value = "conventional_dict", description = "系统字典接口", tags = {"系统字典接口"})
public class SystemDictController extends BaseRestController {

    @Autowired
    private ConventionalDictEntry conventionalDictEntry;

    @Autowired
    private SystemDictManager systemDictManager;

    @ApiOperation(value = "根据字典名称判断字典是否已存在")
    @RequestMapping("/system_dict/exists")
    public Object validator(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        boolean isExist = systemDictManager.isExistDict(name);
        return isExist;
    }

    @ApiOperation(value = "创建字典")
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
        boolean isExist = systemDictManager.isExistDict(name);
        if (isExist) {
            return "faild";
        }
        SystemDict systemDict = systemDictManager.createDict(name, reference, userId);
        return systemDict;
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/system_dict", method = RequestMethod.DELETE)
    public Object deleteDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId) {
        systemDictManager.deleteDict(dictId);
        return "success";
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/system_dict", method = RequestMethod.PUT)
    public Object updateDict(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        SystemDict systemDict = systemDictManager.getDictEntries(dictId);
        systemDict.setName(name);
        systemDictManager.updateDict(systemDict);
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
    public Object searchSysDicts(
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

        List<SystemDict> systemDicts = systemDictManager.searchSysDicts(conditionMap);
        Integer totalCount = systemDictManager.searchAppsInt(conditionMap);
        return getResult(systemDicts, totalCount, page, rows);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry", method = RequestMethod.POST)
    public Object createDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典编号", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog) {
        SystemDict systemDict = systemDictManager.getDictEntries(dictId);
        if (systemDict == null) {
            return "系统字典不存在";
        }
        if (systemDictManager.containEntry(code)) {
            return "系统字典代码已存在";
        }
        int nextSort;
        if (sort != null) {
            nextSort = sort;
        } else {
            nextSort = systemDictManager.getNextSort(dictId);
        }
        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode(code);
        systemDictEntry.setValue(value);
        systemDictEntry.setSort(nextSort);
        systemDictEntry.setCatalog(catalog);
        systemDictManager.createDictEntry(systemDictEntry);
        return systemDictEntry;
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典编号", defaultValue = "")
            @PathVariable(value = "code") String code) {
        SystemDict systemDict = systemDictManager.getDictEntries(dictId);
        if (systemDict == null) {
            return "字典不存在";
        }
        if (!systemDictManager.containEntry(code)) {
            return "字典名称不可用";
        }
        systemDictManager.deleteDictEntry(dictId, code);
        return "success";
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/{code}", method = RequestMethod.PUT)
    public Object updateDictEntry(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典编号", defaultValue = "")
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
        systemDictManager.saveSystemDictEntry(systemDictEntry);
        return systemDictEntry;
    }

    @ApiOperation(value = "查询字典项列表")
    @RequestMapping(value = "/system_dict/{dict_id}/system_dict_entry/search", method = RequestMethod.GET)
    public Object searchDictEntryList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("dictId", dictId);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        List<SystemDictEntry> list = systemDictManager.searchEntryList(conditionMap);
        Integer totalCount = list.size();
        return getResult(list, totalCount, page, rows);
    }

    @ApiOperation(value = "根据字典编号和字典名称查询字典列表，模糊查询")
    @RequestMapping("/system_dict/{dict_id}/system_dict_entries/search")
    public Object autoSearchDictEntryList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "dict_id", value = "字典项编号", defaultValue = "")
            @PathVariable(value = "dict_id") Long dictId,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value) {

        List<SystemDictEntry> systemDictEntryList = systemDictManager.getDictEntries(dictId, value);
        return systemDictEntryList;
    }

    @ApiOperation(value = "获取标签字典项列表")
    @RequestMapping(value = "/system_dict/tags", method = RequestMethod.GET)
    public Object selectTags() {
        List<ConventionalDict> tags = conventionalDictEntry.getTagsList();
        return tags;
    }
}
