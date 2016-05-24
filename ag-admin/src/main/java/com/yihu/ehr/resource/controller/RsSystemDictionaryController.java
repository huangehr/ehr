package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsSystemDictionary;
import com.yihu.ehr.resource.client.RsSystemDictionaryClient;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "systemDictionaries", description = "系统字典服务接口")
public class RsSystemDictionaryController extends BaseController {

    @Autowired
    private RsSystemDictionaryClient rsSystemDictionaryClient;

    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典列表", notes = "根据查询条件获取系统字典列表")
    public List<MRsSystemDictionary> searchRsSystemDictionaries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        return rsSystemDictionaryClient.searchRsSystemDictionaries(fields,filters,sorts,size,page);
    }



    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典", notes = "创建系统字典")
    public MRsSystemDictionary createRsSystemDictionary(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        return rsSystemDictionaryClient.createRsSystemDictionary(jsonData);
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDicts, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典", notes = "修改系统字典")
    public MRsSystemDictionary updateRsSystemDictionary(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        return rsSystemDictionaryClient.updateRsSystemDictionary(jsonData);
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典", notes = "删除系统字典")
    public boolean deleteRsSystemDictionary(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return rsSystemDictionaryClient.deleteRsSystemDictionary(id);
    }

}