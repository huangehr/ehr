package com.yihu.ehr.standard.version.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dispatch.service.DispatchService;
import com.yihu.ehr.standard.version.service.CDAVersion;
import com.yihu.ehr.standard.version.service.CDAVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "version", description = "标准版本", tags = {"标准版本"})
public class VersionController extends ExtendController<MCDAVersion> {

    @Autowired
    private CDAVersionService versionService;

    @Autowired
    private DispatchService dispatchService;

    protected ApiException errNotFound(String version) {
        return errNotFound("标准版本", version);
    }

    private CDAVersion findVersion(String version) {
        CDAVersion cdaVersion = versionService.getVersion(version);
        if (cdaVersion == null)
            throw errNotFound(version);
        return cdaVersion;
    }

    private CDAVersion findVersionInstage(String version) {
        CDAVersion cdaVersion = findVersion(version);
        if (!cdaVersion.isInStage()) {
            throw new ApiException(ErrorCode.InValidCDAVersionStage);
        }
        return cdaVersion;
    }

    @RequestMapping(value = RestApi.Standards.Versions, method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Collection<MCDAVersion> searchCDAVersions(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "version,versionName,commitTime,baseVersion,inStage")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+version")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List appList = versionService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, versionService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MCDAVersion.class, fields);
    }


    @RequestMapping(value = RestApi.Standards.VersionNewest, method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    public boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = versionService.getLatestVersion();
        String latestVersion = cdaVersion.getVersion();
        return latestVersion != null && latestVersion.equals(version);
    }

    @RequestMapping(value = RestApi.Standards.Versions, method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    public MCDAVersion addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) throws Exception {

        CDAVersion baseVersion = versionService.getLatestVersion();
        CDAVersion xcdaVersion = versionService.createStageVersion(baseVersion, userLoginCode);
        if (xcdaVersion != null)
            return getModel(xcdaVersion);
        return null;
    }


    @RequestMapping(value = RestApi.Standards.Version, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除版本")
    public boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        versionService.dropVersion(findVersion(version));
        return true;
    }


    @RequestMapping(value = RestApi.Standards.VersionRevert, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除编辑状态的版本")
    public boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        versionService.revertVersion(findVersionInstage(version));
        return true;
    }


    @RequestMapping(value = RestApi.Standards.VersionCommit, method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    public boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = findVersionInstage(version);
        Map<String, Object> mapResult = dispatchService.createFullVersionFile(cdaVersion.getVersion());
        if (mapResult == null) return false;

        versionService.commitVersion(cdaVersion);
        return true;
    }


    @RequestMapping(value = RestApi.Standards.VersionBackStage, method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = versionService.getVersion(version);
        versionService.rollbackToStage(cdaVersion);

        return true;
    }


    @RequestMapping(value = RestApi.Standards.Version, method = RequestMethod.PUT)
    @ApiOperation(value = "修改版本信息")
    public MCDAVersion updateVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version,
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName,
            @ApiParam(name = "userCode", value = "操作者", defaultValue = "")
            @RequestParam(value = "userCode") String userCode,
            @ApiParam(name = "inStage", value = "编辑状态", defaultValue = "")
            @RequestParam(value = "inStage") int inStage,
            @ApiParam(name = "baseVersion", value = "父版本", defaultValue = "")
            @RequestParam(value = "baseVersion") String baseVersion) throws Exception {

        CDAVersion cdaVersion = findVersion(version);
        cdaVersion.setVersionName(versionName);
        cdaVersion.setAuthor(userCode);
        cdaVersion.setCommitTime(new Date());
        cdaVersion.setInStage(inStage == 1);
        cdaVersion.setBaseVersion(baseVersion);
        versionService.save(cdaVersion);
        return getModel(cdaVersion);
    }


    @RequestMapping(value = RestApi.Standards.VersionNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    public boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName) throws Exception {

        int num = versionService.checkVersionName(versionName);
        return num > 0;
    }


    @RequestMapping(value = RestApi.Standards.VersionInStageExist, method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public boolean existInStage() throws Exception {
        int num = versionService.searchInStage();
        return num > 0;
    }

    @RequestMapping(value = RestApi.Standards.Version, method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    public MCDAVersion getVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = versionService.retrieve(version);
        return getModel(cdaVersion);
    }


    @RequestMapping(value = RestApi.Standards.VersionCache, method = RequestMethod.PUT)
    @ApiOperation(value = "向Redis中缓存标准数据")
    public void cache(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) {

    }
}