package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by AndyCai on 2016/2/29.
 */
@FeignClient(MicroServices.AdaptionMgr)
@RequestMapping(ApiVersion.Version1_0+"/adapter")
@ApiIgnore
public interface AdapterOrgClient {
    @RequestMapping(value = "/orgs", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    Collection<MAdapterOrg> searchAdapterOrg(
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


    @RequestMapping(value = "/org/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配采集标准")
    MAdapterOrg getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) ;


    @RequestMapping(value = "/org", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    MAdapterOrg addAdapterOrg(
            @ApiParam(name = "adapterOrg", value = "采集机构模型", defaultValue = "")
            @RequestParam(value = "adapterOrg", required = false) String adapterOrg) ;


    @RequestMapping(value = "/org/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    MAdapterOrg updateAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) ;


    @RequestMapping(value = "/orgs", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    boolean delAdapterOrg(
            @ApiParam(name = "codes", value = "代码", defaultValue = "")
            @PathVariable(value = "codes") String codes);


    @RequestMapping(value = "/isExistAdapterData/{org}", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    boolean orgIsExistData(
            @ApiParam(name = "org", value = "机构", defaultValue = "")
            @PathVariable(value = "org") String org) ;
}
