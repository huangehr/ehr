package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MCDAVersion;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient("svr-standard")
@RequestMapping(ApiVersion.Version1_0 + "/std")
@ApiIgnore
public interface CDAVersionClient {

    @RequestMapping(value = "/cdaVersions", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    Collection<MCDAVersion> searchCDAVersions(
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


    @RequestMapping(value = "/cdaVersions/{version}/isLatest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/cdaVersion", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    boolean addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode);


    @RequestMapping(value = "/cdaVersion/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "丢弃版本")
    boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/cdaVersion/{version}/revert", method = RequestMethod.PUT)
    @ApiOperation(value = "删除编辑状态的版本")
    boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);


    @RequestMapping(value = "/cdaVersion/{version}/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/cdaVersion/{version}/rollbackToStage", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = "/cdaVersion/{strVersion}", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean updateVersion(
            @ApiParam(name = "vesion", value = "版本号", defaultValue = "")
            @RequestParam(value = "vesion") String vesion,
            @ApiParam(name = "vesionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "vesionName") String vesionName);


    @RequestMapping(value = "/cdaVersion/checkName", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName);


    @RequestMapping(value = "/cdaVersion/existInStage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    boolean existInStage();

}
