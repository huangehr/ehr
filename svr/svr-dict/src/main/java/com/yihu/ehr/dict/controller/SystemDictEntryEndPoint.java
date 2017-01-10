package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.15 18:25
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "DictionaryEntry", description = "系统全局字典项管理", tags = {"系统字典项"})
public class SystemDictEntryEndPoint extends EnvelopRestEndPoint {
    @Autowired
    SystemDictService dictService;

    @Autowired
    SystemDictEntryService systemDictEntryService;

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.GET)
    public List<MDictionaryEntry> getDictEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
//        page = reducePage(page);

//        Page<SystemDictEntry> p = null;
//        if (StringUtils.isEmpty(value)) {
//            p = systemDictEntryService.findByDictIdAndValueLike(dictId, "%" + value + "%", page, size);
//        } else {
//            p = systemDictEntryService.findByDictId(dictId, page, size);
//        }

        List<SystemDictEntry> systemDictEntryList = systemDictEntryService.search(fields,filters,sorts,page,size);
        pagedResponse(request, response,systemDictEntryService.getCount(filters), page, size);
        return (List<MDictionaryEntry>)convertToModels(systemDictEntryList,new ArrayList<MDictionaryEntry>(systemDictEntryList.size()),MDictionaryEntry.class,null);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MDictionaryEntry createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestBody String entryJson) {
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);
        SystemDict systemDict = dictService.retrieve(entry.getDictId());
        if (systemDict == null) throw new ApiException(ErrorCode.GetDictFaild, "所属字典不存在");
        int nextSort = systemDictEntryService.getNextSN(entry.getDictId());
        entry.setSort(nextSort);
        systemDictEntryService.createDictEntry(entry);

        return convertToModel(entry, MDictionaryEntry.class, null);
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.GET)
    public MDictionaryEntry getDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典项代码", defaultValue = "")
            @PathVariable(value = "code") String code) {
        SystemDictEntry systemDictEntry = systemDictEntryService.getDictEntry(dictId, code);

        return convertToModel(systemDictEntry, MDictionaryEntry.class);
    }

    @ApiOperation(value = "删除字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.DELETE)
    public Object deleteDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) throws Exception{

        systemDictEntryService.deleteDictEntry(dictId, code);
        return true;
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MDictionaryEntry updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestBody String entryJson) {
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);
        SystemDictEntry temp = systemDictEntryService.retrieve(new DictEntryKey(entry.getCode(), entry.getDictId()));
        if (null == temp) {
            throw new ApiException(ErrorCode.InvalidSysDictEntry, "字典项不存在");
        }

        systemDictEntryService.saveDictEntry(entry);

        return convertToModel(entry, MDictionaryEntry.class, null);
    }

    @RequestMapping(value = "/dictionaries/existence/{dict_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "根基dictId和code判断提交的字典项名称是否已经存在")
    public boolean isDictEntryCodeExists(
            @ApiParam(name = "dict_id", value = "dict_id", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code){
        return systemDictEntryService.isDictContainEntry(dictId, code);
    }
}
