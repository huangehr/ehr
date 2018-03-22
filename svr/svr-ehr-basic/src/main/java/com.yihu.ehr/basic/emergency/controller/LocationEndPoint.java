package com.yihu.ehr.basic.emergency.controller;

import com.yihu.ehr.basic.emergency.service.AmbulanceService;
import com.yihu.ehr.basic.emergency.service.LocationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.entity.emergency.Location;
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
public class LocationEndPoint extends EnvelopRestEndPoint {

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
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<Location> locations = locationService.search(fields, filters, sorts, page, size);
        int count = (int)locationService.getCount(filters);
        Envelop envelop = getPageResult(locations, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "location", value = "待命地点")
            @RequestBody String location) throws Exception {
        Location newLocation = objectMapper.readValue(location, Location.class);
        Location location1 = locationService.save(newLocation);
        return success(location1);
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    public Envelop update(
            @ApiParam(name = "location", value = "排班")
            @RequestBody String location) throws Exception {
        Location newLocation = objectMapper.readValue(location, Location.class);
        Location oldLocation = locationService.findById(newLocation.getId());
        if (oldLocation == null) {
            return failed("无相关待命地点记录");
        }
        Location location1 = locationService.save(newLocation);
        return success(location1);
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除待命地点")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表(int)1,2,3,...")
            @RequestParam(value = "ids") String ids) throws Exception {
        //List<Integer> idList = toEntity(ids, List.class);
        List<Ambulance> ambulanceList = ambulanceService.search("location=" + ids);
        if (ambulanceList == null || ambulanceList.size() > 0) {
            return failed("不能删除已有关联车辆的待命地点");
        }
        String [] idArrStr = ids.split(",");
        Integer [] idArrInt = new Integer[idArrStr.length];
        for (int i = 0; i < idArrStr.length; i++) {
            idArrInt[i] = new Integer(idArrStr[i]);
        }
        locationService.delete(idArrInt);
        return success(true);
    }

}
