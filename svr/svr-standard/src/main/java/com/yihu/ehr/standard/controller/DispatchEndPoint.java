//package com.yihu.ehr.standard.controller;
//
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.constants.ErrorCode;
//import com.yihu.ehr.constants.ServiceApi;
//import com.yihu.ehr.fastdfs.FastDFSUtil;
//import com.yihu.ehr.standard.common.ExtendEndPoint;
//import com.yihu.ehr.standard.service.DispatchService;
//import com.yihu.ehr.util.encrypt.RSA;
//import com.yihu.ehr.util.rest.RestEcho;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.Base64;
//import java.util.Map;
//

//与eip整合,未将其整合进去
//据志勇说,该接口已经没有用了,先注释掉 2018-01-22


///**
// * @author Sand
// * @version 1.0
// * @created 2015.07.14 17:59
// */
//@RestController
//@RequestMapping(ApiVersion.Version1_0)
//@Api(value = "DispatchEndPoint", description = "分发与下载", tags = {"标准服务-分发与下载"})
//public class DispatchEndPoint extends ExtendEndPoint {
//
//    @Autowired
//    private DispatchService dispatchService;
//    @Autowired
//    FastDFSUtil fastDFSUtil;
//
//    @RequestMapping(value = ServiceApi.Standards.Dispatches, method = RequestMethod.GET)
//    @ApiOperation(value = "获取适配方案摘要", response = RestEcho.class, produces = "application/json",
//            notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
//    public Object getSchemeInfo(
//            @ApiParam(required = true, name = "userPrivateKey", value = "用户私钥")
//            @RequestParam(value = "userPrivateKey", required = true) String userPrivateKey,
//            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
//            @RequestParam(value = "update_version", required = true) String updateVersion,
//            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
//            @RequestParam(value = "current_version", required = false) String currentVersion) {
//
//        Map<String, Object> schema = null;
//        String password = null;
//        byte[] fileBytes = null;
//
//        try {
//
//            if (updateVersion == null || "".equals(updateVersion))
//                return new RestEcho().failed(ErrorCode.MissParameter, " 缺失参数:updateVersion");
//
//            if (currentVersion == null) {
//                schema = dispatchService.sendStandard(updateVersion);
//            } else {
//                schema = dispatchService.sendStandard(currentVersion, updateVersion);
//            }
//
//            if (schema == null)
//                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 标准化数据生成失败");
//
//            String group = (String) schema.get(FastDFSUtil.GROUP_NAME);
//            String remoteFile = (String) schema.get(FastDFSUtil.REMOTE_FILE_NAME);
//
//            if (group == null || remoteFile == null)
//                return new RestEcho().failed(ErrorCode.GenerateArchiveFailed, " 标准化数据生成失败");
//
//            password = (String) schema.get("password");
//            byte[] bytes = fastDFSUtil.download(group, remoteFile);
//            fileBytes = Base64.getEncoder().encode(bytes);
//        } catch (Exception e) {
//            return new RestEcho().failed(ErrorCode.DownArchiveFileFailed, "下载标准适配版本失败");
//        }
//
//        try {
//            //密码RSA加密
//            String encryptPwd = RSA.encrypt(password, RSA.genPrivateKey(userPrivateKey));
//            RestEcho restEcho = new RestEcho().success();
//            restEcho.putResult("cryptograph", encryptPwd);
//            restEcho.putResult("zipfile", fileBytes);
//            return restEcho;
//        } catch (IOException ex) {
//            return new RestEcho().failed(ErrorCode.GenerateArchiveFileStreamFailed, "生成适配版本文件流失败");
//
//        } catch (Exception ex) {
//            return new RestEcho().failed(ErrorCode.GenerateFileCryptographFailed, "生成适配版本文件密码失败");
//        }
//    }
//
//
//    @RequestMapping(value = ServiceApi.Standards.Dispatches, method = RequestMethod.POST)
//    @ApiOperation(value = "生成适配方案摘要", produces = "application/json",
//            notes = "")
//    public Map createSchemeInfo(
//            @ApiParam(required = true, name = "version", value = "要生成的目标版本")
//            @RequestParam(value = "version", required = true) String version) throws Exception {
//
//        return dispatchService.sendStandard(version);
//    }
//}
//
