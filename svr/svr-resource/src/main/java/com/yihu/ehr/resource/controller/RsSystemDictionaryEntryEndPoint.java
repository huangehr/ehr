package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsSystemDictionaryEntry;
import com.yihu.ehr.resource.model.RsSystemDictionaryEntry;
import com.yihu.ehr.resource.service.RsSystemDictionaryEntryService;
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
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsSystemDictionaryEntryEndPoint", description = "资源系统字典项", tags = {"资源服务-资源系统字典项"})
public class RsSystemDictionaryEntryEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RsSystemDictionaryEntryService systemDictionaryEntryService;

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典项列表", notes = "根据查询条件获取系统字典项列表")
    public List<MRsSystemDictionaryEntry> searchRsSystemDictionaryEntries(
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
        List<RsSystemDictionaryEntry> systemDictionaryEntries = systemDictionaryEntryService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, systemDictionaryEntryService.getCount(filters), page, size);
        return (List<MRsSystemDictionaryEntry>) convertToModels(systemDictionaryEntries, new ArrayList<MRsSystemDictionaryEntry>(systemDictionaryEntries.size()), MRsSystemDictionaryEntry.class, fields);
    }


    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典项", notes = "创建系统字典项")
    public MRsSystemDictionaryEntry createRsSystemDictionaryEntry(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsSystemDictionaryEntry systemDictionaryEntry = toEntity(jsonData, RsSystemDictionaryEntry.class);
        String code = systemDictionaryEntry.getCode();
        String dictCode = systemDictionaryEntry.getDictCode();
        if(isExistence(dictCode,code)){
            throw new Exception("字典项代码不能重复");
        }
        systemDictionaryEntry.setId(getObjectId(BizObject.RsSystemDictionaryEntry));
        systemDictionaryEntryService.save(systemDictionaryEntry);
        return convertToModel(systemDictionaryEntry, MRsSystemDictionaryEntry.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典项", notes = "修改系统字典项")
    public MRsSystemDictionaryEntry updateRsSystemDictionaryEntry(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsSystemDictionaryEntry systemDictionaryEntry = toEntity(jsonData, RsSystemDictionaryEntry.class);
        String id = systemDictionaryEntry.getId();
        RsSystemDictionaryEntry d = systemDictionaryEntryService.findById(id);
        String code = systemDictionaryEntry.getCode();
        String dictCode = systemDictionaryEntry.getDictCode();
        if(code!=d.getCode() && isExistence(dictCode,code)){
            throw new Exception("字典代码不可修改");
        }
        systemDictionaryEntryService.save(systemDictionaryEntry);
        return convertToModel(systemDictionaryEntry, MRsSystemDictionaryEntry.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典项", notes = "删除系统字典项")
    public boolean deleteRsSystemDictionaryEntry(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        systemDictionaryEntryService.delete(id);
        return true;
    }

    public boolean isExistence(String dictCode,String code) {
        return systemDictionaryEntryService.findByFields(new String[]{"dictCode","code"},new String[]{dictCode,code}) != null;
    }
}