package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.emergency.client.ScheduleClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + ServiceApi.GateWay.admin)
@Api(value = "ScheduleController", description = "排班历史", tags = {"应急指挥-排班历史"})
public class ScheduleController extends BaseController {

    @Autowired
    private ScheduleClient scheduleClient;

    @RequestMapping(value = ServiceApi.Emergency.ScheduleList, method = RequestMethod.GET)
    @ApiOperation("获取排班列表")
    public Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required =  true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required =  true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        return scheduleClient.list(fields, filters, sorts, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleLevel, method = RequestMethod.GET)
    @ApiOperation("获取排班层级列表（年-月-日）")
    public Envelop level(
            @ApiParam(name = "date", value = "年-月")
            @RequestParam(value = "date", required = false) String date,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        return scheduleClient.level(date, page, size);
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleSave, method = RequestMethod.POST)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "schedule", value = "排班")
            @RequestParam(value = "schedule") String schedule){
        return scheduleClient.save(schedule);
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleUpdate, method = RequestMethod.PUT)
    @ApiOperation("更新单条记录，只允许更新时间和状态")
    public Envelop update(
            @ApiParam(name = "schedule", value = "排班")
            @RequestParam(value = "schedule") String schedule){
        return scheduleClient.update(schedule);
    }

    @RequestMapping(value = ServiceApi.Emergency.ScheduleBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入排班信息")
    public Envelop createSchedulesBatch(
            @ApiParam(name = "schedules", value = "排班信息", defaultValue = "")
            @RequestParam(value = "schedules") String schedules) throws Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try{
            scheduleClient.createSchedulesBatch(schedules);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("导入失败！"+e.getMessage());
        }
        return envelop;
    }


}
