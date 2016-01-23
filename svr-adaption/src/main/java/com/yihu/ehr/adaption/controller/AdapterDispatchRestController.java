package com.yihu.ehr.adaption.controller;

import com.yihu.ha.adapter.model.XAdapterInfoSendManager;
import com.yihu.ha.adapter.model.XOrgAdapterPlan;
import com.yihu.ha.adapter.model.XOrgAdapterPlanManager;
import com.yihu.ha.constrant.ApiVersionPrefix;
import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.security.model.XSecurityManager;
import com.yihu.ha.security.model.XUserSecurity;
import com.yihu.ha.util.RestEcho;
import com.yihu.ha.util.controller.BaseRestController;
import com.yihu.ha.util.encode.Base64;
import com.yihu.ha.util.encrypt.RSA;
import com.yihu.ha.util.fastdfs.FastDFSUtil;
import com.yihu.ha.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2015/12/22.
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/adapter-dispatcher")
@Api(protocols = "https", value = "Adapter-Dispatch", description = "适配分发接口", tags = {"标准化", "适配方案", "分发"})
public class AdapterDispatchRestController extends BaseRestController {

    @Resource(name = Services.SecurityManager)
    private XSecurityManager securityManager;

    @Resource(name = Services.AdapterInfoSendManager)
    private XAdapterInfoSendManager xAdapterInfoSendManager;

    @Resource(name = Services.OrgAdapterPlanManager)
    private XOrgAdapterPlanManager xOrgAdapterPlanManager;

    @RequestMapping(value = "/schemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getSchemeMappingInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) {
        Map<String, Object> mapResult = null;
        XUserSecurity userSecurity = null;
        String password = null;
        String fileBytes = null;
        try {
            userSecurity = securityManager.getUserSecurityByUserName(userName);
        } catch (Exception e) {
            LogService.getLogger(AdapterDispatchRestController.class).error(e.getMessage());
            return failed(ErrorCode.GenerateUserKeyFailed);
        }
        try {
            if (userSecurity == null) return failed(ErrorCode.GetUserKeyFailed);

            if(versionCode==null || versionCode=="" || orgcode==null || orgcode=="")
            {
                return null;
            }

            mapResult = xAdapterInfoSendManager.GetAdapterFileInfo(versionCode,orgcode);

            if (mapResult == null) return failed(ErrorCode.GenerateArchiveFailed);

            String group = (String) mapResult.get(FastDFSUtil.GroupField);
            String remoteFile = (String) mapResult.get(FastDFSUtil.RemoteFileField);

            if (group == null || remoteFile == null) return failed(ErrorCode.GenerateArchiveFailed);

            password = (String) mapResult.get("password");
            byte[] bytes = FastDFSUtil.download(group, remoteFile);
            fileBytes = Base64.encode(bytes);
        }catch (Exception e) {
            return failed(ErrorCode.DownArchiveFileFailed);
        }

        try {
            //密码RSA加密
            String privateKey = userSecurity.getPrivateKey();
            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(privateKey));

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("cryptograph", encryptPwd);
            restEcho.putResult("zipfile", fileBytes);
            return restEcho;
        } catch (IOException ex) {
            LogService.getLogger(AdapterDispatchRestController.class).error(ex.getMessage());

            return failed(ErrorCode.GenerateArchiveFileStreamFailed);
        } catch (Exception ex) {
            LogService.getLogger(AdapterDispatchRestController.class).error(ex.getMessage());

            return failed(ErrorCode.GenerateFileCryptographFailed);
        }
    }

    @RequestMapping(value = "/allSchemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getALLSchemeMappingInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) {
        Map<String, Object> mapResult = null;
        XUserSecurity userSecurity = null;
        String password = null;
        String fileBytes = null;
        try {
            userSecurity = securityManager.getUserSecurityByUserName(userName);
        } catch (Exception e) {
            LogService.getLogger(AdapterDispatchRestController.class).error(e.getMessage());
            return failed(ErrorCode.GenerateUserKeyFailed);
        }
        try {
            if (userSecurity == null) return failed(ErrorCode.GetUserKeyFailed);

            if(versionCode==null || versionCode=="" || orgcode==null || orgcode=="")
            {
                return null;
            }

            mapResult = xAdapterInfoSendManager.GetStandardAndMappingInfo(versionCode,orgcode);

            if (mapResult == null) return failed(ErrorCode.GenerateArchiveFailed);

            String group = (String) mapResult.get(FastDFSUtil.GroupField);
            String remoteFile = (String) mapResult.get(FastDFSUtil.RemoteFileField);

            if (group == null || remoteFile == null) return failed(ErrorCode.GenerateArchiveFailed);

            password = (String) mapResult.get("password");
            byte[] bytes = FastDFSUtil.download(group, remoteFile);
            fileBytes = Base64.encode(bytes);
        }catch (Exception e) {
            return failed(ErrorCode.DownArchiveFileFailed);
        }

        try {
            //密码RSA加密
            String privateKey = userSecurity.getPrivateKey();
            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(privateKey));

            RestEcho restEcho = new RestEcho().success();
            restEcho.putResult("cryptograph", encryptPwd);
            restEcho.putResult("zipfile", fileBytes);
            return restEcho;
        } catch (IOException ex) {
            LogService.getLogger(AdapterDispatchRestController.class).error(ex.getMessage());

            return failed(ErrorCode.GenerateArchiveFileStreamFailed);
        } catch (Exception ex) {
            LogService.getLogger(AdapterDispatchRestController.class).error(ex.getMessage());

            return failed(ErrorCode.GenerateFileCryptographFailed);
        }
    }

    @RequestMapping(value = "/versionplan", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ", response = RestEcho.class, produces = "application/json", notes = "指定版本的信息")
    public Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) {
        String json = "";
        try {

            Map<String, Object> args = new HashMap<>();
            args.put("orgcode", orgCode);

            List<XOrgAdapterPlan> listPlan = xOrgAdapterPlanManager.getOrgAdapterPlanByOrgCode(args);

            if (listPlan != null && listPlan.size()>0) {
                RestEcho restEcho = new RestEcho().success();
                restEcho.putResult("version", listPlan.get(0).getVersion().getVersion());
                restEcho.putResult("timestamp", listPlan.get(0).getVersion().getCommitTime().toString());

                return restEcho;
            } else {
                return failed(ErrorCode.GetCDAVersionFailed);
            }
        } catch (Exception ex) {
            return failed(ErrorCode.GetCDAVersionFailed);
        }
    }
}
