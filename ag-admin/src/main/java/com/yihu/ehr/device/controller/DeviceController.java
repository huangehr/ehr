package com.yihu.ehr.device.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.device.client.DeviceClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "DeviceController", description = "设备管理", tags = {"基础信息-设备管理"})
public class DeviceController extends BaseController {

    @Autowired
    private DeviceClient deviceClient;

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
        return deviceClient.list(fields, filters, sorts, page, size);
    }


    @RequestMapping(value = ServiceApi.Device.DeviceSave, method = RequestMethod.POST)
    @ApiOperation("保存")
    public Envelop save(
            @ApiParam(name = "device", value = "实体类Json")
            @RequestParam(value = "device") String device) throws Exception {
       return deviceClient.save(device);
    }


    @RequestMapping(value = ServiceApi.Device.DeviceDelete, method = RequestMethod.POST)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表", required = true)
            @RequestParam(value = "ids") String ids) throws Exception {
        return deviceClient.delete(ids);
    }

    @RequestMapping(value = ServiceApi.Device.FindById, method = RequestMethod.GET)
    @ApiOperation("获取单条记录")
    public Envelop findById(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id){
        return deviceClient.findById(id);
    }

    @RequestMapping(value = ServiceApi.Device.DeviceBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入")
    public Envelop addBatch(
            @ApiParam(name = "devices", value = "实体类列表Json")
            @RequestParam(value = "devices") String devices) throws Exception{
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try{
            deviceClient.addBatch(devices);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("导入失败！"+e.getMessage());
        }
        return envelop;
    }
}
