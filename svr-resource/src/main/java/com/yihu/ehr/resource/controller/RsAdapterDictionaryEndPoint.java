package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import com.yihu.ehr.resource.model.RsAdapterDictionary;
import com.yihu.ehr.resource.service.RsAdapterDictionaryService;
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
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 )
@Api(value = "adapterDictionaries", description = "AdapterDictionary服务接口")
public class RsAdapterDictionaryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsAdapterDictionaryService adapterDictionaryService;


    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取适配字典列表", notes = "根据查询条件获取适配字典列表")
    public List<MRsAdapterDictionary> searchRsAdapterDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsAdapterDictionary> adapterDictionaries = adapterDictionaryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, adapterDictionaryService.getCount(filters), page, size);

        return (List<MRsAdapterDictionary>) convertToModels(adapterDictionaries, new ArrayList<MRsAdapterDictionary>(adapterDictionaries.size()), MRsAdapterDictionary.class, fields);
    }



    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建适配字典", notes = "创建适配字典")
    public MRsAdapterDictionary createRsAdapterDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsAdapterDictionary adapterDictionary = toEntity(jsonData, RsAdapterDictionary.class);
        adapterDictionary.setId(getObjectId(BizObject.RsAdapterDictionary));
        adapterDictionaryService.save(adapterDictionary);
        return convertToModel(adapterDictionary, MRsAdapterDictionary.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改适配字典", notes = "修改适配字典")
    public MRsAdapterDictionary updateRsAdapterDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsAdapterDictionary adapterDictionary = toEntity(jsonData, RsAdapterDictionary.class);
        adapterDictionaryService.save(adapterDictionary);
        return convertToModel(adapterDictionary, MRsAdapterDictionary.class, null);
    }


    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配字典", notes = "删除适配字典")
    public boolean deleteRsAdapterDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        adapterDictionaryService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取适配字典")
    public MRsAdapterDictionary getRsAdapterDictionaryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @RequestParam(value = "id") String id) {
        RsAdapterDictionary adapterDictionary = adapterDictionaryService.findById(id);
        return convertToModel(adapterDictionary, MRsAdapterDictionary.class);
    }

    @RequestMapping(value = ServiceApi.Resources.AdapterDictsBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建适配字典", notes = "批量创建适配字典")
    public boolean createRsAdapterDictionaries(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        List<RsAdapterDictionary> dictionaries = toEntity(jsonData,List.class);
        adapterDictionaryService.batchInsert(dictionaries);
        return true;
    }

}