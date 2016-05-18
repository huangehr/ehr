package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsDictionaryEntry;
import com.yihu.ehr.resource.model.RsDictionaryEntry;
import com.yihu.ehr.resource.model.RsDictionary;
import com.yihu.ehr.resource.service.RsDictionaryEntryService;
import com.yihu.ehr.util.controller.BaseRestController;
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
@RequestMapping(value = ApiVersion.Version1_0 + "/dictionaryEntries")
@Api(value = "dictionaryEntries", description = "标准字典项服务接口")
public class RsDictionaryEntryController extends BaseRestController {
    @Autowired
    private RsDictionaryEntryService rsDictionaryEntryService;

    @RequestMapping(value = "/searchRsDictionaryEntries", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典项列表", notes = "根据查询条件获取标准字典项列表")
    public List<MRsDictionaryEntry> searchRsDictionaryEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsDictionaryEntry> dictionaryEntries = rsDictionaryEntryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsDictionaryEntryService.getCount(filters), page, size);
        return (List<MRsDictionaryEntry>) convertToModels(dictionaryEntries, new ArrayList<MRsDictionaryEntry>(dictionaryEntries.size()), MRsDictionaryEntry.class, fields);
    }



    @RequestMapping(value = "/createRsDictionaryEntry", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准字典项", notes = "创建标准字典项")
    public MRsDictionaryEntry createRsDictionaryEntry(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsDictionaryEntry dictionaryEntry = toEntity(jsonData, RsDictionaryEntry.class);
        rsDictionaryEntryService.save(dictionaryEntry);
        return convertToModel(dictionaryEntry, MRsDictionaryEntry.class, null);

    }

    @RequestMapping(value = "/updateRsDictionaryEntry", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准字典项", notes = "修改标准字典项")
    public MRsDictionaryEntry updateRsDictionaryEntry(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsDictionaryEntry dictionaryEntry = toEntity(jsonData, RsDictionaryEntry.class);
        rsDictionaryEntryService.save(dictionaryEntry);
        return convertToModel(dictionaryEntry, MRsDictionaryEntry.class, null);
    }

    @RequestMapping(value = "/deleteRsDictionaryEntry{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典项", notes = "删除标准字典项")
    public boolean deleteRsDictionaryEntry(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsDictionaryEntryService.delete(id);
        return true;
    }
}