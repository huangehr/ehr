package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsDictionary;
import com.yihu.ehr.resource.model.RsDictionary;
import com.yihu.ehr.resource.model.RsDictionaryEntry;
import com.yihu.ehr.resource.service.RsDictionaryEntryService;
import com.yihu.ehr.resource.service.RsDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/dictionaries")
@Api(value = "dictionaries", description = "标准字典服务接口")
public class RsDictionaryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsDictionaryService dictionaryService;

    @Autowired
    private RsDictionaryEntryService dictionaryEntryService;

    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典列表", notes = "根据查询条件获取标准字典列表")
    public List<MRsDictionary> searchRsDictionaries(
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
        List<RsDictionary> dictionaries = dictionaryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, dictionaryService.getCount(filters), page, size);

        return (List<MRsDictionary>) convertToModels(dictionaries, new ArrayList<MRsDictionary>(dictionaries.size()), MRsDictionary.class, fields);
    }



    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准字典", notes = "创建标准字典")
    public MRsDictionary createRsDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsDictionary rsDictionary = toEntity(jsonData, RsDictionary.class);
        String code = rsDictionary.getCode();
        if(isExistence(code)){
            throw new Exception("字典代码不能重复");
        }
        dictionaryService.save(rsDictionary);
        return convertToModel(rsDictionary, MRsDictionary.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改标准字典", notes = "修改标准字典")
    public MRsDictionary updateRsDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsDictionary dictionary = toEntity(jsonData, RsDictionary.class);
        dictionaryService.save(dictionary);
        return convertToModel(dictionary, MRsDictionary.class, null);
    }


    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典", notes = "删除标准字典")
    public boolean deleteRsDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {

        id = URLDecoder.decode(id, "UTF-8");
        RsDictionary dictionary = dictionaryService.findById(id);
        hasChild(dictionary);
        dictionaryService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public MRsDictionary getRsDictionaryById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) throws UnsupportedEncodingException {

        RsDictionary dictionary = dictionaryService.findById(URLDecoder.decode(id, "UTF-8"));
        return convertToModel(dictionary, MRsDictionary.class);
    }

    @RequestMapping(value = ServiceApi.Resources.DictBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建标准字典", notes = "批量创建标准字典")
    public boolean createRsDictionaries(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsDictionary[] dictionaries = toEntity(jsonData,RsDictionary[].class);
        dictionaryService.batchInsertDictionaries(dictionaries);
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.DictExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistenceFilters(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        List ls = dictionaryService.search("",filters,"", 1, 1);
        return ls!=null && ls.size()>0;
    }

    private boolean isExistence(String code) {
        return dictionaryService.findByField("code",code).size() != 0;
    }

    private void hasChild(RsDictionary dictionary) throws Exception {
        String code = dictionary.getCode();
        List<RsDictionaryEntry> dictionaryEntries = dictionaryEntryService.findByDictCode(code);
        if(dictionaryEntries!=null && dictionaryEntries.size()!=0){
            throw new Exception("该字典包含字典项，不可删除");
        }
    }

}