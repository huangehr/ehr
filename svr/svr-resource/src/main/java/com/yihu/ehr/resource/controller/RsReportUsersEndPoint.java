package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.model.RsReport;
import com.yihu.ehr.resource.model.RsReportUsers;
import com.yihu.ehr.resource.service.RsReportUsersService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.List;

/**
 * Created by wxw on 2018/7/31.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsReportUsersEndPoint", description = "资源报表用户关系", tags = {"资源服务-资源报表用户关系"})
public class RsReportUsersEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportUsersService rsReportUsersService;

    @ApiOperation("根据用户ID获取资源报表列表")
    @RequestMapping(value = ServiceApi.Resources.QueryByUserId, method = RequestMethod.GET)
    public Envelop queryByUserId(
            @ApiParam(name = "userId", value = "用户编码", required = true)
            @RequestParam(value = "userId") String userId) throws Exception {
        Envelop envelop = new Envelop();
        List<RsReportUsers> rsReportUsersList = rsReportUsersService.getRsReportUsersList(userId);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rsReportUsersList);
        return envelop;
    }

    @ApiOperation("根据用户ID获取其他资源报表列表")
    @RequestMapping(value = ServiceApi.Resources.QueryOtherReportByUserId, method = RequestMethod.GET)
    public Envelop queryOtherReportByUserId(
            @ApiParam(name = "userId", value = "用户编码", required = true)
            @RequestParam(value = "userId") String userId) throws Exception {
        Envelop envelop = new Envelop();
        List<RsReport> rsReportList = rsReportUsersService.getOtherReportListByUserId(userId);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rsReportList);
        return envelop;
    }

    @ApiOperation("保存用户报表关系")
    @RequestMapping(value = ServiceApi.Resources.SaveByUserId, method = RequestMethod.POST)
    public Envelop saveByUserId(
            @ApiParam(name = "userId", value = "用户编码", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam String model) throws Exception {
        Envelop envelop = new Envelop();
        List<RsReportUsers> rsReportUsersList = objectMapper.readValue(URLDecoder.decode(model, "UTF-8"), new TypeReference<List<RsReportUsers>>() {});
        rsReportUsersService.saveRsReportUser(userId, rsReportUsersList);
        envelop.setSuccessFlg(true);
        return envelop;
    }
}
