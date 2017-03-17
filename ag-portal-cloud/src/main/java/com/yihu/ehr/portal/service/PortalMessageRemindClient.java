package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MMessageRemind;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by ysj on 2017年2月20日11:30:03.
 */
@FeignClient(name=MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalMessageRemindClient {

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    ResponseEntity<List<MMessageRemind>> getMessageRemindTop10();

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息列表", notes = "根据查询条件获取消息提醒信息列表在前端表格展示")
    ResponseEntity<List<MMessageRemind>> searchMessageRemind(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息信息", notes = "消息提醒信息信息")
    MMessageRemind getMessageRemind(@PathVariable(value = "messageRemind_id") Long messageRemindId);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindCount, method = RequestMethod.GET)
    @ApiOperation(value = "获取消息提醒信息数量", notes = "消息提醒数量")
    int getMessageRemindCount(@RequestParam(value = "filters") String filters);

}
