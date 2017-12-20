package com.yihu.ehr.emergency.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
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


/**
 * Client - 排班历史
 * Created by progr1mmer on 2017/11/8.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface ScheduleClient {

    @RequestMapping(value = ServiceApi.Emergency.ScheduleList, method = RequestMethod.GET)
    @ApiOperation("获取排班列表")
    Envelop list(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.ScheduleLevel, method = RequestMethod.GET)
    @ApiOperation("获取排班层级列表（年-月-日）")
    Envelop level(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size);

    @RequestMapping(value = ServiceApi.Emergency.ScheduleSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    Envelop save(
            @ApiParam(name = "schedule", value = "排班")
            @RequestBody String schedule);

    @RequestMapping(value = ServiceApi.Emergency.ScheduleUpdate, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新单条记录，只允许更新时间和状态")
    Envelop update(
            @ApiParam(name = "schedule", value = "排班")
            @RequestBody String schedule);

    @RequestMapping(value = ServiceApi.Emergency.ScheduleBatch, method = RequestMethod.POST)
    @ApiOperation("批量导入排班信息")
    boolean createSchedulesBatch(
            @ApiParam(name = "schedules", value = "排班")
            @RequestParam(value = "schedules") String schedules);

}
