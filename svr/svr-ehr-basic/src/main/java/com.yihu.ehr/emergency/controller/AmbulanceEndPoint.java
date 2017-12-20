package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.emergency.service.AttendanceService;
import com.yihu.ehr.emergency.service.LocationService;
import com.yihu.ehr.emergency.service.ScheduleService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Attendance;
import com.yihu.ehr.entity.emergency.Location;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.org.service.OrgService;
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
 * EndPoint - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AmbulanceEndPoint", description = "救护车信息", tags = {"应急指挥-救护车信息"})
public class AmbulanceEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private LocationService locationService;

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有救护车列表")
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
            List<Ambulance> ambulanceList = ambulanceService.search(fields, filters, sorts, page, size);
            int count = (int)ambulanceService.getCount(filters);
            envelop = getPageResult(ambulanceList, count, page, size);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSearch , method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车信息，包括执勤人员信息，以及最近一条执勤记录，检索条件只针对车辆")
    public Envelop search(
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
            List<Object> resultList = new ArrayList<Object>();
            List<Ambulance> ambulanceList = ambulanceService.search(fields, filters, sorts, page, size);
            Date date = new Date();
            for (Ambulance ambulance : ambulanceList) {
                Map<String, Object> resultMap = new HashMap<String, Object>();
                List<Schedule> scheduleList = scheduleService.findMatch(ambulance.getId(), date);
                Attendance attendance = attendanceService.findByCreateDateAndCarId(date, ambulance.getId());
                Location location = locationService.findById(ambulance.getLocation());
                Map<String, Object> childMap = new HashMap<String, Object>();
                childMap.put("id", ambulance.getId());
                childMap.put("location", location.getId());
                childMap.put("initLongitude", location.getInitLongitude());
                childMap.put("initLatitude", location.getInitLatitude());
                childMap.put("initAddress", location.getInitAddress());
                childMap.put("district", location.getDistrict());
                childMap.put("orgName", ambulance.getOrgName());
                childMap.put("phone", ambulance.getPhone());
                childMap.put("status", ambulance.getStatus());
                childMap.put("entityName", ambulance.getEntityName());
                childMap.put("dutyList", scheduleList);
                childMap.put("attendance", attendance);
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

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdateStatus, method = RequestMethod.PUT)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "更新救护车状态信息")
    public Envelop updateStatus(
            @ApiParam(name = "carId", value = "车牌号码")
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "status", value = "车辆状态码(0为统一可用状态码，1为统一不可用状态码)")
            @RequestParam(value = "status") String status) {
        Envelop envelop = new Envelop();
        Ambulance ambulance = ambulanceService.findById(carId);
        if(ambulance != null) {
            if (status.equals("0")) {
                if(ambulance.getStatus() == Ambulance.Status.down) {
                    ambulance.setStatus(Ambulance.Status.wait);
                    ambulanceService.save(ambulance);
                    envelop.setSuccessFlg(true);
                }else {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("该车辆不处于异常状态");
                }
            }else if(status.equals("1")) {
                ambulance.setStatus(Ambulance.Status.down);
                ambulanceService.save(ambulance);
                //如果有正在出勤中的任务，则设置为意外中止
                List<Attendance.Status> statuses = new ArrayList<Attendance.Status>();
                statuses.add(Attendance.Status.start);
                statuses.add(Attendance.Status.arrival);
                statuses.add(Attendance.Status.back);
                Attendance attendance = attendanceService.findByCarIdAndStatus(carId, statuses);
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

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "ambulance", value = "救护车")
            @RequestBody String ambulance){
        Envelop envelop = new Envelop();
        try {
            Ambulance newAmbulance = objectMapper.readValue(ambulance, Ambulance.class);
            Ambulance oldAmbulance = ambulanceService.findById(newAmbulance.getId());
            if(oldAmbulance != null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("车辆：" + newAmbulance.getId() + "已存在");
                return envelop;
            }
            oldAmbulance = ambulanceService.findByPhone(newAmbulance.getPhone());
            if(oldAmbulance != null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("手机号：" + newAmbulance.getPhone() + "已存在");
                return envelop;
            }
            Location location = locationService.findById(newAmbulance.getLocation());
            if(null == location) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("待命地点：" + newAmbulance.getLocation() + "不存在");
                return envelop;
            }
            newAmbulance.setOrgName(location.getInitAddress());
            /**
            Organization organization = orgService.getOrg(newAmbulance.getOrgCode());
            if (organization == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关机构");
                return envelop;
            }
             */
            if (newAmbulance.getStatus() == Ambulance.Status.wait || newAmbulance.getStatus() == Ambulance.Status.down) {
                ambulanceService.save(newAmbulance);
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("车辆状态不能为执勤中");
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    public Envelop update(
            @ApiParam(name = "ambulance", value = "救护车")
            @RequestBody String ambulance){
        Envelop envelop = new Envelop();
        try {
            Ambulance newAmbulance = objectMapper.readValue(ambulance, Ambulance.class);
            Ambulance oldAmbulance = ambulanceService.findById(newAmbulance.getId());
            if(oldAmbulance == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关车辆信息");
            }else {
                Ambulance oldAmbulance1 = ambulanceService.findByPhone(newAmbulance.getPhone());
                if(oldAmbulance1 != null && !oldAmbulance1.getId().equals(newAmbulance.getId())) {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("手机号码重复");
                    return envelop;
                }
                Location location = locationService.findById(newAmbulance.getLocation());
                if(null == location) {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("待命地点：" + newAmbulance.getLocation() + "不存在");
                    return envelop;
                }
                newAmbulance.setCreator(oldAmbulance.getCreator());
                newAmbulance.setStatus(oldAmbulance.getStatus());
                newAmbulance.setOrgName(location.getInitAddress());
                /**
                Organization organization = orgService.getOrg(newAmbulance.getOrgCode());
                if (organization == null) {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("无相关机构");
                    return envelop;
                }
                */
                if (oldAmbulance.getStatus() == Ambulance.Status.wait || oldAmbulance.getStatus() == Ambulance.Status.down) {
                    ambulanceService.save(newAmbulance);
                    envelop.setSuccessFlg(true);
                }else {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("当前车辆处于执勤状态，无法更新");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表xxxx,xxxx,xxxx,...", required = true)
            @RequestParam(value = "ids") String ids){
        Envelop envelop = new Envelop();
        try {
            //List<String> idList = toEntity(ids, List.class);
            String [] idArr = ids.split(",");
            for (String id : idArr) {
                Ambulance ambulance = ambulanceService.findById(id);
                if (ambulance.getStatus() != Ambulance.Status.wait && ambulance.getStatus() != Ambulance.Status.down) {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("车辆：" + id + "，处于执勤状态，无法删除");
                    return envelop;
                }
                List<Attendance> attendanceList = attendanceService.search("carId=" + id);
                if(attendanceList != null && attendanceList.size() > 0) {
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("车辆：" + id + "，有执勤记录，无法删除");
                    return envelop;
                }
            }
            ambulanceService.delete(idArr);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.Ambulance, method = RequestMethod.GET)
    @ApiOperation("获取单条记录")
    public Envelop findById(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id){
        Envelop envelop = new Envelop();
        Ambulance ambulance = ambulanceService.findById(id);
        if(null != ambulance) {
            envelop.setSuccessFlg(true);
            envelop.setObj(ambulance);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceIdOrPhoneExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在车牌号、电话号码")
    public List idExistence(
            @ApiParam(name = "type", value = "字段名")
            @RequestParam(value ="type") String type,
            @ApiParam(name = "values", value = "车牌号、电话号码")
            @RequestParam(value ="values") String values) throws Exception {
        List existPhones = ambulanceService.idExist(type, toEntity(values, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulancesBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入救护车")
    public boolean createAmbulancesBatch(
            @ApiParam(name = "ambulances", value = "救护车")
            @RequestBody String ambulances) throws Exception{
        List models = objectMapper.readValue(ambulances, List.class);
        ambulanceService.addAmbulancesBatch(models);
        return true;
    }

}
