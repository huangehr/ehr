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
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * EndPoint - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AttendanceEndPoint", description = "出勤记录", tags = {"应急指挥-出勤记录"})
public class AttendanceEndPoint extends BaseRestEndPoint {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private ScheduleService scheduleService;

    @RequestMapping(value = ServiceApi.Emergency.AttendanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "保存出勤记录")
    public Envelop save(
            @ApiParam(name = "attendance", value = "出勤记录")
            @RequestBody String attendance) throws Exception{
        Envelop envelop = new Envelop();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("carId", "赣ESD2322");
        dataMap.put("alarmTel", "120");
        dataMap.put("callAddress", "五三大道");
        dataMap.put("chiefComplaint", "救命");
        dataMap.put("dispatchHospital", "人民医院");
        dataMap.put("patientNum", 2);
        dataMap.put("patientGender", "一男一女");
        dataMap.put("disease", "车祸");
        dataMap.put("remark", "赶紧的");
        dataMap.put("creator", "桂花");
        String dataJson = objectMapper.writeValueAsString(dataMap);
        Attendance newAttendance = toEntity(dataJson, Attendance.class);
        Attendance verification = attendanceService.findByCarIdAndStatus(newAttendance.getCarId(), Attendance.Status.start, Attendance.Status.arrival);
        if(verification != null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该车辆有尚未完成的执勤任务");
            return envelop;
        }
        Ambulance ambulance =  ambulanceService.findById(newAttendance.getCarId());
        //如果救护车不为空，并且状态为待命中
        if(ambulance != null && ambulance.getStatus().equals(Ambulance.Status.wait)) {
            List<Schedule> scheduleList = scheduleService.findMatch(ambulance.getId(), new Date());
            if(scheduleList != null && scheduleList.size() >= 3) {
                //生成出勤记录
                List<Integer> idList = new ArrayList<Integer>();
                for(Schedule schedule : scheduleList) {
                    idList.add(schedule.getId());
                }
                newAttendance.setSchedules(objectMapper.writeValueAsString(idList));
                newAttendance.setStatus(Attendance.Status.start);
                newAttendance.setStartTime(new Date());
                attendanceService.save(newAttendance);
                //更新车辆状态为执勤中
                ambulance.setStatus(Ambulance.Status.active);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("该车辆当前时间点排班人员不足");
            }
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("当前车辆不处于待命状态");
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceUpdate, method = RequestMethod.PUT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "更新出勤记录")
    public Envelop update(
            @ApiParam(name = "carId", value = "车牌号码")
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "status", value = "任务状态")
            @RequestParam(value = "status") String status) {
        Envelop envelop = new Envelop();
        Attendance attendance = attendanceService.findByCarIdAndStatus(carId, Attendance.Status.start, Attendance.Status.arrival);
        if(attendance != null) {
            if(status.equals("1") && attendance.getStatus().equals(Attendance.Status.start)) { //到达事故地点
                attendance.setStatus(Attendance.Status.arrival);
                attendance.setArrivalTime(new Date());
                attendanceService.save(attendance);
                envelop.setSuccessFlg(true);
                envelop.setObj(status);
            }else if(status.equals("2")) { //返回归属地，忽略到达时间为空的情况
                attendance.setStatus(Attendance.Status.complete);
                attendance.setCompleteTime(new Date());
                attendanceService.save(attendance);
                //完成任务，重新设置车辆为待命中
                Ambulance ambulance =  ambulanceService.findById(carId);
                ambulance.setStatus(Ambulance.Status.wait);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
                envelop.setObj(status);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("请求更新状态:" + status + ",与当前状态:"+ attendance.getStatus().ordinal() + ",不匹配");
                envelop.setObj(status);
            }
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该车辆无可更新的出勤记录");
            envelop.setObj(status);
        }
        return envelop;
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
            @ApiParam(name = "page", value = "分页大小", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "页码", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        try {
            List<Ambulance> ambulances = attendanceService.search(fields, filters, sorts, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(ambulances);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除出勤记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表[1,2,3...] int")
            @RequestParam(value = "ids") String ids){
        List<Integer> idList = toEntity(ids, List.class);
        attendanceService.delete(idList);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
