package com.yihu.ehr.emergency.controller;

import com.netflix.eureka.V1AwareInstanceInfoConverter;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.emergency.client.AttendanceClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "AttendanceController", description = "出勤记录", tags = {"应急指挥-出勤记录"})
public class AttendanceController extends BaseController {

    @Autowired
    private AttendanceClient attendanceClient;

    @RequestMapping(value = ServiceApi.Emergency.AttendanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "保存出勤记录")
    public Envelop save(
            @ApiParam(name = "attendance", value = "出勤记录")
            @RequestParam(value = "attendance") String attendance) {
        return attendanceClient.save(attendance);
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceUpdate, method = RequestMethod.PUT)
    @ApiOperation(value = "更新出勤记录")
    public Envelop update(
            @ApiParam(name = "carId", value = "车牌号码")
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "status", value = "任务状态")
            @RequestParam(value = "status") String status) {
        return attendanceClient.update(carId, status);
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceEdit, method = RequestMethod.PUT)
    @ApiOperation("编辑出勤记录")
    public Envelop edit(
            @ApiParam(name = "attendance", value = "出勤记录")
            @RequestParam(value = "attendance") String attendance) {
        return attendanceClient.edit(attendance);
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceList, method = RequestMethod.GET)
    @ApiOperation("获取出勤列表")
    public Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        return attendanceClient.list(fields, filters, sorts, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除出勤记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表(int)1,2,3,...")
            @RequestParam(value = "ids") String ids){
        return attendanceClient.delete(ids);
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDetail, method = RequestMethod.GET)
    @ApiOperation("出勤记录详情")
    public Envelop detail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id){
        return attendanceClient.detail(id);
    }
}
