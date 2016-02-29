package com.yihu.ehr.standard.dispatch.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.dispatch.service.DispatchService;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.07.14 17:59
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/standard-dispatcher")
@Api(protocols = "https", value = "Standard-Dispatch", description = "标准分发接口", tags = {"标准化", "适配方案", "分发"})
public class StandardDispatchRestController  extends ExtendController{

    @Autowired
    private DispatchService dispatchService;

    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = "application/json",
            notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    public List getSchemeInfo(
            @ApiParam(required = true, name = "userPrivateKey", value = "用户私钥")
            @RequestParam(value = "userPrivateKey", required = true) String userPrivateKey,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = true) String currentVersion) {

        Map<String, Object> schema = null;
        String password = null;
        String fileBytes = null;

        try {

            if (updateVersion == null || "".equals(updateVersion))
                throw errMissParm("update_version");

            if (currentVersion == null) {
                schema = dispatchService.sendStandard(updateVersion);
            } else {
                schema = dispatchService.sendStandard(currentVersion, updateVersion);
            }

            if (schema == null)
                throw new ApiException(ErrorCode.GenerateArchiveFailed);

            String group = (String) schema.get(FastDFSUtil.GroupField);
            String remoteFile = (String) schema.get(FastDFSUtil.RemoteFileField);

            if (group == null || remoteFile == null)
                throw new ApiException(ErrorCode.GenerateArchiveFailed);

            password = (String) schema.get("password");
            byte[] bytes = new FastDFSUtil().download(group, remoteFile);
            fileBytes = Base64.encode(bytes);
        }catch (Exception e) {
            throw new ApiException(ErrorCode.DownArchiveFileFailed);
        }

        try {
            List rs = new ArrayList<>();
            //密码RSA加密
            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(userPrivateKey));
            rs.add(encryptPwd);
            rs.add(fileBytes);
            return rs;
        } catch (IOException ex) {
            throw new ApiException(ErrorCode.GenerateArchiveFileStreamFailed);
        } catch (Exception ex) {
            throw new ApiException(ErrorCode.GenerateFileCryptographFailed);
        }
    }
}

