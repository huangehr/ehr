package com.yihu.ehr.government.service;

import com.yihu.ehr.agModel.government.GovernmentMenuReportMonitorTypeModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 资源报表监测监测分类 client
 *
 * @author janseny
 * @created 2017年11月7日15:05:53
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface GovernmentMenuReportMonitorTypeClient {


    @ApiOperation("新增政府菜单资源报表监测分类")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeSave, method = RequestMethod.POST)
    GovernmentMenuReportMonitorTypeModel add(
            @ApiParam(name = "governmentMenuReportMonitorType", value = "政府菜单资源报表监测分类JSON字符串", required = true)
            @RequestBody String governmentMenuReportMonitorType);


    @ApiOperation("删除政府菜单资源报表监测分类")
    @RequestMapping(value = ServiceApi.Government.GovernmentMenuReportMonitorTypeDelete, method = RequestMethod.DELETE)
    void delete(
            @ApiParam(name = "id", value = "政府菜单资源报表监测分类ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("获取政府菜单资源报表监测类型Id")
    @RequestMapping(value = ServiceApi.Government.MonitorTypeList, method = RequestMethod.GET)
    List<Integer> getMonitorTypeIdByGovernmentMenuId(
            @ApiParam(name = "menuId", value = "菜单ID", required = true)
            @RequestParam(value = "menuId") String menuId);

}
