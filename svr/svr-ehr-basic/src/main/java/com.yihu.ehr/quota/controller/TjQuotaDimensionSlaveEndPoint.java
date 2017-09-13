package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.TjQuotaDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.quota.service.TjQuotaDimensionSlaveService;
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
@Api(value = "TjQuotaDimensionSlave", description = "统计指标从维度关联信息", tags = {"从统计指标从维度关联信息"})
public class TjQuotaDimensionSlaveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TjQuotaDimensionSlaveService tjQuotaDimensionSlaveService;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询统计指标从维度关联信息")
    public ListResult getTjQuotaDimensionSlaveList(
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
        List<TjQuotaDimensionSlave> qtjQuotaDimensionSlaveList = tjQuotaDimensionSlaveService.search(fields, filters, sorts, page, size);
        if(qtjQuotaDimensionSlaveList != null){
            listResult.setDetailModelList(qtjQuotaDimensionSlaveList);
            listResult.setTotalCount((int)tjQuotaDimensionSlaveService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取从维度子表中的所有mainCode")
    ListResult getTjQuotaDimensionSlaveAll(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult listResult = new ListResult();
        List<TjQuotaDimensionSlave> qtjQuotaDimensionSlaveList = tjQuotaDimensionSlaveService.search(filters, sorts);
        if(qtjQuotaDimensionSlaveList != null){
            listResult.setDetailModelList(qtjQuotaDimensionSlaveList);
            listResult.setTotalCount((int)tjQuotaDimensionSlaveService.getCount(filters));
            listResult.setCode(200);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }
    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改统计指标从维度关联信息")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjQuotaDimensionSlave obj = objectMapper.readValue(model, TjQuotaDimensionSlave.class);
        obj = tjQuotaDimensionSlaveService.save(obj);
        return Result.success("统计指标从维度关联信息更新成功！", obj);
    }

    @RequestMapping(value = ServiceApi.TJ.AddTjQuotaDimensionSlave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改统计指标从维度关联信息")
    public ObjectResult addTjQuotaDimensionSlave(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        List<TjQuotaDimensionSlave> list = objectMapper.readValue(model, new TypeReference<List<TjQuotaDimensionSlave>>(){});
        if (list != null && list.size() > 0) {
            tjQuotaDimensionSlaveService.deleteByQuotaCode(list.get(0).getQuotaCode());
        }
        for (int i=0; i<list.size(); i++) {
            tjQuotaDimensionSlaveService.save(list.get(i));
        }
        return Result.success("统计指标从维度关联信息更新成功！", list);
    }

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标从维度关联信息")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        tjQuotaDimensionSlaveService.delete(id);
        return Result.success("统计指标从维度关联信息删除成功！");
    }

    @RequestMapping(value = "/tj/deleteSlaveByQuotaCode", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标从维度关联信息")
    public Result deleteSlaveByQuotaCode(
            @ApiParam(name = "quotaCode", value = "指标Id", defaultValue = "")
            @RequestParam(value = "quotaCode") String quotaCode) throws Exception{
        tjQuotaDimensionSlaveService.deleteByQuotaCode(quotaCode);
        return Result.success("保存成功！");
    }
}
