package com.yihu.ehr.emergency.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface AmbulanceClient{

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有救护车列表")
    Envelop list(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSearch, method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车信息,包括执勤人员信息")
    Envelop search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdate, method = RequestMethod.PUT)
    @ApiOperation(value = "更新救护车状态信息")
    Envelop updateStatus(
            @RequestParam(value = "carId") String carId,
            @RequestParam(value = "status") String status);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    Envelop save(
            @RequestBody String ambulance);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    Envelop update(
            @RequestBody String ambulance);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除记录")
    Envelop delete(
            @RequestParam(value = "ids") String ids);
}
