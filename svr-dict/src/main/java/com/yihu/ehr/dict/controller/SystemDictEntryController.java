package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.PageArg;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.15 18:25
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "DictionaryEntry", description = "系统全局字典项管理", tags = {"系统字典项"})
public class SystemDictEntryController extends BaseRestController {
    @Autowired
    SystemDictService dictService;

    @Autowired
    SystemDictEntryService systemDictEntryService;

    @ApiOperation(value = "获取字典项列表")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries", method = RequestMethod.GET)
    public Collection<MDictionaryEntry> getDictEntries(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") Long dictId,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false, defaultValue = PageArg.DefaultSizeS) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false, defaultValue = PageArg.DefaultPageS) int page,
            HttpServletRequest request,
            HttpServletResponse response) {
        page = reducePage(page);

        Page<SystemDictEntry> p = systemDictEntryService.findByDictId(dictId, page, size);

        pagedResponse(request, response, p.getTotalElements(), page, size);

        return convertToModels(p.getContent(), new ArrayList<MDictionaryEntry>(p.getNumber()), MDictionaryEntry.class, null);
    }

    @ApiOperation(value = "创建字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries", method = RequestMethod.POST)
    public MDictionaryEntry createDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);

        SystemDict systemDict = dictService.retrieve(entry.getDictId());
        if (systemDict == null) throw new ApiException(ErrorCode.GetDictFaild, "所属字典不存在");

        if (systemDictEntryService.isDictContainEntry(entry.getDictId(), entry.getCode())){
            throw new ApiException(ErrorCode.InvalidSysDictEntry, "字典项代码已存在");
        }
        int nextSort = systemDictEntryService.getNextSN(entry.getDictId());
        entry.setSort(nextSort);
        SystemDictEntry systemDictEntry = new SystemDictEntry();
        systemDictEntry.setSort(nextSort);
        systemDictEntryService.createDictEntry(entry);

        return convertToModel(systemDictEntry, MDictionaryEntry.class, null);
    }

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.POST)
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
    public void deleteDictEntry(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId,
            @ApiParam(name = "code", value = "字典ID", defaultValue = "")
            @PathVariable(value = "code") String code) {
        SystemDict systemDict = dictService.retrieve(dictId);
        if (systemDict == null) {
            return;
        }

        if (!systemDictEntryService.isDictContainEntry(dictId, code)) {
            return;
        }

        systemDictEntryService.deleteDictEntry(dictId, code);
    }

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.PUT)
    public MDictionaryEntry updateDictEntry(
            @ApiParam(name = "entry", value = "字典JSON结构")
            @RequestParam(value = "entry") String entryJson) {
        SystemDictEntry entry = toEntity(entryJson, SystemDictEntry.class);
        SystemDictEntry temp = systemDictEntryService.retrieve(new DictEntryKey(entry.getCode(), entry.getDictId()));
        if(null == temp){
            throw new ApiException(ErrorCode.InvalidSysDictEntry, "字典项不存在");
        }

        systemDictEntryService.saveDictEntry(entry);

        return convertToModel(entry, MDictionaryEntry.class, null);
    }

    @ApiOperation(value = "搜索字典项列表")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/search", method = RequestMethod.GET)
    public Collection<MDictionaryEntry> searchDictEntries(
            @ApiParam(name = "dict_id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "dict_id") Long dictId,
            @ApiParam(name = "value", value = "字典项值", defaultValue = "")
            @RequestParam(value = "value", required = false) String value,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false, defaultValue = "15") Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            HttpServletRequest request,
            HttpServletResponse response) {
        page = reducePage(page);

        Page<SystemDictEntry> p = systemDictEntryService.findByDictIdAndValueLike(dictId, "%" + value + "%", page, size);

        pagedResponse(request, response, p.getTotalElements(), page, size);

        return convertToModels(p.getContent(), new ArrayList<>(p.getNumber()), MDictionaryEntry.class, null);
    }
}
