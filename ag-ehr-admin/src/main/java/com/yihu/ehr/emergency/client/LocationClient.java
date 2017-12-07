package com.yihu.ehr.emergency.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface LocationClient {

    @RequestMapping(value = ServiceApi.Emergency.LocationList, method = RequestMethod.GET)
    @ApiOperation("获取待命地点列表")
    Envelop list(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.LocationSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    Envelop save(
            @ApiParam(name = "location", value = "待命地点")
            @RequestBody String location);

    @RequestMapping(value = ServiceApi.Emergency.LocationUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    Envelop update(
            @ApiParam(name = "location", value = "排班")
            @RequestBody String location);

    @RequestMapping(value = ServiceApi.Emergency.LocationDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除待命地点")
    Envelop delete(
            @ApiParam(name = "ids", value = "id列表[1,2,3...] int")
            @RequestParam(value = "ids") String ids);

}
