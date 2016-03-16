package com.yihu.ehr.pack.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.pack.service.JsonPackage;
import com.yihu.ehr.pack.service.JsonPackageService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * JSON 档案Rest控制器。
 *
 * @author Sand
 * @version 1.0
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "json_package_service", description = "档案数据包服务")
public class JsonPackageEndPoint extends BaseRestController {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private JsonPackageService jsonPackageService;

    @RequestMapping(value = RestApi.Packages.Packages, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", response = MPackage.class, responseContainer = "List", notes = "获取当前平台上的档案列表")
    public Collection<MPackage> packageList(@ApiParam(name = "archive_status", value = "档案包状态", defaultValue = "Received")
                                            @RequestParam(value = "archive_status")
                                            ArchiveStatus archiveStatus,
                                            @ApiParam(name = "since", value = "起始日期", defaultValue = "2015-12-01")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(value = "since") Date since,
                                            @ApiParam(name = "to", value = "截止日期", defaultValue = "2015-12-31")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")
                                            @RequestParam(value = "to") Date to,
                                            @ApiParam(name = "page", value = "页面号，从1开始", defaultValue = "1")
                                            @RequestParam(value = "page") int page,
                                            @ApiParam(name = "page_size", value = "页面记录数", defaultValue = "15")
                                            @RequestParam(value = "page_size") int pageSize) throws ParseException {

        Pageable pageable = new PageRequest(page, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("fromTime", since);
        map.put("toTime", to);
        map.put("archiveStatus", archiveStatus);
        List<JsonPackage> jsonPackageList = jsonPackageService.searchArchives(map, pageable);

        return convertToModels(jsonPackageList, new ArrayList<>(jsonPackageList.size()), MPackage.class, null);
    }

    /**
     * 接收档案包。
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     */
    @RequestMapping(value = RestApi.Packages.Packages, method = RequestMethod.PUT)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    @Deprecated
    public void savePackageWithUser(@ApiParam(required = false, name = "file_string", value = "JSON档案包字符串")
                                    @RequestParam(value = "file_string") String fileString,
                                    @ApiParam(required = true, name = "user_name", value = "用户名")
                                    @RequestParam(value = "user_name") String userName,
                                    @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
                                    @RequestParam(value = "package_crypto") String packageCrypto,
                                    @ApiParam(required = true, name = "md5", value = "档案包MD5")
                                    @RequestParam(value = "md5") String md5) throws Exception {

        if (StringUtils.isEmpty(fileString)) throw new ApiException(ErrorCode.MissParameter, "file");

        MKey userSecurity = securityClient.getUserKey(userName);
        String privateKey = userSecurity.getPrivateKey();
        if (null == privateKey) throw new ApiException(ErrorCode.GenerateUserKeyFailed);
        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        jsonPackageService.receive(new ByteArrayInputStream(fileString.getBytes()), unzipPwd);
    }

    /**
     * 接收档案包。
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     */
    @RequestMapping(value = RestApi.Packages.Packages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public void savePackageWithOrg(@ApiParam(required = false, name = "file_string", value = "JSON档案包字符串")
                                   @RequestParam(value = "file_string") String fileString,
                                   @ApiParam(required = true, name = "org_code", value = "机构代码")
                                   @RequestParam(value = "org_code") String orgCode,
                                   @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
                                   @RequestParam(value = "package_crypto") String packageCrypto,
                                   @ApiParam(required = true, name = "md5", value = "档案包MD5")
                                   @RequestParam(value = "md5") String md5) throws Exception {

        if (StringUtils.isEmpty(fileString)) throw new ApiException(ErrorCode.MissParameter, "file");

        MKey key = securityClient.getOrgKey(orgCode);
        String privateKey = key.getPrivateKey();
        if (null == privateKey) throw new ApiException(ErrorCode.GenerateUserKeyFailed);

        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        jsonPackageService.receive(new ByteArrayInputStream(fileString.getBytes()), unzipPwd);
    }

    /**
     * 获取档案包信息。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = RestApi.Packages.Package, method = {RequestMethod.GET})
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息")
    public ResponseEntity<MPackage> retrievePackage(@ApiParam(name = "id", value = "档案包编号")
                                                    @PathVariable(value = "id")
                                                    String id) {
        JsonPackage jsonPackage = jsonPackageService.getJsonPackage(id);
        if (jsonPackage == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

        MPackage pack = convertToModel(jsonPackage, MPackage.class, null);

        return new ResponseEntity<>(pack, HttpStatus.OK);
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
        jsonPackageService.deletePackage(id);
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
            InputStream is = jsonPackageService.downloadFile(id);
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
}
