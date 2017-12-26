package com.yihu.ehr.emergency.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EndPoint - 出勤记录
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AttendanceEndPoint", description = "出勤记录", tags = {"应急指挥-出勤记录"})
public class AttendanceEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AmbulanceService ambulanceService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private UserManager userManager;

    @RequestMapping(value = ServiceApi.Emergency.AttendanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "保存出勤记录")
    public Envelop save(
            @ApiParam(name = "attendance", value = "出勤记录")
            @RequestBody String attendance) throws JsonProcessingException {
        Envelop envelop = new Envelop();
        Attendance newAttendance = toEntity(attendance, Attendance.class);
        //验证车辆
        Ambulance ambulance =  ambulanceService.findById(newAttendance.getCarId());
        if(ambulance == null || ambulance.getStatus() != Ambulance.Status.wait){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关车辆或该车辆不处于待命状态");
            return envelop;
        }
        //验证出勤任务
        List<Attendance.Status> statuses = new ArrayList<Attendance.Status>();
        statuses.add(Attendance.Status.start);
        statuses.add(Attendance.Status.arrival);
        statuses.add(Attendance.Status.back);
        Attendance verification = attendanceService.findByCarIdAndStatus(newAttendance.getCarId(), statuses);
        if(verification != null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该车辆有尚未完成的执勤任务");
            return envelop;
        }
        //验证排班情况
        List<Schedule> scheduleList = scheduleService.findMatch(ambulance.getId(), new Date());
        if(scheduleList != null && scheduleList.size() >= 3) {
            //生成出勤记录
            List<Integer> idList = new ArrayList<Integer>();
            for(Schedule schedule : scheduleList) {
                idList.add(schedule.getId());
            }
            newAttendance.setSchedules(objectMapper.writeValueAsString(idList));
            //开始任务
            newAttendance.setStatus(Attendance.Status.start);
            attendanceService.save(newAttendance);
            //更新车辆状态为前往中
            ambulance.setStatus(Ambulance.Status.onWay);
            ambulanceService.save(ambulance);
            envelop.setSuccessFlg(true);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("该车辆当前时间点排班人员不足");
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
        Ambulance ambulance = ambulanceService.findById(carId);
        if(ambulance == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关车辆信息");
            envelop.setObj(-1);
            return envelop;
        }
        List<Attendance.Status> statuses = new ArrayList<Attendance.Status>();
        statuses.add(Attendance.Status.start);
        statuses.add(Attendance.Status.arrival);
        statuses.add(Attendance.Status.back);
        Attendance attendance = attendanceService.findByCarIdAndStatus(carId, statuses);
        if(attendance != null) {
            if(status.equals("1") && attendance.getStatus() == Attendance.Status.start) { //到达事故地点
                attendance.setStatus(Attendance.Status.arrival);
                attendance.setArrivalTime(new Date());
                attendanceService.save(attendance);
                ambulance.setStatus(Ambulance.Status.arrival);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
                envelop.setObj(status);
            }else if(status.equals("2")) { //返程中
                attendance.setStatus(Attendance.Status.back);
                attendanceService.save(attendance);
                ambulance.setStatus(Ambulance.Status.back);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
                envelop.setObj(status);
            }else if(status.equals("3")) { //完成任务
                attendance.setStatus(Attendance.Status.complete);
                attendance.setCompleteTime(new Date());
                attendanceService.save(attendance);
                //完成任务，重新设置车辆为待命中
                ambulance.setStatus(Ambulance.Status.wait);
                ambulanceService.save(ambulance);
                envelop.setSuccessFlg(true);
                envelop.setObj(status);
            }else if(status.equals("4")) { //意外中止
                attendance.setStatus(Attendance.Status.discontinue);
                attendance.setCompleteTime(new Date());
                attendanceService.save(attendance);
                //设置车辆为异常状态
                ambulance.setStatus(Ambulance.Status.down);
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
            envelop.setObj(-1);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceEdit, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("编辑出勤记录")
    public Envelop edit(
            @ApiParam(name = "attendance", value = "出勤记录")
            @RequestBody String attendance) {
        Envelop envelop = new Envelop();
        try {
            Attendance newAttendance = objectMapper.readValue(attendance, Attendance.class);
            Attendance oldAttendance = attendanceService.findById(newAttendance.getId());
            if(!oldAttendance.getCarId().equals(newAttendance.getCarId())) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("车牌号码有误");
                return envelop;
            }
            attendanceService.save(newAttendance);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
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
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        try {
            List<Attendance> attendance = attendanceService.search(fields, filters, sorts, page, size);
            int count = (int)attendanceService.getCount(filters);
            envelop = getPageResult(attendance, count, page, size);
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
            @ApiParam(name = "ids", value = "id列表(int)1,2,3,...")
            @RequestParam(value = "ids") String ids){
        //List<Integer> idList = toEntity(ids, List.class);
        String [] idArr = ids.split(",");
        Integer [] idArr1 = new Integer[idArr.length];
        for(int i = 0; i < idArr.length; i++) {
            idArr1[i] = new Integer(idArr[i]);
        }
        attendanceService.delete(idArr1);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceDetail, method = RequestMethod.GET)
    @ApiOperation("出勤详情")
    public Envelop detail(
            @ApiParam(name = "id", value = "出勤任务Id")
            @RequestParam(value = "id", required = false) int id) {
        Envelop envelop = new Envelop();
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            Attendance attendance = attendanceService.findById(id);
            if(attendance!=null){
                if(attendance.getSchedules().length()>2){
                    map.put("schedule",scheduleService.findByIds(attendance.getSchedules().substring(1,attendance.getSchedules().length()-1)));
                }
                map.put("attendance",attendance);
                map.put("user",userManager.getUser(attendance.getCreator()));
                map.put("car",ambulanceService.findById(attendance.getCarId()));
                envelop.setSuccessFlg(true);
                envelop.setObj(map);
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("出勤任务Id不存在");
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = "/attendance/queryChart1", method = RequestMethod.GET)
    @ApiOperation("出车次数分析")
    public Envelop queryChart1(
            @ApiParam(name = "flag", value = "查询类型月、季、年（1,2,3）")
             @RequestParam(value = "flag", required = false) String flag) {
        Envelop envelop = new Envelop();
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("chart1",attendanceService.queryChart1(flag));
            envelop.setSuccessFlg(true);
            envelop.setObj(map);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = "/attendance/queryChart3", method = RequestMethod.GET)
    @ApiOperation("出勤地点热力图")
    public Envelop queryChart3() {
        Envelop envelop = new Envelop();
        try {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("chart3",attendanceService.queryChart3());
            envelop.setSuccessFlg(true);
            envelop.setObj(map);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.AttendanceAnalysis, method = RequestMethod.GET)
    @ApiOperation("出勤记录分析")
    public Envelop analysis() {
        Envelop envelop = new Envelop();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String,Object> map = new HashMap<String,Object>();
            List<Location> locations = locationService.search("");
            List<Map<String,Object>> list =  attendanceService.queryChart5();
            List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> list5 = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> list6 = new ArrayList<Map<String,Object>>();
            //时间占比分析
            if(!list.isEmpty()){
                for(Location model :locations) {
                    List<Map<String, Object>> l1 = new ArrayList<Map<String, Object>>();
                    Map<String, Object> m1 = new HashMap<String, Object>();
                    int i1 = 0;
                    int i2 = 0;
                    int i3 = 0;
                    int i4 = 0;
                    for (Map<String, Object> map1 : list) {
                        if (map1.get("init_address").equals(model.getInitAddress())) {
                            Date createDate = sdf.parse(map1.get("create_date").toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(createDate);
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            if (hour >= 0 && hour < 6) {
                                i1++;
                            } else if (hour >= 6 && hour < 12) {
                                i2++;
                            } else if (hour >= 12 && hour < 18) {
                                i3++;
                            } else if (hour >= 18 && hour < 24) {
                                i4++;
                            }
                        }
                    }
                    Map<String, Object> map11 = new HashMap<String, Object>();
                    map11.put("name", "00:00-06:00");
                    map11.put("value", i1);
                    l1.add(map11);
                    Map<String, Object> map12 = new HashMap<String, Object>();
                    map12.put("name", "06:00-12:00");
                    map12.put("value", i2);
                    l1.add(map12);
                    Map<String, Object> map13 = new HashMap<String, Object>();
                    map13.put("name", "12:00-18:00");
                    map13.put("value", i3);
                    l1.add(map13);
                    Map<String, Object> map14 = new HashMap<String, Object>();
                    map14.put("name", "18:00-24:00");
                    map14.put("value", i4);
                    l1.add(map14);
                    m1.put(model.getInitAddress(), l1);
                    list6.add(m1);
                }
                //时间占比分析
                int i1=0;
                int i2=0;
                int i3=0;
                int i4=0;
                for(Map<String,Object> map1:list){
                    Date createDate = sdf.parse(map1.get("create_date").toString());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createDate);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if(hour>=0&&hour<6){
                        i1++;
                    }else if(hour>=6&&hour<12){
                        i2++;
                    }else if(hour>=12&&hour<18){
                        i3++;
                    }else if(hour>=18&&hour<24){
                        i4++;
                    }
                }
                Map<String,Object> map11 = new HashMap<String,Object>();
                map11.put("name","00:00-06:00");
                map11.put("value",i1);
                list5.add(map11);
                Map<String,Object> map12 = new HashMap<String,Object>();
                map12.put("name","06:00-12:00");
                map12.put("value",i2);
                list5.add(map12);
                Map<String,Object> map13 = new HashMap<String,Object>();
                map13.put("name","12:00-18:00");
                map13.put("value",i3);
                list5.add(map13);
                Map<String,Object> map14 = new HashMap<String,Object>();
                map14.put("name","18:00-24:00");
                map14.put("value",i4);
                list5.add(map14);
            }
            //出车趋势图
            List<String> dateList = getDate();
            List<Map<String,Object>> data = attendanceService.queryChart2(dateList.get(0),dateList.get(dateList.size()-1));
            for(Location model :locations) {
                List<Map<String,Object>> l =  new ArrayList<Map<String,Object>>();
                Map<String,Object> address = new HashMap<String,Object>();
                for(int i=0;i<dateList.size();i++){
                    Map<String,Object> m = new HashMap<String,Object>();
                    m.put(dateList.get(i),getData(dateList.get(i),model.getInitAddress(),data));
                    l.add(m);
                }
                address.put(model.getInitAddress(),l);
                list2.add(address);
            }

            map.put("chart1",attendanceService.queryChart1("1"));
            map.put("chart2",list2);
            map.put("chart4",attendanceService.queryChart4());
            map.put("chart5",list5);
            map.put("chart6",list6);
            map.put("chart7",attendanceService.queryChart7());
            envelop.setSuccessFlg(true);
            envelop.setObj(map);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 获取近一个月日期
     * @return
     */
    public List<String> getDate(){
        List<String> list = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        for(int i=29;i>1;i--) {
            calendar.setTime(new Date());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) -i);
            list.add(sdf.format(calendar.getTime()));
        }
        calendar.setTime(new Date());
        list.add(sdf.format(calendar.getTime()));
        return list;
    }

    /**
     * 根据日期和地点获取总数
     * @param date
     * @param address
     * @return
     */
    public int getData(String date, String address,List<Map<String,Object>> data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int i=0;
        if(!data.isEmpty()){
            for(Map<String,Object> map :data){
                if(date.equals(sdf.format(map.get("create_date")))&&address.equals(map.get("name"))){
                    i+=Integer.parseInt(map.get("value")+"");
                }
            }
        }
        return i;
    }
}
