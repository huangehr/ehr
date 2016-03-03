package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.CDAVersionClient;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/25.
 */
@RequestMapping(ApiVersion.Version1_0 + "/version")
@RestController
public class CDAVersionController extends BaseController {
    @Autowired
    private CDAVersionClient cdaVersionClient;

    @RequestMapping(value = "/cdaVersions", method = RequestMethod.GET)
    @ApiOperation(value = "标准版本列表查询")
    public Envelop searchCDAVersions(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        List<MCDAVersion> mCdaVersions = (List<MCDAVersion>) cdaVersionClient.searchCDAVersions(fields, filters, sorts, size, page);
        List<StdVersionModel> versionModelList = new ArrayList<>();
        for (MCDAVersion mCdaVersion : mCdaVersions) {
            StdVersionModel versionModel = convertToModel(mCdaVersion, StdVersionModel.class);
            //----微服务返回model已修改为String类型
            //versionModel.setCommitTime(DateUtil.formatDate(mCdaVersion.getCommitTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
            //TODO 没有字典
            versionModel.setStageName(mCdaVersion.isInStage() ? "已发布" : "未发布");
            //基础版本名字
            //versionModel.setBaseVersionName();
            versionModelList.add(versionModel);
        }
        //TODO 取得符合条件的总记录数
        int totalCount = 10;
        return getResult(versionModelList, totalCount, page, size);
    }

    @RequestMapping(value = "/cdaVersions/{version}/isLatest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否是最新的已发布的版本")
    public boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        return cdaVersionClient.isLatestVersion(version);
    }

    @RequestMapping(value = "/cdaVersion", method = RequestMethod.POST)
    @ApiOperation(value = "新增编辑状态标准版本")
    public Envelop addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) throws Exception {
        Envelop envelop = new Envelop();
        if (cdaVersionClient.existInStage()) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("已经存在处于编辑状态的标准版，不能新增！");
            return envelop;
        }
        //TODO 微服务返回boolean类型，不利于测试
        if (!cdaVersionClient.addVersion(userLoginCode)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("新增标准版本失败");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/cdaVersion/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除版本版本（编辑状态/非编辑状态）")
    public boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        //TODO 微服务没有去查询出version对应的标准版本，如下：版本对象的baseVersion为空，导致删除该版本后，该版本的子版本的baseVersion为空
        //CDAVersion cdaVersion = new CDAVersion();
        //cdaVersion.setVersion(version);

        return cdaVersionClient.dropCDAVersion(version);
    }

    @RequestMapping(value = "/cdaVersion/{version}/revert", method = RequestMethod.PUT)
    @ApiOperation(value = "删除编辑状态的版本")
    public boolean revertVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        return cdaVersionClient.revertVersion(version);
    }

    @RequestMapping(value = "/cdaVersion/{version}/commit", method = RequestMethod.PUT)
    @ApiOperation(value = "发布新版本")
    public boolean commitVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        return cdaVersionClient.commitVersion(version);
    }

    @RequestMapping(value = "/cdaVersion/{version}/rollbackToStage", method = RequestMethod.PUT)
    @ApiOperation(value = "将最新的已发布版本修改为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {

        return cdaVersionClient.rollbackToStage(version);
    }

    @RequestMapping(value = "/cdaVersion/{version}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改版本信息")
    public Envelop updateVersion(
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

        Envelop envelop = new Envelop();
        boolean flag = cdaVersionClient.updateVersion(version, versionName, userCode, inStage, baseVersion);
        if (!flag) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("标准版本更新失败！");
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/cdaVersion/checkName", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    public boolean checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName) throws Exception {

        return cdaVersionClient.checkVersionName(versionName);
    }

    @RequestMapping(value = "/cdaVersion/existInStage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public boolean existInStage() throws Exception {
        return cdaVersionClient.existInStage();
    }

    @RequestMapping(value = "/cdaVersion/{version}", method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    public Envelop getVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {
        Envelop envelop = new Envelop();
        MCDAVersion mCdaVersion = cdaVersionClient.getVersion(version);
        if (mCdaVersion == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("版本信息获取失败！");
            return envelop;
        }
        StdVersionModel versionModel = convertToModel(mCdaVersion, StdVersionModel.class);
        envelop.setSuccessFlg(true);
        envelop.setObj(versionModel);
        return envelop;
    }
}
