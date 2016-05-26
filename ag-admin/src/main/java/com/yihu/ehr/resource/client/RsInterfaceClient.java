package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsInterface;
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
public interface RsInterfaceClient {

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源列表", notes = "根据查询条件获取资源列表")
    ResponseEntity<List<MRsInterface>> searchRsInterfaces(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);


    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源", notes = "创建资源")
    MRsInterface createRsInterface(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改资源", notes = "修改资源")
    MRsInterface updateRsInterface(
            @RequestBody String jsonData);
    @RequestMapping(value = ServiceApi.Resources.Interface, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "删除资源")
    boolean deleteRsInterface(
            @PathVariable(value = "id") String id);
}
