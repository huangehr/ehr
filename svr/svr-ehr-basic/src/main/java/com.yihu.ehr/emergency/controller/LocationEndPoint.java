package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.emergency.service.LocationService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Location;
import com.yihu.ehr.entity.emergency.Schedule;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * EndPoint - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "LocationEndPoint", description = "待命地点", tags = {"应急指挥-待命地点"})
public class LocationEndPoint extends BaseRestEndPoint {

    @Autowired
    private LocationService locationService;
    @Autowired
    private AmbulanceService ambulanceService;

    @RequestMapping(value = ServiceApi.Emergency.LocationList, method = RequestMethod.GET)
    @ApiOperation("获取待命地点列表")
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
            List<Schedule> schedules = locationService.search(fields, filters, sorts, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(schedules);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "location", value = "待命地点")
            @RequestBody String location){
        Envelop envelop = new Envelop();
        try {
            Location newLocation = objectMapper.readValue(location, Location.class);
            locationService.save(newLocation);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    public Envelop update(
            @ApiParam(name = "location", value = "排班")
            @RequestBody String location){
        Envelop envelop = new Envelop();
        try {
            Location newLocation = objectMapper.readValue(location, Location.class);
            Location oldLocation = locationService.findById(newLocation.getId());
            if (oldLocation == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("无相关待命地点记录");
                return envelop;
            }
            locationService.save(newLocation);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除待命地点")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表[1,2,3...] int")
            @RequestParam(value = "ids") String ids){
        Envelop envelop = new Envelop();
        List<Integer> idList = toEntity(ids, List.class);
        try {
            List<Ambulance> ambulanceList = ambulanceService.search("id=" + ids.substring(1, ids.length() - 1));
            if(ambulanceList == null || ambulanceList.size() > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("不能删除已有关联车辆的待命地点");
                return envelop;
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        locationService.delete(idList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
