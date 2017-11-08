package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.resource.MRsReportMonitorType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 资源报表监测监测分类 client
 *
 * @author janseny
 * @created 2017年11月7日15:05:53
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsReportMonitorTypeClient {

    @ApiOperation("根据ID获取资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorType, method = RequestMethod.GET)
    MRsReportMonitorType getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id);

    @ApiOperation("新增资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.POST)
    MRsReportMonitorType add(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON字符串", required = true)
            @RequestBody String rsReportMonitorType);

    @ApiOperation("更新资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.PUT)
    MRsReportMonitorType update(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON字符串", required = true)
            @RequestBody String rsReportMonitorType);

    @ApiOperation("删除资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeDelete, method = RequestMethod.DELETE)
    void delete(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam(value = "id") Integer id);

    @ApiOperation("验证资源报表监测分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeIsUniqueName, method = RequestMethod.GET)
    Boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam("id") Integer id,
            @ApiParam(name = "name", value = "资源报表监测分类名称", required = true)
            @RequestParam("name") String name);

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypes, method = RequestMethod.GET)
    @ApiOperation(value = "资源报表监测分类列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypesNoPage, method = RequestMethod.GET)
    @ApiOperation("获取资源报表类别")
    List<MRsReportMonitorType> getAll(@RequestParam(value="filters",required = false)String filters);

}
