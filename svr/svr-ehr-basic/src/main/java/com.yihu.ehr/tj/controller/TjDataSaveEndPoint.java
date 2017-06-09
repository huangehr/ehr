package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.tj.TjDataSave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDataSaveService;
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
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjDataSave", description = "数据存储", tags = {"统计指标管理-数据存储"})
public class TjDataSaveEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TjDataSaveService tjDataSaveService;

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSaveList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询数据存储")
    public ListResult getTjDataSaveList(
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
        List<TjDataSave> tjDataSaveList = tjDataSaveService.search(fields, filters, sorts, page, size);
        if(tjDataSaveList != null){
            listResult.setDetailModelList(tjDataSaveList);
            listResult.setTotalCount((int)tjDataSaveService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.TJ.AddTjDataSave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改数据存储")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjDataSave obj = objectMapper.readValue(model, TjDataSave.class);
        obj = tjDataSaveService.save(obj);
        return Result.success("数据存储更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.TJ.DeleteTjDataSave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据存储")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{
        tjDataSaveService.delete(id);
        return Result.success("数据存储删除成功！");
    }
}
