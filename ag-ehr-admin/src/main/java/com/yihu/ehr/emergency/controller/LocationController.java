package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.client.LocationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller - 待命地点
 * Created by progr1mmer on 2017/11/22.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "LocationController", description = "待命地点", tags = {"应急指挥-待命地点"})
public class LocationController extends BaseRestEndPoint {

    @Autowired
    private LocationClient locationClient;

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
            @RequestParam(value = "size") int size) {
        return locationClient.list(fields, filters, sorts, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationSave, method = RequestMethod.POST)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "location", value = "待命地点")
            @RequestParam(value = "location") String location){
        return locationClient.save(location);
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationUpdate, method = RequestMethod.PUT)
    @ApiOperation("更新单条记录")
    public Envelop update(
            @ApiParam(name = "location", value = "排班")
            @RequestParam(value = "location") String location){
        return locationClient.update(location);
    }

    @RequestMapping(value = ServiceApi.Emergency.LocationDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除待命地点")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表(int)1,2,3,...")
            @RequestParam(value = "ids") String ids){
        return locationClient.delete(ids);
    }

}
