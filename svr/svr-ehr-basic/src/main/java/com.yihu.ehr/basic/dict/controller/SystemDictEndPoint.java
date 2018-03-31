package com.yihu.ehr.basic.dict.controller;

import com.yihu.ehr.basic.dict.service.SystemDictService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.phonics.PinyinUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by linaz on 2015/8/12.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Dictionary", description = "系统全局字典管理", tags = {"系统字典-系统全局字典管理"})
public class SystemDictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private SystemDictService dictService;

    @ApiOperation(value = "获取字典列表", response = MSystemDict.class, responseContainer = "List")
    @RequestMapping(value = ServiceApi.SystemDict.Crud, method = RequestMethod.GET)
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
            HttpServletResponse response) throws Exception {
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

    @ApiOperation(value = "创建字典", response = SystemDict.class)
    @RequestMapping(value = ServiceApi.SystemDict.Crud, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop createDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody SystemDict dict) throws IOException {
        if (StringUtils.isEmpty(dict.getName())) {
            return failed("字典名称不能为空");
        }
        if (StringUtils.isEmpty(dict.getPhoneticCode())) {
            dict.setPhoneticCode(PinyinUtil.getPinYinHeadChar(dict.getName(), true));
        }
        if (StringUtils.isEmpty(dict.getCode())) {
            dict.setCode(dict.getPhoneticCode());
        }
        SystemDict systemDict = dictService.createDict(dict);
        return success(systemDict);
    }

    @ApiOperation(value = "获取字典", response = MSystemDict.class)
    @RequestMapping(value = ServiceApi.SystemDict.FindById, method = RequestMethod.GET)
    public SystemDict getDictionary(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id) {
        SystemDict dict = dictService.retrieve(id);
        if (dict == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "字典不存在");
        }
        return dict;
    }

    @ApiOperation(value = "获取字典", response = SystemDict.class)
    @RequestMapping(value = ServiceApi.SystemDict.FindByPhoneticCode, method = RequestMethod.GET)
    public SystemDict getDictionaryByPhoneticCode(
            @ApiParam(name = "phoneticCode", value = "拼音编码", required = true)
            @PathVariable(value = "phoneticCode") String phoneticCode) {
        SystemDict dict = dictService.findByPhoneticCode(phoneticCode);
        if (dict == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "字典不存在");
        }
        return dict;
    }

    @ApiOperation(value = "更新字典")
    @RequestMapping(value = ServiceApi.SystemDict.Crud, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Envelop updateDictionary(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody SystemDict dict) throws Exception {
        SystemDict oldDict = dictService.findOne(dict.getId());
        if (null == oldDict) {
            return failed("字典不存在", ErrorCode.OBJECT_NOT_FOUND.value());
        }
        if (StringUtils.isEmpty(dict.getName())) {
            return failed("字典名称不能为空");
        }
        if (!oldDict.getName().equals(dict.getName()) && dictService.findByField("name", dict.getName()).size() > 0) {
            return failed("字典名称在系统中已存在");
        }
        if (StringUtils.isEmpty(dict.getPhoneticCode())) {
            dict.setPhoneticCode(PinyinUtil.getPinYinHeadChar(dict.getName(), true));
        }
        if (StringUtils.isEmpty(dict.getCode())) {
            dict.setCode(dict.getPhoneticCode());
        }
        oldDict.setName(dict.getName());
        oldDict.setPhoneticCode(dict.getPhoneticCode());
        oldDict.setCode(dict.getCode());
        dictService.updateDict(oldDict);
        return success(oldDict);
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = ServiceApi.SystemDict.DeleteById, method = RequestMethod.DELETE)
    public Boolean deleteDictionary(
            @ApiParam(name = "id", value = "字典ID")
            @PathVariable(value = "id") long id) throws Exception{
        dictService.deleteDict(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.SystemDict.CheckName, method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isDictNameExists(
            @ApiParam(name = "dict_name", value = "字典名称")
            @RequestParam(value = "dict_name") String dictName){
        return dictService.isDictNameExists(dictName);
    }

    @RequestMapping(value = ServiceApi.SystemDict.CheckCode, method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典编码是否已经存在")
    public boolean checkCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code") String code) {
        if (dictService.findByField("code", code).size() > 0) {
            return true;
        }
        return false;
    }

}
