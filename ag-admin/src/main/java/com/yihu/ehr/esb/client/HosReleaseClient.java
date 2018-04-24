package com.yihu.ehr.esb.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.13 14:15
 */
@FeignClient(name=MicroServices.ESB)
@RequestMapping(value = ApiVersion.Version1_0 + "/esb")
@ApiIgnore
public interface HosReleaseClient {

    @RequestMapping(value = "/searchHosEsbMiniReleases", method = RequestMethod.GET)
    ResponseEntity<List<MHosEsbMiniRelease>> searchHosEsbMiniReleases(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    String deleteHosEsbMiniRelease(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/createHosEsbMiniRelease", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增软件包")
    MHosEsbMiniRelease createHosEsbMiniRelease(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);


    @RequestMapping(value = "/updateHosEsbMiniRelease", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改软件包")
    MHosEsbMiniRelease updateHosEsbMiniRelease(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);
}
