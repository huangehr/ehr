package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0 )
@ApiIgnore
public interface RsAdapterDictionaryClient {

    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取适配字典列表", notes = "根据查询条件获取适配字典列表")
    ResponseEntity<List<MRsAdapterDictionary>> searchRsAdapterDictionaries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建适配字典", notes = "创建适配字典")
    MRsAdapterDictionary createRsAdapterDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.AdapterDicts, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改适配字典", notes = "修改适配字典")
    MRsAdapterDictionary updateRsAdapterDictionary(
            @RequestBody String jsonData);


    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配字典", notes = "删除适配字典")
    boolean deleteRsAdapterDictionary(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.AdapterDict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取适配字典")
    MRsAdapterDictionary getRsAdapterDictionaryById(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.AdapterDictsBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建适配字典", notes = "批量创建适配字典")
    boolean createRsAdapterDictionaries(
            @RequestBody String jsonData);
}