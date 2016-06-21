package com.yihu.ehr.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by AndyCai on 2016/2/29.
 */
@FeignClient(name=MicroServices.Adaption)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgDataSetClient {

    @RequestMapping(value = "/adapter/org/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    MOrgDataSet getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = "/adapter/org/data_set", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建机构数据集")
    MOrgDataSet createOrgDataSet(
            @ApiParam(name = "model", value = "json_data", defaultValue = "")
            @RequestBody String jsonData);


    @RequestMapping(value = "/adapter/org/data_set/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = "/adapter/org/data_set", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改机构数据集")
    MOrgDataSet updateOrgDataSet(
            @ApiParam(name = "model", value = "json_data", defaultValue = "")
            @RequestBody String jsonData);


    @RequestMapping(value = "/adapter/org/data_sets", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    ResponseEntity<Collection<MOrgDataSet>> searchAdapterOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/adapter/org/is_exist", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    boolean dataSetIsExist(
            @ApiParam(name = "org_code",value = "机构代码",defaultValue = "")
            @RequestParam(value = "org_code")String orgCode,
            @ApiParam(name="code",value="数据集代码",defaultValue = "")
            @RequestParam(value = "code")String code);

    @RequestMapping(value = "/adapter/org/data_set",method = RequestMethod.GET)
    MOrgDataSet getDataSetBySequence(
            @RequestParam(value="org_code")String orgCode,
            @RequestParam(value = "sequence")long sequence);
}
