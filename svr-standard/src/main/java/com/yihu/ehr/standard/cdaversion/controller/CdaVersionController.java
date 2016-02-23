package com.yihu.ehr.standard.cdaversion.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionService;
import com.yihu.ehr.standard.commons.ExtendController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/std")
@Api(protocols = "https", value = "cdaVersion", description = "cda版本", tags = {"cda版本"})
public class CdaVersionController extends ExtendController<MCDAVersion>{

   @Autowired
    private CDAVersionService cdaVersionService;

    @RequestMapping(value = "/cdaVersions", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Collection searchCDAVersions(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        List appList = cdaVersionService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, cdaVersionService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<MCDAVersion>(appList.size()), MCDAVersion.class, fields.split(","));
    }


    @RequestMapping(value="/cdaVersions/{version}/isLatest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    public boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception{

        CDAVersion xcdaVersion = cdaVersionService.getLatestVersion();
        String latestVersion = xcdaVersion.getVersion();
        return latestVersion!=null&&latestVersion.equals(version);
    }

    @RequestMapping(value = "/cdaVersion", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    public boolean addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) throws Exception{

        CDAVersion baseVersion = cdaVersionService.getLatestVersion();
        CDAVersion xcdaVersion = cdaVersionService.createStageVersion(baseVersion, userLoginCode);
        return xcdaVersion!=null;
    }


    @RequestMapping(value = "/cdaVersion/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "丢弃版本")
    public boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception{

        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(version);
        cdaVersionService.dropVersion(cdaVersion);
        return true;
    }

    @RequestMapping(value = "/cdaVersion/{version}/revert", method = RequestMethod.PUT)
    @ApiOperation(value = "删除编辑状态的版本")
    public boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception{

        CDAVersion cdaVersion = cdaVersionService.getVersion(version);
        cdaVersionService.revertVersion(cdaVersion);
        return true;
    }

    @RequestMapping(value = "/cdaVersion/{version}/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    public boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception{

        CDAVersion cdaVersion = cdaVersionService.getVersion(version);
        cdaVersionService.commitVersion(cdaVersion);
        return true;
    }

    @RequestMapping(value = "/cdaVersion/{version}/rollbackToStage", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        CDAVersion cdaVersion = cdaVersionService.getVersion(version);
        cdaVersionService.rollbackToStage(cdaVersion);
        return true;
    }


    @RequestMapping(value = "/cdaVersion/{strVersion}", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean updateVersion(
            @ApiParam(name = "vesion", value = "版本号", defaultValue = "")
            @RequestParam(value = "vesion") String vesion,
            @ApiParam(name = "vesionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "vesionName") String vesionName) throws Exception{

        CDAVersion cdaVersion = cdaVersionService.retrieve(vesion);
        if(cdaVersion==null)
            throw errNotFound();
        cdaVersion.setVersionName(vesionName);
        cdaVersionService.save(cdaVersion);
        return true;
    }

    @RequestMapping(value = "/cdaVersion/checkName", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    public boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName ) throws Exception{

        int num = cdaVersionService.checkVersionName(versionName);
        return num>0;
    }

    @RequestMapping(value = "/cdaVersion/existInStage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public boolean existInStage() throws Exception{
        int num = cdaVersionService.searchInStage();
        return num>0;
    }
}