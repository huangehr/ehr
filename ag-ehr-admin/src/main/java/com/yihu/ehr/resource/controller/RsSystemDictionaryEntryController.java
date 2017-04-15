package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MRsSystemDictionaryEntry;
import com.yihu.ehr.resource.client.RsSystemDictionaryEntryClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "systemDictionaryEntries", description = "系统字典项服务接口", tags = {"资源管理-系统字典项服务接口"})
public class RsSystemDictionaryEntryController extends BaseController {

    @Autowired
    private RsSystemDictionaryEntryClient rsSystemDictionaryEntryClient;

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典项列表", notes = "根据查询条件获取系统字典项列表")
    public Envelop searchRsSystemDictionaryEntries(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        Envelop envelop = new Envelop();
        try {
            ResponseEntity<List<MRsSystemDictionaryEntry>> responseEntity = rsSystemDictionaryEntryClient.searchRsSystemDictionaryEntries(fields,filters,sorts,page,size);
            List<MRsSystemDictionaryEntry> rsSystemDictionaryEntries = responseEntity.getBody();
            envelop = getResult(rsSystemDictionaryEntries, getTotalCount(responseEntity), page, size);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典项", notes = "创建系统字典项")
    public Envelop createRsSystemDictionaryEntry(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsSystemDictionaryEntry rsSystemDictionaryEntry = rsSystemDictionaryEntryClient.createRsSystemDictionaryEntry(jsonData);
            envelop.setObj(rsSystemDictionaryEntry);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典项", notes = "修改系统字典项")
    public Envelop updateRsSystemDictionaryEntry(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsSystemDictionaryEntry rsSystemDictionaryEntry = rsSystemDictionaryEntryClient.updateRsSystemDictionaryEntry(jsonData);
            envelop.setObj(rsSystemDictionaryEntry);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典项", notes = "删除系统字典项")
    public Envelop deleteRsSystemDictionaryEntry(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            rsSystemDictionaryEntryClient.deleteRsSystemDictionaryEntry(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

}