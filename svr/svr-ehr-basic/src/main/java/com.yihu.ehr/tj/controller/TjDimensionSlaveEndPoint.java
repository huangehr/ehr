package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.tj.TjDimensionMain;
import com.yihu.ehr.entity.tj.TjDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDimensionSlaveService;
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
@Api(value = "TjDimensionSlave", description = "从纬度", tags = {"从纬度"})
public class TjDimensionSlaveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TjDimensionSlaveService tjDimensionSlaveService;

    @RequestMapping(value = ServiceApi.TJ.GetTjDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询从纬度")
    public ListResult getTjDimensionSlaveList(
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
        List<TjDimensionSlave> qtjDimensionSlaveList = tjDimensionSlaveService.search(fields, filters, sorts, page, size);
        if(qtjDimensionSlaveList != null){
            listResult.setDetailModelList(qtjDimensionSlaveList);
            listResult.setTotalCount((int)tjDimensionSlaveService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改从纬度")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjDimensionSlave obj = objectMapper.readValue(model, TjDimensionSlave.class);
        obj = tjDimensionSlaveService.save(obj);
        return Result.success("从纬度更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从纬度")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        tjDimensionSlaveService.delete(id);
        return Result.success("从纬度删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.TjDimensionSlaveCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取主纬度")
    public TjDimensionSlave getTjDimensionSlave(
            @ApiParam(name = "code", value = "code")
            @RequestParam(value = "code") String code) throws Exception {
        String filter = "code=" + code;
        List<TjDimensionSlave> tjDimensionSlaves = tjDimensionSlaveService.search(filter);
        if(tjDimensionSlaves == null){
            return null;
        }else{
            return  tjDimensionSlaves.get(0);
        }
    }

}
