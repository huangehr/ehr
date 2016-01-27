package com.yihu.ehr.standard.cdaversion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.log.LogService;
import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionModel;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by lincl on 2015/12/30.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cdaVersion")
@Api(protocols = "https", value = "cdaVersion", description = "cda版本", tags = {"cda版本"})
public class CdaVersionController extends BaseController{

   @Autowired
    private CDAVersionManager cdaVersionManager;

    @RequestMapping("getVersionList")
    @ApiOperation(value = "cda版本分页查询")
    public Object getCDAVersions(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows){
        List<CDAVersionModel> models = new ArrayList<>();
        Result result = new Result();
        try{
            CDAVersion[] cdaVersions = cdaVersionManager.getVersionList();
            for(CDAVersion s:cdaVersions){
                CDAVersionModel model = new CDAVersionModel();
                model.setAuthor(s.getAuthor());
                model.setBaseVersion(s.getBaseVersion());
                model.setCommitTime(s.getCommitTime()+"");
                //model.setStage(s.isInStage()==true?1:0);
                model.setStage(s.isInStage());
                model.setVersion(s.getVersion());
                model.setVersionName(s.getVersionName());
                models.add(model);
            }
            result = getResult(models,models.size(),page,rows);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping("isLatestVersion")
    @ApiOperation(value = "判断是否最新版本")
    public Object isLatestVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion){
        Result result = new Result();
        try{
            CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
            String latestVersion = xcdaVersion.getVersion();
            if(latestVersion!=null&&latestVersion.equals(strVersion)){
                result = getSuccessResult(true);
            }else{
                result = getSuccessResult(false);
            }
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("addVersion")
    @ApiOperation(value = "新增cda版本")
    public Object addVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode){
        Result result = new Result();
        try{
            CDAVersion baseVersion = cdaVersionManager.getLatestVersion();
            CDAVersion xcdaVersion = cdaVersionManager.createStageVersion(baseVersion, userLoginCode);
            if (xcdaVersion!=null){
                result = getSuccessResult(true);
            }else{
                result = getSuccessResult(false);
            }
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("searchVersions")
    @ApiOperation(value = "查询cda版本")
    public Object searchVersions(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "searchNm", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNm") String searchNm,
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows){
        Result result = new Result();
        List<CDAVersionModel> models = new ArrayList<>();;
        Map<String, Object> args = new HashMap<>();
        args.put("version",searchNm);
        args.put("versionName",searchNm);
        args.put("page",page);
        args.put("rows",rows);
        Integer totalCount=0;
        try{
            List<CDAVersion> xcdaVersionList = cdaVersionManager.searchVersions(args);
            for(CDAVersion s:xcdaVersionList){
                CDAVersionModel model = new CDAVersionModel();
                model.setAuthor(s.getAuthor());
                model.setBaseVersion(s.getBaseVersion());
                model.setCommitTime(s.getCommitTime()+"");
                model.setStage(s.isInStage());
                model.setVersion(s.getVersion());
                model.setVersionName(s.getVersionName());
                models.add(model);
            }
            if(page!=0) {
                totalCount = cdaVersionManager.searchVersionInt(args);
            }
            else {
                page=1;
                rows=1;
            }
            result = getResult(models,totalCount,page,rows);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("dropCDAVersion")
    @ApiOperation(value = "丢弃版本")
    public Object dropCDAVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion){
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(strVersion);
        Result rs = new Result();
        try {
            cdaVersionManager.dropVersion(cdaVersion);
            rs.setSuccessFlg(true);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            rs.setSuccessFlg(false);
        }
        return rs;
    }


    @RequestMapping("revertVersion")
    @ApiOperation(value = "删除编辑状态的版本")
    public Object revertVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion){
        Result rs = new Result();
        try{
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.revertVersion(cdaVersion);
            rs.setSuccessFlg(true);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            rs.setSuccessFlg(false);
        }
        return rs;
    }


    @RequestMapping("commitVersion")
    @ApiOperation(value = "发布新版本")
    public Object commitVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion){
        Result rs = new Result();
        try{
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.commitVersion(cdaVersion);
            rs.setSuccessFlg(true);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            rs.setSuccessFlg(false);
        }
        return rs;
    }


    @RequestMapping("rollbackToStage")
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public Object rollbackToStage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion){
        Result rs = new Result();
        try{
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.rollbackToStage(cdaVersion);
            rs.setSuccessFlg(true);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            rs.setSuccessFlg(false);
        }
        return rs;
    }

    //todo：待测试
    @RequestMapping("updateVersion")
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public Object updateVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersionModelJsonData", value = "版本json模型", defaultValue = "")
            @RequestParam(value = "cdaVersionModelJsonData") String cdaVersionModelJsonData ){
        CDAVersion cdaVersion = new CDAVersion();
        String versionModel = cdaVersionModelJsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        Result rs = new Result();
        try{
            CDAVersionModel model = objectMapper.readValue(versionModel, CDAVersionModel.class);
            cdaVersion.setVersion(model.getVersion());
            cdaVersion.setVersionName(model.getVersionName());
            cdaVersion.setAuthor(model.getAuthor());
            cdaVersion.setCommitTime(new Date());
            cdaVersion.setInStage(model.getStage());
            cdaVersion.setBaseVersion(model.getBaseVersion());
            cdaVersionManager.updateVersion(cdaVersion);
            rs.setSuccessFlg(true);
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return rs;
    }


    @RequestMapping("checkVersionName")
    @ApiOperation(value = "判断版本名称是否已存在")
    public Object checkVersionName(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName ){
        Result result = new Result();
        try{
            int num = cdaVersionManager.checkVersionName(versionName);
            if (num>0){
                result = getSuccessResult(true);
            }else{
                result = getSuccessResult(false);
            }
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping("existInStage")
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public Object existInStage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion){
        String baseVersion = "";
        Result result = new Result();
        try{
            int num = cdaVersionManager.searchInStage();
            if (num>0){
                result = getSuccessResult(true);
            }else{
                CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
                if (xcdaVersion==null){
                    baseVersion = CDAVersionManager.FBVersion;
                }else{
                    baseVersion = xcdaVersion.getVersion();
                }
                result = getSuccessResult(false);
                result.setObj(baseVersion);
            }
        }catch (Exception ex){
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }
}