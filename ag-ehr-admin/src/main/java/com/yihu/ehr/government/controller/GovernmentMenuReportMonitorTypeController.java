package com.yihu.ehr.government.controller;

import com.yihu.ehr.agModel.government.GovernmentMenuReportMonitorTypeModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.government.service.GovernmentMenuReportMonitorTypeClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxw on 2017/11/2.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "governmentMenuReportMonitorType", description = "政府服务平台菜单报表监测类型接口", tags = {"政府服务平台-菜单"})
public class GovernmentMenuReportMonitorTypeController extends BaseController {
    @Autowired
    private GovernmentMenuReportMonitorTypeClient governmentMenuReportMonitorTypeClient;
    @Autowired
    private UserClient userClient;

    @ApiOperation("政府服务平台菜单报表监测类型")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeSave, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "rsReoportMonitorTypeId", value = "资源报表监测分类ID", required = true)
            @RequestParam(value = "rsReoportMonitorTypeId") String rsReoportMonitorTypeId) throws Exception {
        Envelop envelop = new Envelop();
        try {
            GovernmentMenuReportMonitorTypeModel newMGovernmentMenuReportMonitorType = governmentMenuReportMonitorTypeClient.add(rsReoportMonitorTypeId);
            envelop.setObj(newMGovernmentMenuReportMonitorType);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(GovernmentMenuReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }

    @ApiOperation("删除政府服务平台菜单报表监测类型")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeDelete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "主键", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        Envelop envelop = new Envelop();
        try {
            governmentMenuReportMonitorTypeClient.delete(id);
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            LogService.getLogger(GovernmentMenuReportMonitorTypeController.class).error(e.getMessage());
            return failed(ErrorCode.SystemError.toString());
        }
    }


}
