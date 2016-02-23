package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MStdDictEntry;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dict.service.DictEntryService;
import com.yihu.ehr.standard.dict.service.IDictEntry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/std/dict")
@Api(protocols = "https", value = "/entry", description = "标准字典项", tags = {"标准字典项"})
public class DictEntryController extends ExtendController<MStdDictEntry> {

    @Autowired
    private DictEntryService dictEntryService;

    private Class getServiceEntity(String version){
        return dictEntryService.getServiceEntity(version);
    }

    private void setValues(IDictEntry dictEntry, long dictId, String code, String value, String desc){
        dictEntry.setCode(code);
        dictEntry.setDesc(desc);
        dictEntry.setDictId(dictId);
        dictEntry.setValue(value);
    }

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项")
    public boolean updateDictEntry(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "值", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "desc", value = "描述", defaultValue = "")
            @RequestParam(value = "desc") String desc) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDictEntry dictEntry = dictEntryService.retrieve(id, entityClass);
        if(!dictEntry.getCode().equals(code)
                && dictEntryService.isExistByField("code", code, entityClass))
            throw errRepeatCode();

        setValues(dictEntry, dictId, code, value, desc);
        dictEntryService.save(dictEntry);
        return true;
    }


    @RequestMapping(value = "/entry", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    public boolean addDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "value", value = "值", defaultValue = "")
            @RequestParam(value = "value") String value,
            @ApiParam(name = "desc", value = "描述", defaultValue = "")
            @RequestParam(value = "desc") String desc) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDictEntry dictEntry = (IDictEntry) entityClass.newInstance();
        if(dictEntryService.isExistByField("code", code, entityClass))
            throw errRepeatCode();

        setValues(dictEntry, dictId, code, value, desc);
        return dictEntryService.add(dictEntry, version);
    }


    @RequestMapping(value = "/entry", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public boolean deleteDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "id") long id) throws Exception{

        return dictEntryService.deleteByFields(
                    new String[]{"dictId", "id"},
                    new Object[]{dictId, id},
                    getServiceEntity(version)) > 0;
    }

    @RequestMapping(value = "/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public boolean deleteDictEntrys(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "ids", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        return dictEntryService.deleteByFields(
                    new String[]{"dictId", "id"},
                    new Object[]{dictId, ids.split(",")},
                    getServiceEntity(version)
                ) > 0;
    }

    @RequestMapping(value = "/{dictId}/entry", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    public boolean deleteDictEntryList(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") String dictId) throws Exception{

        return dictEntryService.deleteByField("dictId", dictId, getServiceEntity(version)) > 0;
    }


    @RequestMapping(value = "/entrys", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
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
        List ls = dictEntryService.search(entityClass, fields, filters, sorts, page, size);
        pagedResponse(request, response, dictEntryService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<MStdDictEntry>(ls.size()), MStdDictEntry.class, fields.split(","));
    }

    @RequestMapping(value = "/entry/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    public MStdDictEntry getDictEntry(
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return getModel(dictEntryService.retrieve(id, getServiceEntity(version)));
    }

}
