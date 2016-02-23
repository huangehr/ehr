package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dict.service.DictService;
import com.yihu.ehr.standard.dict.service.IDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/std/")
@Api(protocols = "https", value = "dict", description = "标准字典", tags = {"标准字典", "标准字典项"})
public class DictController extends ExtendController<MStdDict> {

    @Autowired
    private DictService dictService;

    private Class getServiceEntity(String version){
        return dictService.getServiceEntity(version);
    }

    private void setValues(IDict dict, String code, String name,
                           long baseDict, String stdSource, String stdVersion, String description){

        dict.setStdVersion(stdVersion);
        dict.setCode(code);
        dict.setBaseDict(baseDict);
        dict.setDescription(description);
        dict.setName(name);
        dict.setSourceId(stdSource);
    }


    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    public Collection searchDataSets(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        Class entityClass = getServiceEntity(version);
        List ls = dictService.search(entityClass, fields, filters, sorts, page, size);
        pagedResponse(request, response, dictService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<MStdDict>(ls.size()), MStdDict.class, fields.split(","));
    }


    @RequestMapping(value = "/dict", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典")
    public boolean addDict(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "stdSource", value = "标准来源", defaultValue = "")
            @RequestParam(value = "stdSource") String stdSource,
            @ApiParam(name = "stdVersion", value = "标准版本", defaultValue = "")
            @RequestParam(value = "stdVersion") String stdVersion,
            @ApiParam(name = "baseDict", value = "继承字典", defaultValue = "")
            @RequestParam(value = "baseDict", required = false) long baseDict,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "用户编号", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{

        Class entityClass = getServiceEntity(stdVersion);
        if(dictService.isExistByField("code", code, entityClass))
            throw errRepeatCode();

        IDict dict = (IDict) getServiceEntity(stdVersion).newInstance();
        dict.setCreatedate(new Date());
        dict.setAuthor(userId);
        setValues(dict, code, name, baseDict, stdSource, stdVersion, description);
        return dictService.add(dict);
    }

    @RequestMapping(value = "/dict/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "修改字典")
    public boolean updateDict(
            @ApiParam(name = "id", value = "代码", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "stdSource", value = "标准来源", defaultValue = "")
            @RequestParam(value = "stdSource") String stdSource,
            @ApiParam(name = "stdVersion", value = "标准版本", defaultValue = "")
            @RequestParam(value = "stdVersion") String stdVersion,
            @ApiParam(name = "baseDict", value = "继承字典", defaultValue = "")
            @RequestParam(value = "baseDict") long baseDict,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "userId", value = "用户编号", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{

        Class entityClass = getServiceEntity(stdVersion);
        IDict dict = dictService.retrieve(id, entityClass);
        if(!dict.getCode().equals(code) &&
                dictService.isExistByField("code", code, entityClass))
            throw errRepeatCode();

        setValues(dict, code, name, baseDict, stdSource, stdVersion, description);
        dictService.save(dict);
        return true;
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    public boolean deleteDict(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

        return dictService.removeDicts(new Object[]{id}, version) > 0;
    }

    @RequestMapping(value = "/dicts", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典")
    public boolean deleteDicts(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        return dictService.removeDicts(ids.split(","), version) > 0;
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据CdaVersion及字典ID查询相应版本的字典详细信息")
    public Object getCdaDictInfo(
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return getModel(dictService.retrieve(id, getServiceEntity(version)));
    }
}
