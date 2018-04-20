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
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除出勤记录")
    Envelop delete(
            @ApiParam(name = "ids", value = "id列表[1,2,3...] int")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDetail, method = RequestMethod.GET)
    @ApiOperation("出勤记录详情")
    Envelop detail(
            @ApiParam(name = "id", value = "出勤记录id")
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Emergency.AttendanceAnalysis, method = RequestMethod.GET)
    @ApiOperation("出勤记录分析")
    Envelop analysis();

    @RequestMapping(value = "/attendance/queryChart1", method = RequestMethod.GET)
    @ApiOperation("出车次数分析")
    Envelop queryChart1(
            @ApiParam(name = "flag", value = "查询类型月、季、年（1,2,3）")
            @RequestParam(value = "flag") String flag);

    @RequestMapping(value = "/attendance/queryChart3", method = RequestMethod.GET)
    @ApiOperation("出勤地点热力图")
    Envelop queryChart3();
}
