package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.resource.MRsReportMonitorType;
import com.yihu.ehr.resource.model.RsReport;
import com.yihu.ehr.resource.model.RsReportMonitorType;
import com.yihu.ehr.resource.service.RsReportMonitorTypeService;
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
 * Created by jansny 2017年11月7日15:17:24
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "资源报表监测分类服务接口", description = "资源报表-资源报表监测分类服务接口")
public class RsResourceMonitorTypeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsReportMonitorTypeService rsReportMonitorTypeService;

    @ApiOperation("根据ID获取资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorType, method = RequestMethod.GET)
    public MRsReportMonitorType getById(
            @ApiParam(name = "id", value = "id", required = true)
            @PathVariable(value = "id") Integer id) throws Exception {
        return convertToModel(rsReportMonitorTypeService.getById(id), MRsReportMonitorType.class);
    }

    @ApiOperation("新增资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.POST)
    public MRsReportMonitorType add(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON", required = true)
            @RequestBody String rsReportMonitorType) throws Exception {
        RsReportMonitorType newRsReportMonitorType = toEntity(rsReportMonitorType, RsReportMonitorType.class);
        newRsReportMonitorType = rsReportMonitorTypeService.save(newRsReportMonitorType);
        return convertToModel(newRsReportMonitorType, MRsReportMonitorType.class);
    }

    @ApiOperation("更新资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeSave, method = RequestMethod.PUT)
    public MRsReportMonitorType update(
            @ApiParam(name = "rsReportMonitorType", value = "资源报表监测分类JSON", required = true)
            @RequestBody String rsReportMonitorType) throws Exception {
        RsReportMonitorType newRsReportMonitorType = toEntity(rsReportMonitorType, RsReportMonitorType.class);
        newRsReportMonitorType = rsReportMonitorTypeService.save(newRsReportMonitorType);
        return convertToModel(newRsReportMonitorType, MRsReportMonitorType.class);
    }

    @ApiOperation("删除资源报表监测分类")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeDelete, method = RequestMethod.DELETE)
    public void delete(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        rsReportMonitorTypeService.delete(id);
    }

    @ApiOperation("验证资源报表监测分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypeIsUniqueName, method = RequestMethod.GET)
    public boolean isUniqueName(
            @ApiParam(name = "id", value = "资源报表监测分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "资源报表监测分类名称", required = true)
            @RequestParam(value = "name") String name) throws Exception {
        return rsReportMonitorTypeService.isUniqueName(id, name);
    }

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypes, method = RequestMethod.GET)
    @ApiOperation(value = "资源报表监测分类列表")
    public ListResult getQcQuotaDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<RsReportMonitorType> rsReportMonitorTypeList = rsReportMonitorTypeService.search(fields, filters, null, page, size);
        if(rsReportMonitorTypeList != null){
            listResult.setDetailModelList(rsReportMonitorTypeList);
            listResult.setTotalCount((int)rsReportMonitorTypeService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypesNoPage, method = RequestMethod.GET)
    @ApiOperation("获取资源报表监测分类")
    public List<MRsReportMonitorType> getAll(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<RsReportMonitorType> list = rsReportMonitorTypeService.search(filters);

        return (List<MRsReportMonitorType>) convertToModels(list, new ArrayList<>(list.size()), MRsReportMonitorType.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.RsReportMonitorTypesById, method = RequestMethod.GET)
    @ApiOperation("获取报表监测分类")
    public List<MRsReportMonitorType> getInfoById (
            @ApiParam(name = "monitorTypeIds", value = "类型id", defaultValue = "")
            @RequestParam(value = "monitorTypeIds", required = false) List<Integer> monitorTypeIds) {
        List<RsReportMonitorType> monitorTypes = rsReportMonitorTypeService.getInfoById(monitorTypeIds);
        return (List<MRsReportMonitorType>) convertToModels(monitorTypes, new ArrayList<>(monitorTypes.size()), MRsReportMonitorType.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.RsReportByMonitorTypeId, method = RequestMethod.GET)
    @ApiOperation("获取报表信息")
    public List<RsReport> getRsReportByMonitorTypeId(
            @ApiParam(name = "monitorTypeId", value = "类型id", defaultValue = "")
            @RequestParam(value = "monitorTypeId")Integer monitorTypeId) throws Exception {
        List<RsReport> reportList = rsReportMonitorTypeService.getRsReportByMonitorTypeId(monitorTypeId);
        return reportList;
    }
}
