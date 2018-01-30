package com.yihu.ehr.basic.emergency.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.basic.emergency.service.AmbulanceService;
import com.yihu.ehr.basic.emergency.service.ScheduleService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


/**
 * EndPoint - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ScheduleEndPoint", description = "排班历史", tags = {"应急指挥-排班历史"})
public class ScheduleEndPoint extends EnvelopRestEndPoint {

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
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        try {
            List<Schedule> schedules = scheduleService.search(fields, filters, sorts, page, size);
            int count = (int)scheduleService.getCount(filters);
            envelop = getPageResult(schedules, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(schedules);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleLevel, method = RequestMethod.GET)
    @ApiOperation("获取排班层级列表（年-月-日）")
    public Envelop level(
            @ApiParam(name = "date", value = "年-月")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws ParseException {
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(date)) {
            try {
                List<Object> resultList = scheduleService.getLevel(page, size);
                Integer count = scheduleService.getLevelCount();
                envelop = getPageResult(resultList, count, page, size);
                return envelop;
            } catch (ParseException e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }else {
            List<Object> resultList = new ArrayList<Object>();
            List<java.sql.Date> dateGroup = scheduleService.getDateGroup(date, page, size);
            for(int i = 0; i < dateGroup.size(); i ++) {
                for(int j = 1; j >= 0 ; j --) {
                    Map<String, Object> middleMap1 = new HashMap<String, Object>();
                    java.sql.Date date1 = dateGroup.get(i);
                    //时间节点
                    middleMap1.put("date", date1.toString());
                    //数据节点
                    List<Schedule> scheduleList = scheduleService.getDateMatch(date1, j);
                    Map<String, Map<String, Object>> carMap = new HashMap<String, Map<String, Object>>();
                    String carId;
                    for (Schedule schedule : scheduleList) {
                        carId = schedule.getCarId();
                        Ambulance ambulance = ambulanceService.findById(carId);
                        if (null != ambulance) {
                            String dutyRole = schedule.getDutyRole();
                            String dutyName = schedule.getDutyName();
                            String scheduleId = schedule.getId().toString();
                            if (carMap.containsKey(carId)) {
                                Map<String, Object> dataMap = carMap.get(carId);
                                String time = schedule.getStart().toString();
                                if (dataMap.containsKey(time)) {
                                    for (String timeKey : dataMap.keySet()) {
                                        if (time.equals(timeKey)) {
                                            Map<String, String> detailMap = (Map<String, String>) dataMap.get(time);
                                            String scheduleIds = detailMap.get("scheduleIds");
                                            detailMap.put("scheduleIds", scheduleIds + "," + scheduleId);
                                            if ("医生".equals(dutyRole)) {
                                                String doctors;
                                                if (StringUtils.isEmpty(detailMap.get("doctor"))) {
                                                    doctors = dutyName;
                                                } else {
                                                    doctors = detailMap.get("doctor") + "," + dutyName;
                                                }
                                                detailMap.put("doctor", doctors);
                                            } else if ("护士".equals(dutyRole)) {
                                                String nurses;
                                                if (StringUtils.isEmpty(detailMap.get("nurse"))) {
                                                    nurses = dutyName;
                                                } else {
                                                    nurses = detailMap.get("nurse") + "," + dutyName;
                                                }
                                                detailMap.put("nurse", nurses);
                                            } else if ("司机".equals(dutyRole)) {
                                                String drivers;
                                                if (StringUtils.isEmpty(detailMap.get("driver"))) {
                                                    drivers = dutyName;
                                                } else {
                                                    drivers = detailMap.get("driver") + "," + dutyName;
                                                }
                                                detailMap.put("driver", drivers);
                                            }
                                        }
                                    }
                                } else {
                                    //Map<String, Object> newDataMap = new HashMap<String, Object>();
                                    Map<String, String> detailMap = new HashMap<String, String>();
                                    detailMap.put("start", schedule.getStart().toString());
                                    detailMap.put("end", schedule.getEnd().toString());
                                    detailMap.put("carId", carId);
                                    detailMap.put("main", schedule.getMain().toString());
                                    detailMap.put("location", ambulance.getOrgName());
                                    detailMap.put("scheduleIds", scheduleId);
                                    if ("医生".equals(dutyRole)) {
                                        detailMap.put("doctor", dutyName);
                                    } else if ("护士".equals(dutyRole)) {
                                        detailMap.put("nurse", dutyName);
                                    } else if ("司机".equals(dutyRole)) {
                                        detailMap.put("driver", dutyName);
                                    }
                                    //dataMap.put("time", schedule.getStart().toString());
                                    dataMap.put(schedule.getStart().toString(), detailMap);
                                    //carMap.put(carId, newDataMap);
                                }
                            } else {
                                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                                Map<String, String> detailMap = new HashMap<String, String>();
                                detailMap.put("start", schedule.getStart().toString());
                                detailMap.put("end", schedule.getEnd().toString());
                                detailMap.put("carId", carId);
                                detailMap.put("main", schedule.getMain().toString());
                                detailMap.put("location", ambulance.getOrgName());
                                detailMap.put("scheduleIds", scheduleId);
                                if ("医生".equals(dutyRole)) {
                                    detailMap.put("doctor", dutyName);
                                } else if ("护士".equals(dutyRole)) {
                                    detailMap.put("nurse", dutyName);
                                } else if ("司机".equals(dutyRole)) {
                                    detailMap.put("driver", dutyName);
                                }
                                //dataMap.put("time", schedule.getStart().toString());
                                dataMap.put(schedule.getStart().toString(), detailMap);
                                carMap.put(carId, dataMap);
                            }
                        } else {
                            Map<String, Object> errorMap = new HashMap<String, Object>(1);
                            errorMap.put("error", "无相关车辆");
                            carMap.put(carId, errorMap);
                        }
                    }
                    List<Object> middleList = new ArrayList<Object>(carMap.size());
                    for (String car : carMap.keySet()) {
                        for (String timeKey : carMap.get(car).keySet()) {
                            middleList.add(carMap.get(car).get(timeKey));
                        }
                    }
                    middleMap1.put("data", middleList);
                    resultList.add(middleMap1);
                }
            }
            int count = scheduleService.getDateGroupCount(date);
            envelop = getPageResult(resultList, count, page, size);
            return envelop;
        }
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
    @ApiOperation("更新单条记录，历史记录不能更新")
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
            if(new Date().getTime() > oldSchedule.getDate().getTime()) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("历史记录不能更新");
                return envelop;
            }
            Ambulance ambulance = ambulanceService.findById(newSchedule.getCarId());
            if(ambulance == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关车辆信息");
            }else if(ambulance.getStatus() == Ambulance.Status.wait || ambulance.getStatus() == Ambulance.Status.down) {
                scheduleService.save(newSchedule);
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("相关车辆处于执勤状态");
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleBathUpdate, method = RequestMethod.PUT)
    @ApiOperation("更新多条数据，历史记录不能更新")
    public Envelop bathUpdate(
            @ApiParam(name = "schedules", value = "排班")
            @RequestParam(value = "schedules") String schedules) throws IOException{
        Envelop envelop = new Envelop();
        List<Map<String, Object>> scheduleList = objectMapper.readValue(schedules, List.class);
        for(Map<String, Object> newSchedule : scheduleList) {
            Schedule old = scheduleService.findById((int)newSchedule.get("id"));
            Ambulance ambulance = ambulanceService.findById(old.getCarId());
            if(ambulance.getStatus() != Ambulance.Status.wait && ambulance.getStatus() != Ambulance.Status.down) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("相关车辆正在执勤，不允许更新");
                return envelop;
            }
            if((new Date().getTime() - old.getDate().getTime()) > 0 ) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("历史记录不能更新");
                return envelop;
            }
            old.setDutyName(newSchedule.get("dutyName") == null? "": newSchedule.get("dutyName").toString());
            old.setGender(newSchedule.get("gender") == null? "": newSchedule.get("gender").toString());
            old.setDutyNum(newSchedule.get("dutyNum") == null? "": newSchedule.get("dutyNum").toString());
            old.setDutyPhone(newSchedule.get("dutyPhone") == null? "": newSchedule.get("dutyPhone").toString());
            scheduleService.save(old);
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入排班信息")
    public boolean createSchedulesBatch(
            @ApiParam(name = "schedules", value = "排班信息", defaultValue = "")
            @RequestParam(value = "schedules") String schedules) throws Exception{
        List models = objectMapper.readValue(schedules, new TypeReference<List>() {});
        scheduleService.addSchedulesBatch(models);
        return true;
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleCleanData, method = RequestMethod.POST)
    @ApiOperation("删除历史数据之外的某个时间段内的数据，基本单位为天")
    public int cleanData(
            @ApiParam(name = "carId", value = "车牌号码", required = true)
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "start", value = "开始时间", required = true)
            @RequestParam(value = "start") String start,
            @ApiParam(name = "end", value = "结束时间", required = true)
            @RequestParam(value = "end") String end) throws Exception{
        Date start1 = DateUtil.strToDate(start);
        Date end1 = DateUtil.strToDate(end);
        return scheduleService.cleanAllData(carId, start1, end1);
    }
}
