package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.emergency.client.AmbulanceClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "AmbulanceController", description = "救护车信息", tags = {"应急指挥-救护车信息"})
public class AmbulanceController extends BaseController {

    @Autowired
    private AmbulanceClient ambulanceClient;

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有救护车列表")
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
        return ambulanceClient.list(fields, filters, sorts, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSearch , method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车信息以及包括执勤人员信息，检索条件只针对车辆")
    public Envelop search(
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
        return ambulanceClient.search(fields, filters, sorts, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdateStatus, method = RequestMethod.PUT)
    @ApiOperation(value = "更新救护车状态信息")
    public Envelop update(
            @ApiParam(name = "carId", value = "车牌号码")
            @RequestParam(value = "carId") String carId,
            @ApiParam(name = "status", value = "车辆状态码(0为统一可用状态码，1为统一不可用状态码)")
            @RequestParam(value = "status") String status) {
        return ambulanceClient.updateStatus(carId, status);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "ambulance", value = "救护车")
            @RequestParam(value = "ambulance") String ambulance){
        return ambulanceClient.save(ambulance);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录")
    public Envelop update(
            @ApiParam(name = "ambulance", value = "救护车")
            @RequestParam(value = "ambulance") String ambulance){
        return ambulanceClient.update(ambulance);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceDelete, method = RequestMethod.DELETE)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表['xxxx','xxxx','xxxx'...] String")
            @RequestParam(value = "ids") String ids){
        return ambulanceClient.delete(ids);
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulanceIdOrPhoneExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在车牌号、电话号码")
    public List idExistence(
            @ApiParam(name = "type", value = "字段名", defaultValue = "")
            @RequestParam(value ="type") String type,
            @ApiParam(name = "values", value = "", defaultValue = "")
            @RequestParam(value ="values") String values) throws Exception {
        List vals = ambulanceClient.idExistence(type,values);
        return vals;
    }

    @RequestMapping(value = ServiceApi.Emergency.AmbulancesBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入救护车")
    public Envelop createAmbulancesBatch(
            @ApiParam(name = "ambulances", value = "救护车", defaultValue = "")
            @RequestParam(value = "ambulances") String ambulances) throws Exception {
        Envelop envelop = new Envelop();
        try{
            ambulanceClient.createAmbulancesBatch(ambulances);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("导入失败！" + e.getMessage());
        }
        return envelop;
    }
}
