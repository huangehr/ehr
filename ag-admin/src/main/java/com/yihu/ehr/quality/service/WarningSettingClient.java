package com.yihu.ehr.quality.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 质控-预警设置
 * @author yeshijie on 2018/6/5.
 */
@FeignClient(name= MicroServices.Analyzer)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface WarningSettingClient{

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台接收预警列表")
    Envelop paltformReceiveWarningList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台资源化预警列表")
    Envelop paltformResourceWarningList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "平台上传预警列表")
    Envelop paltformUploadWarningList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台接收预警")
    Envelop getMDqPaltformReceiveWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台资源化预警")
    Envelop getMDqPaltformResourceWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarning, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台上传预警")
    Envelop getMDqPaltformUploadWarningById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台接收预警")
    Envelop paltformReceiveWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台资源化预警")
    Envelop paltformResourceWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台上传预警")
    Envelop paltformUploadWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台接收预警")
    Envelop paltformReceiveWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam(value = "id", required = true) Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台资源化预警")
    Envelop paltformResourceWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam(value = "id", required = true) Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除平台上传预警")
    Envelop paltformUploadWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam(value = "id", required = true) Long id);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformReceiveWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台接收预警")
    Envelop paltformReceiveWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformResourceWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台资源化预警")
    Envelop paltformResourceWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.PaltformUploadWarningUpd, method = RequestMethod.POST)
    @ApiOperation(value = "新增平台上传预警")
    Envelop paltformUploadWarningUpd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.DatasetWarningAdd, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集")
    Envelop datasetWarningAdd(
            @ApiParam(name = "jsonData", value = "对象JSON结构体",  defaultValue = "")
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.DataQuality.DatasetWarningDel, method = RequestMethod.POST)
    @ApiOperation(value = "删除数据集")
    Envelop datasetWarningDel(
            @ApiParam(name = "id", value = "1",  defaultValue = "")
            @RequestParam(value = "id", required = true) Long id);

}
