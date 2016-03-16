package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MOrgMetaData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by AndyCai on 2016/3/1.
 */
@FeignClient(MicroServices.AdaptionMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgMetaDataClient {

    @RequestMapping(value = "/adapter/org/meta/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取机构数据元")
    MOrgMetaData getOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = "/adapter/org/meta", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    MOrgMetaData createOrgMetaData(
            @ApiParam(name = "model", value = "json_data", defaultValue = "")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = "/adapter/org/meta/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    boolean deleteOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = "/adapter/org/metas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    boolean deleteOrgMetaDataList(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = "/adapter/org/meta", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元")
    MOrgMetaData updateOrgMetaData(
            @ApiParam(name = "model", value = "json_data", defaultValue = "")
            @RequestParam(value = "model") String jsonData);


    @RequestMapping(value = "/adapter/org/page", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    ResponseEntity<Collection<MOrgMetaData>> searchOrgMetaDatas(
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

    @RequestMapping(value = "/adapter/org/meta/is_exist",method = RequestMethod.GET)
    boolean isExistMetaData(
            @RequestParam(value = "data_set_id") int dataSetId,
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "meta_data_code")String metaDataCode);
}
