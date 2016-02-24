package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.SystemDict;
import com.yihu.ehr.dict.service.SystemDictService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by linaz on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "Dictionary", description = "系统全局字典管理", tags = {"系统字典"})
public class SystemDictController extends BaseRestController {
    @Autowired
    SystemDictService dictService;

    @ApiOperation(value = "获取字典列表", response = MSystemDict.class, responseContainer = "List")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.GET)
    public Collection<MSystemDict> getDictionaries(
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
            HttpServletResponse response) {
        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<SystemDict> systemDictPage = dictService.getDictList(sorts, page, size);
            pagedResponse(request, response, systemDictPage.getTotalElements(), page, size);
            return convertToModels(systemDictPage.getContent(), new ArrayList<>(systemDictPage.getNumber()), MSystemDict.class, fields);
        } else {
            List<SystemDict> systemDictList = dictService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, dictService.getCount(filters), page, size);
            return convertToModels(systemDictList, new ArrayList<>(systemDictList.size()), MSystemDict.class, fields);
        }
    }

    @ApiOperation(value = "创建字典", response = MSystemDict.class)
    @RequestMapping(value = "/dictionaries", method = RequestMethod.POST)
    public MSystemDict createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        SystemDict dict = toEntity(dictJson, SystemDict.class);
        Long id = dictService.getNextId();
        dict.setId(id);
        SystemDict systemDict = dictService.createDict(dict);
        return convertToModel(systemDict, MSystemDict.class, null);
    }

    @ApiOperation(value = "获取字典", response = MSystemDict.class)
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.GET)
    public MSystemDict getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        SystemDict dict = dictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MSystemDict.class);
    }

    @ApiOperation(value = "更新字典")
    @RequestMapping(value = "/dictionaries", method = RequestMethod.PUT)
    public MSystemDict updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {
        SystemDict dict = toEntity(dictJson, SystemDict.class);
        if (null == dictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        dictService.updateDict(dict);
        return convertToModel(dict, MSystemDict.class);
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dictionaries/{id}", method = RequestMethod.DELETE)
    public Object deleteDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{
        dictService.deleteDict(id);
        return true;
    }

    @RequestMapping(value = "/dictionaries/existence/{app_name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isAppNameExists(
            @ApiParam(name = "app_name", value = "app_name", defaultValue = "")
            @PathVariable(value = "app_name") String appName){
        return dictService.isDictNameExists(appName);
    }
}
