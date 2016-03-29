package com.yihu.ehr.standard.dict.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
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
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "entry", description = "标准字典项", tags = {"标准字典项"})
public class DictEntryController extends ExtendController<MStdDictEntry> {

    @Autowired
    private DictEntryService dictEntryService;

    private Class getServiceEntity(String version){
        return dictEntryService.getServiceEntity(version);
    }


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项")
    public MStdDictEntry updateDictEntry(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "标准版本", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDictEntry dictEntryModel = (IDictEntry) jsonToObj(model, entityClass);
//        IDictEntry dictEntry = dictEntryService.retrieve(id, entityClass);
//        if(!dictEntry.getCode().equals(dictEntryModel.getCode())
//                && dictEntryService.isExistByField("code", dictEntryModel.getCode(), entityClass))
//            throw errRepeatCode();

        dictEntryModel.setId(id);
        dictEntryService.save(dictEntryModel);
        return getModel(dictEntryModel);
    }


    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    public MStdDictEntry addDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDictEntry dictEntry = (IDictEntry) jsonToObj(model, entityClass);
//        if(dictEntryService.isExistByField("code", dictEntry.getCode(), entityClass))
//            throw errRepeatCode();
        if (dictEntryService.add(dictEntry, version))
            return getModel(dictEntry);
        return null;
    }


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public boolean deleteDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

        return dictEntryService.delete(id, getServiceEntity(version)) > 0;
    }


    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public boolean deleteDictEntrys(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        return dictEntryService.delete(strToLongArr(ids), getServiceEntity(version)) > 0;
    }


    @RequestMapping(value = RestApi.Standards.EntriesWithDictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    public boolean deleteDictEntryList(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dict_id", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dict_id") long dictId) throws Exception{

        return dictEntryService.deleteByField("dictId", dictId, getServiceEntity(version)) > 0;
    }


    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    public Collection<MStdDictEntry> searchDictEntry(
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
        return convertToModels(ls, new ArrayList<>(ls.size()), MStdDictEntry.class, fields);
    }


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    public MStdDictEntry getDictEntry(
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return getModel(dictEntryService.retrieve(id, getServiceEntity(version)));
    }

    @RequestMapping(value = RestApi.Standards.EntryCodeIsExist,method = RequestMethod.GET)
    public boolean isExistEntryCode(
            @RequestParam(value = "dict_id")long dictId,
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode) {
        Class entityClass = getServiceEntity(versionCode);
        return dictEntryService.isExistByFields(new String[]{"dictId","code"}, new Object[]{dictId,code}, entityClass);
    }
}
