package com.yihu.ehr.tj.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.tj.TjDataSource;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.tj.service.TjDataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjDataSource", description = "数据源", tags = {"统计指标管理-数据源"})
public class TjDataSourceEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TjDataSourceService tjDataSourceService;

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询数据源")
    public ListResult getTjDataSourceList(
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
        List<TjDataSource> tjDataSourceList = tjDataSourceService.search(fields, filters, sorts, page, size);
        if(tjDataSourceList != null){
            listResult.setDetailModelList(tjDataSourceList);
            listResult.setTotalCount((int)tjDataSourceService.getCount(filters));
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

    @RequestMapping(value = ServiceApi.TJ.AddTjDataSource, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改数据源")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        TjDataSource obj = objectMapper.readValue(model,TjDataSource.class);
        if(obj.getId() != 0){
            obj.setUpdateTime(new Date());
        }else{
            obj.setCreateTime(new Date());
        }
        obj = tjDataSourceService.save(obj);
        return Result.success("数据源更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.TJ.DeleteTjDataSource, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据源")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{
//        tjDataSourceService.delete(id);
        TjDataSource tjDataSource = tjDataSourceService.getById(id);
        tjDataSource.setStatus(-1);
        tjDataSourceService.save(tjDataSource);
        return Result.success("数据源删除成功！");
    }

    @RequestMapping(value = ServiceApi.TJ.GetTjDataSourceById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取数据源")
    public TjDataSource getById(
            @ApiParam(name = "id")
            @PathVariable(value = "id") Long id) {
        TjDataSource tjDataSource = tjDataSourceService.getById(id);
        return tjDataSource;
    }

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    public boolean hasExistsName(
            @ApiParam(name = "name")
            @PathVariable("name") String name) throws Exception {
        String filter = "name=" + name;
        List<TjDataSource> list = tjDataSourceService.search(filter);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = ServiceApi.TJ.TjDataSourceExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    public boolean hasExistsCode(
            @ApiParam(name = "code")
            @PathVariable("code") String code) throws Exception {
        String filter = "code=" + code;
        List<TjDataSource> list = tjDataSourceService.search(filter);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }
}
