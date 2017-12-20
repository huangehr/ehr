package com.yihu.ehr.emergency.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


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
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSearch, method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车信息,包括执勤人员信息")
    Envelop search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdateStatus, method = RequestMethod.PUT)
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

    @RequestMapping(value = ServiceApi.Emergency.Ambulance, method = RequestMethod.GET)
    @ApiOperation("获取单条记录")
    Envelop findById(
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceIdOrPhoneExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在车牌号、电话号码")
    List idExistence(
            @ApiParam(name = "type", value = "字段名")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "values", value = "值")
            @RequestParam(value = "values") String values);

    @RequestMapping(value = ServiceApi.Emergency.AmbulancesBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入救护车")
    boolean createAmbulancesBatch(
            @RequestBody String ambulances);
}
