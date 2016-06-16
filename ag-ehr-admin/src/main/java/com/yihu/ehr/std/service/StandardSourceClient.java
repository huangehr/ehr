package com.yihu.ehr.std.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdSource;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by yww on 2016/3/1.
 */
@FeignClient(name=MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface StandardSourceClient {

    @RequestMapping(value = ServiceApi.Standards.Sources, method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索")
    ResponseEntity<Collection<MStdSource>> searchSources(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Standards.Source, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取标准来源信息")
    MStdSource getStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id);


    @RequestMapping(value = ServiceApi.Standards.Source, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改标准来源，通过id取数据，取不到数据时新增，否则修改")
    MStdSource updateStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);


    @RequestMapping(value = ServiceApi.Standards.Sources, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增标准来源")
    MStdSource addStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);


    @RequestMapping(value = ServiceApi.Standards.Sources, method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    boolean delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Standards.Source, method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Standards.IsSourceCodeExist,method = RequestMethod.GET)
    boolean isCodeExist(@RequestParam(value="code")String code);

    @RequestMapping(value = ServiceApi.Standards.NoPageSources, method = RequestMethod.GET)
    ResponseEntity<Collection<MStdSource>> search(
            @RequestParam(value = "filters", required = false) String filters);
}
