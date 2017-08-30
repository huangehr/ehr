package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsReport;
import com.yihu.ehr.resource.model.RsReport;
import com.yihu.ehr.resource.service.RsReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源报表 服务接口
 *
 * @author 张进军
 * @created 2017.8.15 19:18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "资源报表服务接口")
public class RsReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportService rsReportService;

    @ApiOperation("根据ID获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReport, method = RequestMethod.GET)
    public MRsReport getById(
            @ApiParam(name = "id", value = "id", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(rsReportService.getById(id), MRsReport.class);
    }

    @ApiOperation(value = "根据条件获取资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReports, method = RequestMethod.GET)
    List<MRsReport> search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsReport> rsReports = rsReportService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsReportService.getCount(filters), page, size);
        return (List<MRsReport>) convertToModels(rsReports, new ArrayList<MRsReport>(), MRsReport.class, fields);
    }

    @ApiOperation("新增资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.POST)
    public MRsReport add(
            @ApiParam(name = "rsReport", value = "资源报表JSON", required = true)
            @RequestParam(value = "rsReport") String rsReport) throws Exception {
        RsReport newRsReport = toEntity(rsReport, RsReport.class);
        newRsReport = rsReportService.save(newRsReport);
        return convertToModel(newRsReport, MRsReport.class);
    }

    @ApiOperation("更新资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportSave, method = RequestMethod.PUT)
    public MRsReport update(
            @ApiParam(name = "rsReport", value = "资源报表JSON", required = true)
            @RequestParam(value = "rsReport") String rsReport) throws Exception {
        RsReport newRsReport = toEntity(rsReport, RsReport.class);
        newRsReport = rsReportService.save(newRsReport);
        return convertToModel(newRsReport, MRsReport.class);
    }

    @ApiOperation("删除资源报表")
    @RequestMapping(value = ServiceApi.Resources.RsReportDelete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        rsReportService.delete(id);
    }

    @ApiOperation("验证资源报表编码是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueCode, method = RequestMethod.GET)
    public boolean isUniqueCode(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "资源报表编码", required = true)
            @RequestParam(value = "code") String code) throws Exception {
        return rsReportService.isUniqueCode(id, code);
    }

    @ApiOperation("验证资源报表名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportIsUniqueName, method = RequestMethod.GET)
    public boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "资源报表名称", required = true)
            @RequestParam(value = "name") String name) throws Exception {
        return rsReportService.isUniqueName(id, name);
    }

    @ApiOperation("查询报表信息（不分页）")
    @RequestMapping(value = ServiceApi.Resources.RsReportNoPage, method = RequestMethod.GET)
    public List<MRsReport> queryNoPageResources(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception{
        List<RsReport> list = rsReportService.search(filters);
        return (List<MRsReport>) convertToModels(list, new ArrayList<>(list.size()), MRsReport.class, null);
    }
}
