package com.yihu.ehr.portal.service.function;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalFeedback;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by yeshijie on 2017/2/21.
 */
@FeignClient(name= MicroServices.Portal)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PortalFeedbackClient {

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈列表", notes = "根据查询条件获取意见反馈列表在前端表格展示")
    ResponseEntity<List<MPortalFeedback>> searchPortalFeedback(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedbackAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取意见反馈信息", notes = "意见反馈信息")
    MPortalFeedback getPortalFeedback(@PathVariable(value = "portalFeedback_id") Long portalFeedbackId);

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建意见反馈", notes = "重新绑定意见反馈信息")
    MPortalFeedback createPortalFeedback(
            @ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
            @RequestBody String portalFeedbackJsonData);

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedback, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改意见反馈", notes = "重新绑定意见反馈信息")
    MPortalFeedback updatePortalFeedback(@ApiParam(name = "portalFeedback_json_data", value = "", defaultValue = "")
                                         @RequestBody String portalFeedbackJsonData);

    @RequestMapping(value = ServiceApi.PortalFeedback.PortalFeedbackAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除意见反馈", notes = "根据意见反馈id删除")
    boolean deletePortalFeedback(@PathVariable(value = "portalFeedback_id") String portalFeedbackId);

}
