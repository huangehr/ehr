package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsAdapterDictionary;
import com.yihu.ehr.model.resource.MRsAdapterMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface AdapterDictionaryClient {

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建适配字典项")
    MRsAdapterDictionary createDictionaries(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新适配字典项")
    MRsAdapterDictionary updateDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionary, method = RequestMethod.DELETE)
    @ApiOperation("删除适配字典项")
    boolean deleteDictionary(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionary,method = RequestMethod.GET)
    @ApiOperation("根据ID获取适配字典项")
    public MRsAdapterDictionary getDictionaryById(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Adaptions.RsAdapterDictionaries, method = RequestMethod.GET)
    @ApiOperation("查询适配字典项")
    ResponseEntity<List<MRsAdapterDictionary>> getDictionaries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);
}
