package com.yihu.ehr.standard.cdaversion.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionService;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dispatch.service.DispatchService;
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
@RequestMapping(ApiVersion.Version1_0 + "/std")
@Api(protocols = "https", value = "std_version", description = "标准版本", tags = {"标准版本"})
public class CdaVersionController extends ExtendController<MCDAVersion> {

    @Autowired
    private CDAVersionService cdaVersionService;
    @Autowired
    DispatchService dispatchService;

    protected ApiException errNotFound(String version) {
        return errNotFound("标准版本", version);
    }

    private CDAVersion findVersion(String version) {
        CDAVersion cdaVersion = cdaVersionService.getVersion(version);
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

    @RequestMapping(value = "/versions", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Collection<MCDAVersion> searchCDAVersions(
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
            HttpServletResponse response) throws Exception {

        List appList = cdaVersionService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, cdaVersionService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MCDAVersion.class, fields);
    }


    @RequestMapping(value = "/version/{version}/is_latest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    public boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion xcdaVersion = cdaVersionService.getLatestVersion();
        String latestVersion = xcdaVersion.getVersion();
        return latestVersion != null && latestVersion.equals(version);
    }

    @RequestMapping(value = "/version", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    public MCDAVersion addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) throws Exception {

        CDAVersion baseVersion = cdaVersionService.getLatestVersion();
        CDAVersion xcdaVersion = cdaVersionService.createStageVersion(baseVersion, userLoginCode);
        if (xcdaVersion != null)
            return getModel(xcdaVersion);
        return null;
    }


    @RequestMapping(value = "/version/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除版本")
    public boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        cdaVersionService.dropVersion(findVersion(version));
        return true;
    }


    @RequestMapping(value = "/version/{version}/revert", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除编辑状态的版本")
    public boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        cdaVersionService.revertVersion(findVersionInstage(version));
        return true;
    }


    @RequestMapping(value = "/version/{version}/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    public boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = findVersionInstage(version);

        Map<String, Object> mapResult = dispatchService.createFullVersionFile(cdaVersion.getVersion());
        if (mapResult == null)
            return false;

        cdaVersionService.commitVersion(cdaVersion);
        return true;
    }

    @RequestMapping(value = "/version/{version}/rollback_stage", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = cdaVersionService.getVersion(version);
        cdaVersionService.rollbackToStage(cdaVersion);
        return true;
    }


    @RequestMapping(value = "/version/{version}", method = RequestMethod.PUT)
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
        cdaVersionService.save(cdaVersion);
        return getModel(cdaVersion);
    }

    @RequestMapping(value = "/version/check_name", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    public boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName) throws Exception {

        int num = cdaVersionService.checkVersionName(versionName);
        return num > 0;
    }

    @RequestMapping(value = "/version/exist_instage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public boolean existInStage() throws Exception {
        int num = cdaVersionService.searchInStage();
        return num > 0;
    }


    @RequestMapping(value = "/version/{version}", method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    public MCDAVersion getVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        CDAVersion cdaVersion = cdaVersionService.retrieve(version);
        return getModel(cdaVersion);
    }
}