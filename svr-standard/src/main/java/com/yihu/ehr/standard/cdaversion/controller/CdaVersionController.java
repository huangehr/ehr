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
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/cdaVersion")
@Api(protocols = "https", value = "cdaVersion", description = "cda版本", tags = {"cda版本"})
public class CdaVersionController extends BaseController {

    @Autowired
    private CDAVersionManager cdaVersionManager;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "cda版本分页查询")
    public Result getCDAVersions(
            @ApiParam(name = "page", value = "当前页", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "15")
            @RequestParam(value = "rows") int rows) {
        List<CDAVersionModel> models = new ArrayList<>();
        Result result = new Result();
        try {
            CDAVersion[] cdaVersions = cdaVersionManager.getVersionList();
            for (CDAVersion s : cdaVersions) {
                CDAVersionModel model = new CDAVersionModel();
                model.setAuthor(s.getAuthor());
                model.setBaseVersion(s.getBaseVersion());
                model.setCommitTime(s.getCommitTime() + "");
                model.setStage(s.isInStage());
                model.setVersion(s.getVersion());
                model.setVersionName(s.getVersionName());
                models.add(model);
            }
            result = getResult(models, models.size(), page, rows);
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "/isNew", method = RequestMethod.GET)
    @ApiOperation(value = "判断是否最新版本")
    public Result isLatestVersion(
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {
        Result result = new Result();
        try {
            CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
            String latestVersion = xcdaVersion.getVersion();
            if (latestVersion != null && latestVersion.equals(strVersion)) {
                result = getSuccessResult(true);
            } else {
                result = getSuccessResult(false);
            }
        } catch (Exception ex) {

        }
        return result;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "新增cda版本")
    public boolean addVersion(
            @ApiParam(name = "userLoginCode", value = "用户登录名")
            @RequestParam(value = "userLoginCode") String userLoginCode) {
        try {
            CDAVersion baseVersion = cdaVersionManager.getLatestVersion();
            CDAVersion xcdaVersion = cdaVersionManager.createStageVersion(baseVersion, userLoginCode);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "查询cda版本")
    public Result searchVersions(
            @ApiParam(name = "searchNm", value = "查询值", defaultValue = "")
            @RequestParam(value = "searchNm", required = false) String searchNm,
            @ApiParam(name = "page", value = "当前页", defaultValue = "0")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "rows", value = "每页行数", defaultValue = "0")
            @RequestParam(value = "rows", required = false) int rows) {

        Result result = new Result();
        List<CDAVersionModel> models = new ArrayList<>();
        ;
        Map<String, Object> args = new HashMap<>();
        args.put("version", searchNm);
        args.put("versionName", searchNm);
        args.put("page", page);
        args.put("rows", rows);
        Integer totalCount = 0;
        try {
            List<CDAVersion> xcdaVersionList = cdaVersionManager.searchVersions(args);
            for (CDAVersion s : xcdaVersionList) {
                CDAVersionModel model = new CDAVersionModel();
                model.setAuthor(s.getAuthor());
                model.setBaseVersion(s.getBaseVersion());
                model.setCommitTime(s.getCommitTime() + "");
                model.setStage(s.isInStage());
                model.setVersion(s.getVersion());
                model.setVersionName(s.getVersionName());
                models.add(model);
            }
            if (page != 0) {
                totalCount = cdaVersionManager.searchVersionInt(args);
            } else {
                page = 1;
                rows = 1;
            }
            result = getResult(models, totalCount, page, rows);
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "/drop", method = RequestMethod.DELETE)
    @ApiOperation(value = "丢弃版本")
    public boolean dropCDAVersion(
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {
        CDAVersion cdaVersion = new CDAVersion();
        cdaVersion.setVersion(strVersion);
        Result rs = new Result();
        try {
            cdaVersionManager.dropVersion(cdaVersion);
            return true;
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            return false;
        }
    }


    @RequestMapping(value = "/inStage", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除编辑状态的版本")
    public boolean revertVersion(
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {

        try {
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.revertVersion(cdaVersion);
            return true;
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            return false;
        }
    }


    @RequestMapping(value = "/newVersion", method = RequestMethod.POST)
    @ApiOperation(value = "发布新版本")
    public boolean commitVersion(
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {
        try {
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.commitVersion(cdaVersion);
            return true;
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            return false;
        }
    }


    @RequestMapping(value = "/rollbackToStage", method = RequestMethod.POST)
    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public boolean rollbackToStage(
            @ApiParam(name = "strVersion", value = "版本号", defaultValue = "")
            @RequestParam(value = "strVersion") String strVersion) {
        try {
            CDAVersion cdaVersion = cdaVersionManager.getVersion(strVersion);
            cdaVersionManager.rollbackToStage(cdaVersion);
            return true;
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
            return false;
        }
    }


    @RequestMapping(value = "/checkVersionName", method = RequestMethod.GET)
    @ApiOperation(value = "判断版本名称是否已存在")
    public Result checkVersionName(
            @ApiParam(name = "versionName", value = "版本名称", defaultValue = "")
            @RequestParam(value = "versionName") String versionName) {
        Result result = new Result();
        try {
            int num = cdaVersionManager.checkVersionName(versionName);
            if (num > 0) {
                result = getSuccessResult(true);
            } else {
                result = getSuccessResult(false);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "/existInStage", method = RequestMethod.GET)
    @ApiOperation(value = "检查是否存在处于编辑状态的版本")
    public Object existInStage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion) {
        String baseVersion = "";
        Result result = new Result();
        try {
            int num = cdaVersionManager.searchInStage();
            if (num > 0) {
                result = getSuccessResult(true);
            } else {
                CDAVersion xcdaVersion = cdaVersionManager.getLatestVersion();
                if (xcdaVersion == null) {
                    baseVersion = CDAVersionManager.FBVersion;
                } else {
                    baseVersion = xcdaVersion.getVersion();
                }
                result = getSuccessResult(false);
                result.setObj(baseVersion);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "/lastInfo", method = RequestMethod.GET)
    @ApiOperation(value = "查询最新版本的CdaVersion，用于初始化查询字典数据")
    public String getLastCdaVersion() {
        String strJson = "{}";
        try {
            CDAVersion cdaVersion = cdaVersionManager.getLatestVersion();
            if (cdaVersion != null) {
                Map map = new HashMap<>();
                map.put("version", cdaVersion.getVersion());
                map.put("author", cdaVersion.getAuthor());
                map.put("baseVersion", cdaVersion.getBaseVersion());
                map.put("commitTime", cdaVersion.getCommitTime());
                ObjectMapper objectMapper = new ObjectMapper();
                strJson = objectMapper.writeValueAsString(map);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return strJson;
    }


    @RequestMapping(value = "/combo", method = RequestMethod.GET)
    @ApiOperation(value = "查询CdaVersion用于下拉框赋值")
    public String getCdaVersionList() {
        String strJson = "[]";
        try {
            CDAVersion[] cdaVersions = cdaVersionManager.getVersionList();

            if (cdaVersions != null) {
                Map[] infos = new Map[cdaVersions.length];
                int i = 0;
                Map map;
                for (CDAVersion xcdaVersion : cdaVersions) {
                    map = new HashMap<>();
                    map.put("version", xcdaVersion.getVersion() + ',' + xcdaVersion.getVersionName());
                    map.put("author", xcdaVersion.getAuthor());
                    map.put("baseVersion", xcdaVersion.getBaseVersion());
                    map.put("commitTime", xcdaVersion.getCommitTime());
                    infos[i] = map;
                    i++;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                strJson = objectMapper.writeValueAsString(infos);
            }
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return strJson;
    }


    //    @RequestMapping("updateVersion")
//    @ApiOperation(value = "将最新的已发布版本回滚为编辑状态")
    public Object updateVersion(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "cdaVersionModelJsonData", value = "版本json模型", defaultValue = "")
            @RequestParam(value = "cdaVersionModelJsonData") String cdaVersionModelJsonData) {
        CDAVersion cdaVersion = new CDAVersion();
        String versionModel = cdaVersionModelJsonData;
        ObjectMapper objectMapper = new ObjectMapper();
        Result rs = new Result();
        try {
            CDAVersionModel model = objectMapper.readValue(versionModel, CDAVersionModel.class);
            cdaVersion.setVersion(model.getVersion());
            cdaVersion.setVersionName(model.getVersionName());
            cdaVersion.setAuthor(model.getAuthor());
            cdaVersion.setCommitTime(new Date());
            cdaVersion.setInStage(model.getStage());
            cdaVersion.setBaseVersion(model.getBaseVersion());
            cdaVersionManager.updateVersion(cdaVersion);
            rs.setSuccessFlg(true);
        } catch (Exception ex) {
            LogService.getLogger(CdaVersionController.class).error(ex.getMessage());
        }
        return rs;
    }
}