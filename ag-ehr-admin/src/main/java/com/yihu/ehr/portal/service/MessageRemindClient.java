package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MMessageRemind;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/17.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface MessageRemindClient {

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息列表", notes = "根据查询条件获取提醒消息列表在前端表格展示")
    ResponseEntity<List<MMessageRemind>> searchMessageRemind(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取提醒消息信息", notes = "提醒消息信息")
    MMessageRemind getMessageRemind(@PathVariable(value = "messageRemind_id") Long messageRemindId);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建提醒消息", notes = "重新绑定提醒消息信息")
    MMessageRemind createMessageRemind(
            @ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
            @RequestBody String messageRemindJsonData);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemind, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改提醒消息", notes = "重新绑定提醒消息信息")
    MMessageRemind updateMessageRemind(@ApiParam(name = "messageRemind_json_data", value = "", defaultValue = "")
                                     @RequestBody String messageRemindJsonData);

    @RequestMapping(value = ServiceApi.MessageRemind.MessageRemindAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除提醒消息", notes = "根据提醒消息id删除消息信息")
    boolean deleteMessageRemind(@PathVariable(value = "messageRemind_id") String messageRemindId);

}
