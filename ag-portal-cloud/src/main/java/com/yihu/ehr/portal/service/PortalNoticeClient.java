package com.yihu.ehr.portal.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.portal.MPortalNotice;
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
public interface PortalNoticeClient {

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    ResponseEntity<List<MPortalNotice>> getPortalNoticeTop10();

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告列表", notes = "根据查询条件获取通知公告列表在前端表格展示")
    ResponseEntity<List<MPortalNotice>> searchPortalNotices(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告信息", notes = "通知公告信息")
    MPortalNotice getPortalNotice(@PathVariable(value = "portalNotice_id") Long portalNoticeId);
}
