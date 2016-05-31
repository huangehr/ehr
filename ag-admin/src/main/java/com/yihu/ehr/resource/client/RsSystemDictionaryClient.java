package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsSystemDictionary;
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
public interface RsSystemDictionaryClient {

    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典列表", notes = "根据查询条件获取系统字典列表")
    ResponseEntity<List<MRsSystemDictionary>> searchRsSystemDictionaries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);



    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典", notes = "创建系统字典")
    MRsSystemDictionary createRsSystemDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.SystemDictList, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典", notes = "修改系统字典")
    MRsSystemDictionary updateRsSystemDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.SystemDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典", notes = "删除系统字典")
    boolean deleteRsSystemDictionary(
            @PathVariable(value = "id") String id);

}
