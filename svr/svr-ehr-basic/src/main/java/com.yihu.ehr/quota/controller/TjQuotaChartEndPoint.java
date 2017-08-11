package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.TjQuotaChart;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.quota.service.TjQuotaChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjQuotaChart", description = "指标统计管理", tags = {"指标统计管理--指标输出报表"})
public class TjQuotaChartEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TjQuotaChartService tjQuotaChartService;

    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标输出报表")
    public ListResult getTjQuotaChartList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<TjQuotaChart> tjQuotaChartList = tjQuotaChartService.search(fields, filters, sorts, page, size);
        if(tjQuotaChartList != null){
            listResult.setDetailModelList(tjQuotaChartList);
            listResult.setTotalCount((int)tjQuotaChartService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改指标输出报表")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjQuotaChart obj = objectMapper.readValue(model,TjQuotaChart.class);
        obj = tjQuotaChartService.save(obj);
        return Result.success("指标输出报表更新成功！", obj);
    }

    @RequestMapping(value = ServiceApi.TJ.BatchTjQuotaChart, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改指标输出报表信息")
    public ObjectResult batchAddTjQuotaChart(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        List<TjQuotaChart> list = objectMapper.readValue(model, new TypeReference<List<TjQuotaChart>>(){});
        if (list != null && list.size() > 0) {
            tjQuotaChartService.deleteByQuotaCode(list.get(0).getQuotaCode());
        }
        for (int i=0; i<list.size(); i++) {
            tjQuotaChartService.save(list.get(i));
        }
        return Result.success("指标输出报表信息更新成功！", list);
    }


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标输出报表")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        tjQuotaChartService.delete(id);
        return Result.success("统计指标删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaChartId, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标输出报表信息", notes = "获取指标输出报表信息")
    TjQuotaChart getTjDimensionMain(
            @PathVariable(value = "id") Integer id){
        return tjQuotaChartService.getTjQuotaChart(id);
    };


    @RequestMapping(value = ServiceApi.TJ.GetAllTjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有选中的图表")
    ListResult getTjQuotaDimensionSlaveAll(
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        ListResult listResult = new ListResult();
        List<TjQuotaChart> tjQuotaCharts = tjQuotaChartService.search(filters, null);
        if(tjQuotaCharts != null){
            listResult.setDetailModelList(tjQuotaCharts);
            listResult.setTotalCount((int)tjQuotaChartService.getCount(filters));
            listResult.setCode(200);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

}
