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

    @RequestMapping(value = "/versions", method = RequestMethod.GET)
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


    @RequestMapping(value = "/version/{version}/is_latest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是最新已发布版本")
    boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/version", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    MCDAVersion addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode);


    @RequestMapping(value = "/version/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除版本版本（编辑状态/非编辑状态）")
    boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/version/{version}/revert", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除编辑状态的版本")
    boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);


    @RequestMapping(value = "/version/{version}/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

    @RequestMapping(value = "/version/{version}/rollback_stage", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本修改为编辑状态")
    boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = "/version/{version}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改版本信息")
    MCDAVersion updateVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName,
            @ApiParam(name = "userCode", value = "操作者", defaultValue = "")
            @RequestParam(value = "userCode") String userCode,
            @ApiParam(name = "inStage", value = "编辑状态", defaultValue = "")
            @RequestParam(value = "inStage") int inStage,
            @ApiParam(name = "baseVersion", value = "父版本", defaultValue = "")
            @RequestParam(value = "baseVersion") String baseVersion);


    @RequestMapping(value = "/version/check_name", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName);


    @RequestMapping(value = "/version/exist_instage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    boolean existInStage();

    @RequestMapping(value = "/version/{version}", method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    MCDAVersion getVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version);

}
