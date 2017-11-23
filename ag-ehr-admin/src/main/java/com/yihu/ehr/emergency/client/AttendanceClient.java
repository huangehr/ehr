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

import java.util.List;


/**
 * Client - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface AttendanceClient {

    @RequestMapping(value = ServiceApi.Emergency.AttendanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存出勤记录")
    Envelop save(
            @RequestBody String attendance);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceUpdate, method = RequestMethod.PUT)
    @ApiOperation(value = "更新出勤记录")
    Envelop update(
            @RequestParam(value = "carId") String carId,
            @RequestParam(value = "status") String status);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceEdit, method = RequestMethod.PUT)
    @ApiOperation("编辑出勤记录")
    Envelop edit(
            @RequestBody String attendance);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceList, method = RequestMethod.GET)
    @ApiOperation("获取出勤列表")
    Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "页码", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除出勤记录")
    Envelop delete(
            @ApiParam(name = "ids", value = "id列表[1,2,3...] int")
            @RequestParam(value = "ids") String ids);
}
