package com.yihu.ehr.adaption.controller;

import com.yihu.ehr.adaption.model.OrgAdapterPlan;
import com.yihu.ehr.adaption.service.AdapterInfoSendService;
import com.yihu.ehr.adaption.service.OrgAdapterPlanService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.feignclient.StdVersionClient;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.rest.ErrorCode;
import com.yihu.ehr.util.rest.RestEcho;
import com.yihu.hos.model.standard.MSTDVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
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
@Api(value = "AdapterDispatchEndPoint", description = "适配分发", tags = {"适配服务-标准化、适配方案、分发"})
public class AdapterDispatchEndPoint {

    @Autowired
    private AdapterInfoSendService adapterInfoSendService;
    @Autowired
    private OrgAdapterPlanService orgAdapterPlanService;
    @Autowired
    private StdVersionClient stdVersionClient;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

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

            String group = (String) mapResult.get(FastDFSUtil.GROUP_NAME);
            String remoteFile = (String) mapResult.get(FastDFSUtil.REMOTE_FILE_NAME);

            if (StringUtils.isEmpty(group) || StringUtils.isEmpty(remoteFile))
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 标准化数据生成失败");

            password = (String) mapResult.get("password");
            byte[] bytes = fastDFSUtil.download(group, remoteFile);
            fileBytes = new String(Base64.getEncoder().encode(bytes));
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

            String group = (String) mapResult.get(FastDFSUtil.GROUP_NAME);
            String remoteFile = (String) mapResult.get(FastDFSUtil.REMOTE_FILE_NAME);

            if (group == null || remoteFile == null)
                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 机构标准数据包生成失败");

            password = (String) mapResult.get("password");
            byte[] bytes = fastDFSUtil.download(group, remoteFile);
            fileBytes = new String(Base64.getEncoder().encode(bytes));
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
                MSTDVersion version = stdVersionClient.getVersion(listPlan.get(0).getVersion());
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



    @RequestMapping(value = "/{org_code}/source", method = RequestMethod.GET)
    @ApiOperation(value = "下载适配数据包，此包内容包含：平台标准，机构标准与二者适配", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Map getAdaptionUrl(
            @ApiParam(required = true, name = "private_key", value = "用户名")
            @RequestParam(value = "private_key", required = true) String privateKey,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgcode) throws Exception{

        Map<String, Object> mapResult;
        try {
            if ((mapResult = adapterInfoSendService.getStandardAndMappingInfo(versionCode, orgcode)) == null)
                return null;
        } catch (Exception e) {
            throw new Exception("获取远程文件路径出错！", e);
        }
        String group = (String) mapResult.get(FastDFSUtil.GROUP_NAME);
        String remoteFile = (String) mapResult.get(FastDFSUtil.REMOTE_FILE_NAME);

        if (StringUtils.isEmpty(group) || StringUtils.isEmpty(remoteFile))
            return null;

        String encryptPwd = null;
        try {
            encryptPwd = RSA.encrypt((String) mapResult.get("password"), RSA.genPrivateKey(privateKey));
        } catch (Exception e) {
            throw new Exception("加密失败！", e);
        }
        Map<String, String> rs = new HashMap<>();
        rs.put("password", encryptPwd);
        rs.put("url", fastDfsPublicServers + "/" + group + "/" + remoteFile);
        return rs;
    }
}
