package com.yihu.ehr.device.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.device.Device;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * @Author: zhengwei
 * @Date: 2018/5/4 15:47
 * @Description: 设备管理
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface DeviceClient {

    @RequestMapping(value = ServiceApi.Device.DeviceList, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有列表")
    Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size);


    @RequestMapping(value = ServiceApi.Device.DeviceSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存")
    Envelop save(
            @ApiParam(name = "device", value = "实体类Json")
            @RequestBody String device);


    @RequestMapping(value = ServiceApi.Device.DeviceDelete, method = RequestMethod.POST)
    @ApiOperation("删除记录")
    Envelop delete(
            @ApiParam(name = "ids", value = "id列表", required = true)
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Device.FindById, method = RequestMethod.GET)
    @ApiOperation("获取单条记录")
    Envelop findById(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Device.DeviceBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入")
    boolean addBatch(
            @ApiParam(name = "devices", value = "实体类列表Json")
            @RequestBody String devices);
}
