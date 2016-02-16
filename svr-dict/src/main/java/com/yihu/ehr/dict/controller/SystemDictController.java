package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linaz on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "Dictionary", description = "系统全局字典管理", tags = {"系统字典"})
public class SystemDictController extends BaseRestController {
    @Autowired
    SystemDictService systemDictService;

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    private Envelop getDictionaries(
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

        List<SystemDict> systemDicts = systemDictService.getDictionaries(conditionMap);
        Integer totalCount = systemDictService.dictCount(conditionMap);
        return null;// getResult(systemDicts, totalCount, page, rows);
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public Object createDictionary(
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

    @ApiOperation(value = "获取字典", response = MSystemDict.class, produces = "application/json")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public Object getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id){
        SystemDict dict = systemDictService.getDict(id);
        if (dict == null) throw new ApiException(ErrorCode.DeleteDictFailed, "字典不存在");

        return convertToModel(dict, MSystemDict.class);
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.PUT)
    public Object updateDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "name", value = "字典名称", defaultValue = "")
            @RequestParam(value = "name") String name) {
        SystemDict systemDict = systemDictService.getDict(id);
        systemDict.setName(name);
        systemDictService.updateDict(systemDict);

        return systemDict;
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public Object deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        systemDictService.deleteDict(id);
        return true;
    }

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.GET)
    public Object getDictEntries(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("id", id);

        List<SystemDictEntry> systemDictEntryList = null;
        if (page != null && rows != null){
            conditionMap.put("page", page);
            conditionMap.put("rows", rows);

            systemDictEntryList = systemDictService.searchEntryList(conditionMap);
        } else {
            systemDictEntryList = systemDictService.getDictEntries(id, value);
        }

        return getResult(systemDictEntryList, systemDictEntryList.size());
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries", method = RequestMethod.POST)
    public Object createDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "字典名称", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "sort", value = "排序号", defaultValue = "")
            @RequestParam(value = "sort") Integer sort,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog) {
        SystemDict systemDict = systemDictService.getDict(id);
        if (systemDict == null) {
            return null;
        }

        if (systemDictService.isDictContainEntry(id, code)) {
            return null;
        }

        int nextSort;
        if (sort != null) {
            nextSort = sort;
        } else {
            nextSort = systemDictService.getNextSort(id);
        }

        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode(code);
        systemDictEntry.setValue(value);
        systemDictEntry.setSort(nextSort);
        systemDictEntry.setCatalog(catalog);
        systemDictService.createDictEntry(systemDictEntry);

        return systemDictEntry;
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.POST)
    public MDictionaryEntry getDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        SystemDictEntry systemDictEntry = systemDictService.getDictEntry(id, code);

        return convertToModel(systemDictEntry, MDictionaryEntry.class);
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {

        systemDictService.deleteDictEntry(id, code);
        return true;
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/{id}/entries/{code}", method = RequestMethod.PUT)
    public Object updateDictEntry(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id,
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
}
