package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictManager;
import com.yihu.ehr.dict.service.common.ConventionalDictEntry;
import com.yihu.ehr.dict.service.common.Tags;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/sys_dict")
@Api(protocols = "https", value = "conventional_dict", description = "系统字典接口", tags = {"系统字典接口"})
public class SystemDictController extends BaseRestController {

    @Autowired
    private ConventionalDictEntry conventionalDictEntry;


    @Autowired
    private SystemDictManager systemDictManager;


    @RequestMapping("createDict")
    @ResponseBody
    public Object createDict(String name, String reference, String userId) {

        Result result = new Result();

        boolean isExist = systemDictManager.isExistDict(name);
        if (isExist) {
            return "falid";
        }
        SystemDict systemDict = systemDictManager.createDict(name, reference, userId);
        if (systemDict == null) {
            return "faild";
        }
        return "success";
    }

    @RequestMapping("deleteDict")
    @ResponseBody
    public Object deleteDict(long dictId) {
        systemDictManager.deleteDict(dictId);
        return "success";
    }

    @RequestMapping("updateDict")
    @ResponseBody
    public Object updateDict(long dictId, String name) {
        SystemDict systemDict = systemDictManager.getDict(dictId);

        systemDict.setName(name);
        systemDictManager.updateDict(systemDict);
        return "success";
    }

    /**
     * 1-1 根据查询条件查询系统字典。
     * {
     * "name"  : "App分类"
     * }
     *
     * @param searchNm
     * @return
     */
    @RequestMapping("searchSysDicts")
    @ResponseBody
    public Object searchSysDicts(String searchNm, int page, int rows) {

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("name",searchNm);
        conditionMap.put("phoneticCode", searchNm);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        List<SystemDict> systemDicts = systemDictManager.searchSysDicts(conditionMap);
        Integer totalCount = systemDictManager.searchAppsInt(conditionMap);
        Result result = new Result();
        result.setObj(systemDicts);
        result.setTotalCount(totalCount);
        return result;
    }

    @RequestMapping("createDictEntry")
    @ResponseBody
    public Object createDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {

        Result result = new Result();
        SystemDict systemDict = systemDictManager.getDict(dictId);
        if (systemDict == null) {
            return "该字典不存在";
        }
        if (systemDictManager.containEntry(code)) {
            return "字典代码重复";
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
        systemDictEntry.setSort(sort);
        systemDictEntry.setCatalog(catalog);

        systemDictManager.createDictEntry(systemDictEntry);
        if (systemDictEntry == null) {
            return "创建失败";
        }
        return "success";
    }

    @RequestMapping("deleteDictEntry")
    @ResponseBody
    public Object deleteDictEntry(long dictId, String code) {

        SystemDict systemDict = systemDictManager.getDict(dictId);

        if (systemDict == null) {
            return "字典不存在";
        }
        if (!systemDictManager.containEntry(code)) {
            return "字典名称不可用";
        }
        systemDictManager.deleteDictEntry(dictId,code);
        return "success";
    }

    @RequestMapping("updateDictEntry")
    @ResponseBody
    public Object updateDictEntry(Long dictId, String code, String value, Integer sort, String catalog) {
        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setCode(code);
        systemDictEntry.setValue(value);
        systemDictEntry.setSort(sort);
        systemDictEntry.setCatalog(catalog);
        systemDictManager.saveSystemDictEntry(systemDictEntry);
        return systemDictEntry;
    }

    @RequestMapping("searchDictEntryListForManage")
    @ResponseBody
    public Object searchDictEntryList(Long dictId, Integer page, Integer rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("dictId", dictId);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);

        Map<String, Object> infoMap = null;
        infoMap = systemDictManager.searchEntryList(conditionMap);
        List<SystemDictEntry> systemDictEntryList = (List<SystemDictEntry>) infoMap.get("SystemDictEntry");

        Integer totalCount = (Integer) infoMap.get("totalCount");
        Result result = new Result();
        result.setObj(systemDictEntryList);
        result.setTotalCount(totalCount);
        return result;
    }

    @RequestMapping("selecttags")
    @ResponseBody
    public Object selectTags() {
        List<Tags> tags = conventionalDictEntry.getTagsList();
        return tags;
    }

    @RequestMapping("searchDictEntryList")
    @ResponseBody
    public Object searchDictEntryListForDDL(Long dictId, Integer page, Integer rows) {

        Result result = new Result();
        SystemDict systemDict = systemDictManager.getDict(dictId);

        if (systemDictManager.getEntryList(dictId).size() == 0) {

            result.setSuccessFlg(true);
            return result.toJson();
        }

        List<SystemDictEntry> systemDictEntrys = systemDictManager.getEntryList(dictId);
        return systemDictEntrys;
    }
}
