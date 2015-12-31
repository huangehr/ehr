package com.yihu.ehr.pack.controller;

import com.sun.javaws.exceptions.InvalidArgumentException;
import com.yihu.ehr.constrant.ApiVersionPrefix;
import com.yihu.ehr.constrant.ArchiveStatus;
import com.yihu.ehr.constrant.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.pack.service.JsonPackage;
import com.yihu.ehr.pack.service.JsonPackageService;
import com.yihu.ehr.pack.service.XJsonPackageRepository;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * JSON 档案Rest控制器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.17 14:22
 */
@RestController
@RequestMapping(ApiVersionPrefix.CommonVersion + "/json_package_service")
@Api(protocols = "https", value = "json_package_service", description = "Json数据包归档接口", tags = {"JSON", "健康档案包"})
public class JsonPackageServiceRestController extends BaseRestController {
    @Autowired
    private JsonPackageService jsonPackageService;

    @Autowired
    private XJsonPackageRepository jsonPackageRepository;

    /**
     * 归档病人档案-数据上传
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     * @return
     */
    @RequestMapping(value = "/pack", method = {RequestMethod.POST})
    @ApiOperation(value = "接收档案", response = Object.class, notes = "从集成开放平台接收健康档案数据包")
    public Object receiveJsonPackage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(required = true, name = "package", value = "JSON档案包", allowMultiple = true)
            MultipartHttpServletRequest jsonPackage,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto) {

        try {
            MultipartFile file = jsonPackage.getFile("file");
            //if(file == null) throw new InvalidArgumentException({"file"});

            //XUserSecurity userSecurity = securityManager.getUserSecurityByUserName(userName);
            //if (null == userSecurity) return failed(ErrorCode.GenerateUserKeyFailed);

            String privateKey = "";// = userSecurity.getPrivateKey();
            String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
            jsonPackageService.receive(file.getInputStream(), unzipPwd);

            return null;
        } catch (IOException e) {
            failed(ErrorCode.SaveArchiveFailed, e.getMessage());
        } catch (Exception e) {
            failed(ErrorCode.ParseArchiveCryptoFailed, e.getMessage());
        }

        return null;
    }

    @RequestMapping(value = "/pack/{package_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "解析JSON包", response = Object.class, notes = "健康档案数据包入库")
    public Object parseJsonPackage(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "package_id", value = "包ID")
            @PathVariable(value = "package_id") String packageId) {
        JsonPackage jsonPackage = jsonPackageService.getJsonPackage(packageId);
        if (jsonPackage == null) {
            throw new ApiException(ErrorCode.UnknownJsonPackageId, "JSON包不存在.");
        }

        //JsonArchiver jsonArchiver = new JsonArchiver();
        //jsonArchiver.doArchive(jsonPackage);

        return null;
    }

    @RequestMapping(value = "/packs", method = RequestMethod.GET)
    public Object packageList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "archive_status", value = "档案包状态", defaultValue = "Acquired")
            @RequestParam(value = "archive_status") ArchiveStatus archiveStatus,
            @ApiParam(name = "from", value = "起始日期", defaultValue = "2015-10-01")
            @DateTimeFormat(pattern="yyyy-MM-dd")
            @RequestParam(value = "from") Date from,
            @ApiParam(name = "to", value = "截止日期", defaultValue = "2015-12-31")
            @DateTimeFormat(pattern="yyyy-MM-dd")
            @RequestParam(value = "to") Date to,
            @ApiParam(name = "page", value = "页面号，从0开始", defaultValue = "0")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "page_size", value = "页面记录数", defaultValue = "10")
            @RequestParam(value = "page_size") int pageSize) {
        try{
            Pageable pageable = new PageRequest(page, pageSize);
            List<JsonPackage> jsonPackageList = jsonPackageRepository.findAll(archiveStatus, from, to, pageable);

            return jsonPackageList;
        } catch(Exception e){
            return null;
        }
    }
}
