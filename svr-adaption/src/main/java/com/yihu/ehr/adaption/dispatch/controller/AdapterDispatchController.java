package com.yihu.ehr.adaption.dispatch.controller;

import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.adaption.dispatch.service.AdapterInfoSendService;
import com.yihu.ehr.adaption.feignclient.StdVersionClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.standard.MCDAVersion;
import com.yihu.ehr.util.RestEcho;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/1
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter_dispatcher")
@Api(protocols = "https", value = "Adapter-Dispatch", description = "适配分发接口", tags = {"标准化", "适配方案", "分发"})
public class AdapterDispatchController {
    @Autowired
    AdapterInfoSendService adapterInfoSendService;

    @Autowired
    OrgAdapterPlanService orgAdapterPlanService;
    @Autowired
    StdVersionClient stdVersionClient;
    @Autowired
    FastDFSUtil fastDFSUtil;


    @RequestMapping(value = "/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "下载适配数据包，此包内容包含：平台标准，机构标准与二者适配", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object downAdaptions(
            @ApiParam(required = true, name = "private_key", value = "用户名")
            @RequestParam(value = "private_key", required = true) String privateKey,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgcode) {

        Map<String, Object> mapResult = null;
        String password = null;
        String fileBytes = null;
        try {
            if ((mapResult = adapterInfoSendService.getStandardAndMappingInfo(versionCode, orgcode)) == null)
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 标准化数据下载失败!");

            String group = (String) mapResult.get(FastDFSUtil.GroupField);
            String remoteFile = (String) mapResult.get(FastDFSUtil.RemoteFileField);

            if (StringUtils.isEmpty(group) || StringUtils.isEmpty(remoteFile))
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 标准化数据生成失败");

            password = (String) mapResult.get("password");
            byte[] bytes = fastDFSUtil.download(group, remoteFile);
            fileBytes = Base64.encode(bytes);
        } catch (Exception e) {
            return new RestEcho().failed(ErrorCode.DownArchiveFileFailed, "下载标准适配版本失败");
        }

        try {
            //密码RSA加密
            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(privateKey));

            RestEcho restEcho =
                    new RestEcho()
                    .success()
                    .putResult("cryptograph", encryptPwd)
                    .putResult("zipfile", fileBytes);
            return restEcho;
        } catch (IOException ex) {
            return new RestEcho().failed(ErrorCode.GenerateArchiveFileStreamFailed, "生成适配版本文件流失败");
        } catch (Exception ex) {
            return new RestEcho().failed(ErrorCode.GenerateFileCryptographFailed, "生成适配版本文件密码失败");
        }
    }


    @RequestMapping(value = "/org_standard_data/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取机构标准数据包", response = RestEcho.class, produces = "application/json", notes = "获取机构标准数据包，文件以Base64编码，压缩格式为zip")
    public Object getSchemeMappingInfo(
            @ApiParam(required = true, name = "private_key", value = "用户私钥")
            @RequestParam(value = "private_key", required = true) String privateKey,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgcode) throws Exception {

        Map<String, Object> mapResult = null;
        String password = null;
        String fileBytes = null;

        try {
            if ((mapResult=adapterInfoSendService.getAdapterFileInfo(versionCode, orgcode)) == null)
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 机构标准数据包生成失败");

            String group = (String) mapResult.get(FastDFSUtil.GroupField);
            String remoteFile = (String) mapResult.get(FastDFSUtil.RemoteFileField);

            if (group == null || remoteFile == null)
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 机构标准数据包生成失败");

            password = (String) mapResult.get("password");
            byte[] bytes = fastDFSUtil.download(group, remoteFile);
            fileBytes = Base64.encode(bytes);
        } catch (Exception e) {
            return new RestEcho().failed(ErrorCode.DownArchiveFileFailed, "下载机构标准数据包失败");
        }

        try {
            //密码RSA加密
            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(privateKey));

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("cryptograph", encryptPwd);
            restEcho.putResult("zipfile", fileBytes);
            return restEcho;
        } catch (IOException ex) {
            return new RestEcho().failed(ErrorCode.GenerateArchiveFileStreamFailed, "生成文件流失败");
        } catch (Exception ex) {
            return new RestEcho().failed(ErrorCode.GenerateFileCryptographFailed, "生成文件密码失败");
        }
    }


    @RequestMapping(value = "/org_plan/version", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ", response = RestEcho.class, produces = "application/json", notes = "指定版本的信息")
    public Object getCDAVersionInfoByOrgCode(
             @ApiParam(name = "org_code", value = "机构编码")
             @RequestParam(value = "org_code") String orgCode) throws Exception {
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("orgcode", orgCode);

            List<OrgAdapterPlan> listPlan = orgAdapterPlanService.getOrgAdapterPlanByOrgCode(args);

            if (listPlan != null && listPlan.size() > 0) {
                RestEcho restEcho = new RestEcho().success();
                MCDAVersion version = stdVersionClient.getVersion(listPlan.get(0).getVersion());
                restEcho.putResult("version", version.getVersion());
                restEcho.putResult("timestamp", version.getCommitTime());
                return restEcho;
            } else {
                return new RestEcho().failed(ErrorCode.GetCDAVersionFailed, "机构无适配数据");
            }
        } catch (Exception ex) {
            return new RestEcho().failed(ErrorCode.GetCDAVersionFailed, "标准版本获取失败：" + ex.getMessage());
        }
    }
}
