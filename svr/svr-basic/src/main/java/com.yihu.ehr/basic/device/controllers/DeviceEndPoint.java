package com.yihu.ehr.basic.device.controllers;

import com.yihu.ehr.basic.device.service.DeviceService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.device.Device;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "DeviceEndPoint", description = "设备管理", tags = {"基础信息-设备管理"})
public class DeviceEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DeviceService deviceService;
    @RequestMapping(value = ServiceApi.Device.DeviceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有列表")
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
        List<Device> list = deviceService.search(fields, filters, sorts, page, size);
        int count = (int)deviceService.getCount(filters);
        Envelop envelop = getPageResult(list, count, page, size);
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Device.DeviceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存")
    public Envelop save(
            @ApiParam(name = "modelStr", value = "实体类Json")
            @RequestBody String modelStr) throws Exception {
        Device model = objectMapper.readValue(modelStr, Device.class);
        model = deviceService.save(model);
        return success(model);
    }


    @RequestMapping(value = ServiceApi.Device.DeviceDelete, method = RequestMethod.POST)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表", required = true)
            @RequestParam(value = "ids") String ids) throws Exception {
        String [] idArr = ids.split(",");

        Integer[] arr = new Integer[idArr.length];
        for (int i = 0; i <idArr.length; i++) {
            arr[i] = Integer.parseInt(idArr[i]);
        }
        deviceService.delete(arr);
        return success(true);
    }

    @RequestMapping(value = ServiceApi.Device.FindById, method = RequestMethod.GET)
    @ApiOperation("获取单条记录")
    public Envelop findById(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception{
        Device model = deviceService.findById(Integer.parseInt(id));
        if (null != model) {
            return success(model);
        }
        return failed("无相关数据");
    }

    @RequestMapping(value = ServiceApi.Device.DeviceBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入")
    public boolean addBatch(
            @ApiParam(name = "devices", value = "实体类列表Json")
            @RequestBody String devices) throws Exception{
        List list = objectMapper.readValue(devices, List.class);
        deviceService.addBatch(list);
        return true;
    }

}
