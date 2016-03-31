package com.yihu.ehr.pack.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.feign.UserClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * 档案包控制器。
 *
 * @author Sand
 * @version 1.0
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "package_service", description = "档案包服务")
public class PackageEndPoint extends BaseRestController {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private PackageService packService;

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = RestApi.Packages.Packages, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", response = MPackage.class, responseContainer = "List", notes = "获取当前平台上的档案列表")
    public Collection<MPackage> packageList(
            @ApiParam(name = "archive_status", value = "档案包状态", defaultValue = "Received")
            @RequestParam(value = "archive_status")
                    ArchiveStatus archiveStatus,
            @ApiParam(name = "since", value = "起始日期", defaultValue = "2016-03-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "since") Date since,
            @ApiParam(name = "to", value = "截止日期", defaultValue = "2016-03-31")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "to") Date to,
            @ApiParam(name = "page", value = "页号，从1开始", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "page_size", value = "页面记录数", defaultValue = "15")
            @RequestParam(value = "page_size") int pageSize) throws ParseException {

        Pageable pageable = new PageRequest(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("since", since);
        map.put("to", to);
        map.put("archiveStatus", archiveStatus);
        List<Package> packageList = packService.searchArchives(map, pageable);

        return convertToModels(packageList, new ArrayList<>(packageList.size()), MPackage.class, null);
    }

    /**
     * 接收档案包。
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     */
    @RequestMapping(value = RestApi.Packages.Packages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public void savePackageWithOrg(
            @ApiParam(required = true, name = "package", value = "档案包", allowMultiple = true)
                    MultipartHttpServletRequest jsonPackage,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5) throws Exception {

        if (jsonPackage.getFile("file") == null) throw new ApiException(ErrorCode.MissParameter, "file");
        MultipartFile multipartFile = jsonPackage.getFile("file");
        byte[] bytes = multipartFile.getBytes();
        MKey key = securityClient.getOrgKey(orgCode);
        String privateKey = key.getPrivateKey();
        if (null == privateKey) throw new ApiException(ErrorCode.GenerateUserKeyFailed);

        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        packService.receive(new ByteArrayInputStream(bytes), unzipPwd);
    }

    /**
     * 获取档案包信息。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = RestApi.Packages.Package, method = {RequestMethod.GET})
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息")
    public ResponseEntity<MPackage> getPackage(@ApiParam(name = "id", value = "档案包编号")
                                                    @PathVariable(value = "id") String id) {
        Package aPackage;
        if (id.equals("OLDEST")){
            // only use for svr-pack-resolve, it will change pack status internal
            aPackage = packService.acquirePackage(id);
        } else {
            aPackage = packService.getPackage(id);
        }

        if (aPackage == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(convertToModel(aPackage, MPackage.class), HttpStatus.OK);
    }

    /**
     * 修改档案包状态。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = RestApi.Packages.Package, method = {RequestMethod.PUT})
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息")
    public ResponseEntity<MPackage> reportStatus(@ApiParam(value = "档案包编号")
                                                 @PathVariable(value = "id") String id,
                                                 @ApiParam(value = "状态")
                                                 @RequestParam(value = "status") ArchiveStatus status,
                                                 @ApiParam(value = "消息")
                                                 @RequestParam(value = "message") String message) {
        Package aPackage = packService.getPackage(id);
        if (aPackage == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

        if (status == ArchiveStatus.Failed) {
            packService.reportArchiveFailed(id, message);
        } else if (status == ArchiveStatus.Finished) {
            packService.reportArchiveFinished(id, message);
        }

        return new ResponseEntity<>((MPackage) null, HttpStatus.OK);
    }

    /**
     * 删除档案包。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = RestApi.Packages.Package, method = {RequestMethod.DELETE})
    @ApiOperation(value = "删除档案", response = Object.class, notes = "删除一个数据包")
    public void deletePackage(@ApiParam(name = "id", value = "档案包编号")
                              @PathVariable(value = "id")
                                      String id) {
        packService.deletePackage(id);
    }

    /**
     * 下载档案包。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = RestApi.Packages.PackageDownloads, method = {RequestMethod.GET})
    @ApiOperation(value = "获取档案包", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE, notes = "获取档案包的信息")
    public ResponseEntity<MPackage> downloadPackage(@ApiParam(name = "id", value = "档案包编号")
                                                    @PathVariable(value = "id")
                                                            String id,
                                                    HttpServletResponse response) throws Exception {
        try {
            InputStream is = packService.downloadFile(id);
            if (is == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + id + ".zip");

            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Cannot download package from server. " + ex.getMessage());
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    /**
     * 接收档案包(兼容旧接口)
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     */
    @RequestMapping(value = RestApi.Packages.LegacyPackages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    @Deprecated
    public void savePackageWithUser(
            @ApiParam(required = true, name = "package", value = "档案包", allowMultiple = true)
                    MultipartHttpServletRequest jsonPackage,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5") String md5) throws Exception {

        if (jsonPackage.getFile("file") == null) throw new ApiException(ErrorCode.MissParameter, "file");
        MultipartFile multipartFile = jsonPackage.getFile("file");
        byte[] bytes = multipartFile.getBytes();
        MUser user = userClient.getUserByUserName(userName);
        MKey key = securityClient.getUserKey(user.getId());
        String privateKey = key.getPrivateKey();
        if (null == privateKey) throw new ApiException(ErrorCode.GenerateUserKeyFailed);
        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        packService.receive(new ByteArrayInputStream(bytes), unzipPwd);
    }
}
