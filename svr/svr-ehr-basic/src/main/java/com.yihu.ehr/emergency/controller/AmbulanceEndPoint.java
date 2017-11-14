package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.emergency.service.AttendanceService;
import com.yihu.ehr.emergency.service.ScheduleService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * EndPoint - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AmbulanceEndPoint", description = "救护车信息", tags = {"应急指挥-救护车信息"})
public class AmbulanceEndPoint extends BaseRestEndPoint {

    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AttendanceService attendanceService;

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有救护车列表")
    public Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        Envelop envelop = new Envelop();
        try {
            List<Ambulance> ambulanceList = ambulanceService.search(fields, filters, sorts, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(ambulanceList);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSearch , method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车信息，包括执勤人员信息，检索条件只针对车辆")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        Envelop envelop = new Envelop();
        try {
            List<Object> resultList = new ArrayList<Object>();
            List<Ambulance> ambulanceList = ambulanceService.search(fields, filters, sorts, page, size);
            Date date = new Date();
            for (Ambulance ambulance : ambulanceList) {
                Map<String, Object> resultMap = new HashMap<String, Object>();
                List<Schedule> scheduleList = scheduleService.findMatch(ambulance.getId(), date);
                Map<String, Object> childMap = new HashMap<String, Object>();
                childMap.put("id", ambulance.getId());
                childMap.put("initLongitude", ambulance.getInitLongitude());
                childMap.put("initLatitude", ambulance.getInitLatitude());
                childMap.put("district", ambulance.getDistrict());
                childMap.put("orgCode", ambulance.getOrgCode());
                childMap.put("orgName", ambulance.getOrgName());
                childMap.put("driver", ambulance.getDriver());
                childMap.put("phone", ambulance.getPhone());
                childMap.put("status", ambulance.getStatus());
                childMap.put("entityName", ambulance.getEntityName());
                childMap.put("dutyList", scheduleList);
                resultMap.put("car", childMap);
                resultList.add(resultMap);
            }
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdate, method = RequestMethod.PUT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "更新救护车状态信息")
    public Envelop update(
            @ApiParam(name = "carId", value = "车牌号码")
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "status", value = "车辆状态码(0为统一可用状态码，1为统一不可用状态码)")
            @RequestParam(value = "status") String status) {
        Envelop envelop = new Envelop();
        Ambulance ambulance = ambulanceService.findById(carId);
        if(ambulance != null) {
            if (status.equals("0")) {
                ambulance.setStatus(Ambulance.Status.wait);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
            }else if(status.equals("1")) {
                ambulance.setStatus(Ambulance.Status.down);
                ambulanceService.save(ambulance);
                Attendance attendance = attendanceService.findByCarIdAndStatus(carId, Attendance.Status.start, Attendance.Status.arrival);
                //如果有正在出勤中的任务，则设置为意外中止
                if(attendance != null) {
                    attendance.setStatus(Attendance.Status.discontinue);
                    attendanceService.save(attendance);
                }
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("状态码错误");
            }
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关车辆信息");
        }
        return envelop;
    }

}
