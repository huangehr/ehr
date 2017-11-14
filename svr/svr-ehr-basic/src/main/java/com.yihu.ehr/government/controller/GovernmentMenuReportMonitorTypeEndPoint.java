package com.yihu.ehr.government.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.government.GovernmentMenuReportMonitorType;
import com.yihu.ehr.government.service.GovernmentMenuReportMonitorTypeService;
import com.yihu.ehr.model.resource.MRsReportMonitorType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Created by jansny 2017年11月7日15:17:24
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "GovernmentMenuReportMonitorType", description = "政府平台菜单资源报表监测监测分类服务接口", tags = {"政府服务平台"})
public class GovernmentMenuReportMonitorTypeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private GovernmentMenuReportMonitorTypeService governmentMenuReportMonitorTypeService;

    @ApiOperation("新增政府平台菜单资源报表监测分类")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeSave, method = RequestMethod.POST)
    public MRsReportMonitorType add(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON", required = true)
            @RequestBody String rsReportMonitorType) throws Exception {
        GovernmentMenuReportMonitorType newGovernmentMenuMonitorType = toEntity(rsReportMonitorType, GovernmentMenuReportMonitorType.class);
        newGovernmentMenuMonitorType = governmentMenuReportMonitorTypeService.save(newGovernmentMenuMonitorType);
        return convertToModel(newGovernmentMenuMonitorType, MRsReportMonitorType.class);
    }

    @ApiOperation("删除政府平台菜单资源报表监测分类")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeDelete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        governmentMenuReportMonitorTypeService.delete(id);
    }

    @ApiOperation("获取政府菜单资源报表监测类型Id")
    @RequestMapping(value = ServiceApi.Government.MonitorTypeList, method = RequestMethod.GET)
    public List<Integer> getMonitorTypeIdByGovernmentMenuId(
            @ApiParam(name = "menuId", value = "菜单ID", required = true)
            @RequestParam(value = "menuId") String menuId) {
        return governmentMenuReportMonitorTypeService.getMonitorTypeIdByGovernmentMenuId(menuId);
    }
}
