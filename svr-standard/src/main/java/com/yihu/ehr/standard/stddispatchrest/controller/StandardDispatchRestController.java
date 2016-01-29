package com.yihu.ehr.standard.stddispatchrest.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:59
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/standard-dispatcher")
@Api(protocols = "https", value = "Standard-Dispatch", description = "标准分发接口", tags = {"标准化", "适配方案", "分发"})
public class StandardDispatchRestController  {
//    @Resource(name = Services.SecurityManager)
//    private XSecurityManager securityManager;
//
//    @Resource(name = Services.StdDispatchManager)
//    private XStdDispatchManager stdSendManager;

    /**
     * @param userName       用户名，用来获取私钥对返回密码进行加密
     * @param updateVersion  需要更新的版本
     * @param currentVersion 本地版本
     * @throws Exception
     */
    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = "application/json", notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    public Object getSchemeInfo(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = true) String currentVersion) {
//        Map<String, Object> schema = null;
//        XUserSecurity userSecurity = null;
//        String password = null;
//        String fileBytes = null;
//        try {
//            userSecurity = securityManager.getUserSecurityByUserName(userName);
//        } catch (Exception e) {
//            LogService.getLogger(StandardDispatchRestController.class).error(e.getMessage());
//            return failed(ErrorCode.GenerateUserKeyFailed);
//        }
//        try {
//            if (userSecurity == null) return failed(ErrorCode.GetUserKeyFailed);
//
//            if (updateVersion == null || "".equals(updateVersion)) missParameter("update_version");
//
//            if (currentVersion == null) {
//                schema = stdSendManager.SendStandard(updateVersion);
//            } else {
//                schema = stdSendManager.SendStandard(currentVersion, updateVersion);
//            }
//
//            if (schema == null) return failed(ErrorCode.GenerateArchiveFailed);
//
//            String group = (String) schema.get(FastDFSUtil.GroupField);
//            String remoteFile = (String) schema.get(FastDFSUtil.RemoteFileField);
//
//            if (group == null || remoteFile == null) return failed(ErrorCode.GenerateArchiveFailed);
//
//            password = (String) schema.get("password");
//            byte[] bytes = FastDFSUtil.download(group, remoteFile);
//            fileBytes = Base64.encode(bytes);
//        }catch (Exception e) {
//            return failed(ErrorCode.DownArchiveFileFailed);
//        }
//
//        try {
//            //密码RSA加密
//            String privateKey = userSecurity.getPrivateKey();
//            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(privateKey));
//
//            RestEcho restEcho = new RestEcho().success();
//            restEcho.putResult("cryptograph", encryptPwd);
//            restEcho.putResult("zipfile", fileBytes);
//            return restEcho;
//        } catch (IOException ex) {
//            LogService.getLogger(StandardDispatchRestController.class).error(ex.getMessage());
//
//            return failed(ErrorCode.GenerateArchiveFileStreamFailed);
//        } catch (Exception ex) {
//            LogService.getLogger(StandardDispatchRestController.class).error(ex.getMessage());
//
//            return failed(ErrorCode.GenerateFileCryptographFailed);
//        }
        return "";
    }
}

