package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.emergency.service.ScheduleService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * EndPoint - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ScheduleEndPoint", description = "排班历史", tags = {"应急指挥-排班历史"})
public class ScheduleEndPoint extends BaseRestEndPoint {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AmbulanceService ambulanceService;


    @RequestMapping(value = ServiceApi.Emergency.ScheduleList, method = RequestMethod.GET)
    @ApiOperation("获取排班列表")
    public Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "页码", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        try {
            List<Schedule> schedules = scheduleService.search(fields, filters, sorts, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(schedules);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "schedule", value = "排班")
            @RequestBody String schedule){
        Envelop envelop = new Envelop();
        try {
            Schedule newSchedule = objectMapper.readValue(schedule, Schedule.class);
            Ambulance ambulance = ambulanceService.findById(newSchedule.getCarId());
            if (ambulance == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关车辆信息");
                return envelop;
            }
            newSchedule.setCrateDate(new Date());
            scheduleService.save(newSchedule);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录，只允许更新时间或状态")
    public Envelop update(
            @ApiParam(name = "schedule", value = "排班")
            @RequestBody String schedule){
        Envelop envelop = new Envelop();
        try {
            Schedule newSchedule = objectMapper.readValue(schedule, Schedule.class);
            Schedule oldSchedule  = scheduleService.findById(newSchedule.getId());
            if(oldSchedule == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关排班信息");
                return envelop;
            }
            Ambulance ambulance = ambulanceService.findById(newSchedule.getCarId());
            if (ambulance == null || ambulance.getStatus() == Ambulance.Status.active) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关车辆信息，或者相关车辆处于执勤状态");
                return envelop;
            }
            newSchedule.setCrateDate(oldSchedule.getCrateDate());
            newSchedule.setUpdateDate(new Date());
            scheduleService.save(newSchedule);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
