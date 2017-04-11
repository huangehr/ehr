package com.yihu.ehr.portal.service;

        import com.yihu.ehr.constants.ServiceApi;
        import com.yihu.ehr.constants.ApiVersion;
        import com.yihu.ehr.constants.MicroServices;
        import com.yihu.ehr.model.portal.MPortalNotice;
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
public interface PortalNoticesClient {

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

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建通知公告", notes = "重新绑定通知公告信息")
    MPortalNotice createPortalNotice(
            @ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
            @RequestBody String portalNoticeJsonData);

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改通知公告", notes = "重新绑定通知公告信息")
    MPortalNotice updatePortalNotice(@ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
                                     @RequestBody String portalNoticeJsonData);

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除通知公告", notes = "根据通知公告id删除医生")
    boolean deletePortalNotice(@PathVariable(value = "portalNotice_id") String portalNoticeId);

}
