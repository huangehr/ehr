package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.agModel.standard.standardversion.StdVersionModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.std.service.CDAVersionClient;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
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
    @ApiOperation(value = "适配采集标准")
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
        List<MCDAVersion> mcdaVersions = (List<MCDAVersion>) cdaVersionClient.searchCDAVersions(fields, filters, sorts, size, page);
        List<StdVersionModel> versionModelList = new ArrayList<>();
        for (MCDAVersion mcdaVersion : mcdaVersions) {
            StdVersionModel versionModel = new StdVersionModel();
            versionModel.setAuthor(mcdaVersion.getAuthor());
            versionModel.setCommitTime(DateUtil.formatDate(mcdaVersion.getCommitTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
            //TODO 没有字典
            boolean isInstage = mcdaVersion.isInStage();
            versionModel.setIsInStage(isInstage);
            versionModel.setStageName(isInstage ? "已发布" : "未发布");
            versionModel.setVersion(mcdaVersion.getVersion());
            versionModel.setVersionName(mcdaVersion.getVersionName());
            versionModel.setBaseVersion(mcdaVersion.getBaseVersion());
            //versionModel.setBaseVersionName();
            versionModelList.add(versionModel);
        }
        //TODO 取得符合条件的总记录数
        int totalCount = 10;
        return getResult(versionModelList, totalCount, page, size);
    }

    @RequestMapping(value = "/cdaVersions/{version}/isLatest", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    public boolean isLatestVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

        return cdaVersionClient.isLatestVersion(version);
    }

    @RequestMapping(value = "/cdaVersion", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    public Envelop addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) throws Exception {
        Envelop envelop = new Envelop();
        if (!cdaVersionClient.addVersion(userLoginCode)) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("新增cda版本失败");
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = "/cdaVersion/{version}/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "丢弃版本")
    public boolean dropCDAVersion(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) throws Exception {

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
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {

        return cdaVersionClient.rollbackToStage(version);
    }

    @RequestMapping(value = "/cdaVersion/{strVersion}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改版本名称")
    public boolean updateVersion(
            @ApiParam(name = "vesion", value = "版本号", defaultValue = "")
            @RequestParam(value = "vesion") String version,
            @ApiParam(name = "vesionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "vesionName") String versionName) throws Exception {

        return cdaVersionClient.updateVersion(version, versionName);
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
//    @RequestMapping(value = "isLatestVersion", method = RequestMethod.GET)
//    public String isLatestVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                  @PathVariable(value = "apiVersion") String apiVersion,
//                                  @ApiParam(name = "versionCode", value = "版本代码")
//                                  @RequestParam(value = "versionCode") String versionCode) {
//        return null;
//    }
//
//    @RequestMapping(value = "/latestVersion",method = RequestMethod.GET)
//    public Object getLatestVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                   @PathVariable(value = "apiVersion") String apiVersion) {
//        return null;
//    }
//
//    @RequestMapping(value = "/addVersion",method = RequestMethod.POST)
//    public String addVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                             @PathVariable(value = "apiVersion") String apiVersion,
//                             @ApiParam(name = "latestVersion", value = "父级版本号")
//                             @RequestParam(value = "latestVersion") String latestVersion,
//                             @ApiParam(name = "versionName", value = "版本名称")
//                             @RequestParam(value = "versionName") String versionName,
//                             @ApiParam(name = "userId", value = "用户ID")
//                             @RequestParam(value = "userId") String userId) {
//        return null;
//    }
//
//    @RequestMapping(value = "/versions",method = RequestMethod.GET)
//    public String getVersionsByCodeOrName(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                          @PathVariable(value = "apiVersion") String apiVersion,
//                                          @ApiParam(name = "code", value = "版本代码")
//                                          @RequestParam(value = "code") String code,
//                                          @ApiParam(name = "name", value = "版本名称")
//                                          @RequestParam(value = "name") String name, @ApiParam(name = "page", value = "当前页", defaultValue = "1")
//                                          @RequestParam(value = "page") int page,
//                                          @ApiParam(name = "rows", value = "每页行数", defaultValue = "20")
//                                          @RequestParam(value = "rows") int rows) {
//        return null;
//    }
//
//    @RequestMapping(value = "/dropCDAVersion", method = RequestMethod.POST)
//    public String dropCDAVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                 @PathVariable(value = "apiVersion") String apiVersion,
//                                 @ApiParam(name = "versionCode", value = "版本代码")
//                                 @RequestParam(value = "versionCode") String versionCode) {
//        return null;
//    }
//
//    //发布新版本
//    @RequestMapping(value = "/commitVersion",method = RequestMethod.POST)
//    public String commitVersion(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                @PathVariable(value = "apiVersion") String apiVersion,
//                                @ApiParam(name = "versionCode", value = "版本代码")
//                                @RequestParam(value = "versionCode") String versionCode) {
//        return null;
//    }
//
//    @RequestMapping(value = "/existInStage",method = RequestMethod.GET)
//    public String existInStage(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                   @PathVariable(value = "apiVersion") String apiVersion){
//        return null;
//    }
//
//    @RequestMapping(value = "/allVersions",method = RequestMethod.GET)
//    public Object getAllVersions(@ApiParam(name = "apiVersion", value = "API版本号", defaultValue = "v1.0")
//                                 @PathVariable(value = "apiVersion") String apiVersion)
//    {
//        return null;
//    }

}
