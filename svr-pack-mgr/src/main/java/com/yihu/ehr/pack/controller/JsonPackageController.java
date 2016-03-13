package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.model.packs.MJsonPackage;
import com.yihu.ehr.pack.service.JsonPackage;
import com.yihu.ehr.pack.service.JsonPackageService;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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
@Api(protocols = "https", value = "json_package_service", description = "Json数据包归档接口", tags = {"JSON,健康档案包"})
public class JsonPackageController extends BaseRestController {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private JsonPackageService jsonPackageService;

    @RequestMapping(value = "/packages", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", response = MJsonPackage.class, responseContainer = "List", notes = "获取当前平台上的档案列表")
    public Collection<MJsonPackage> packageList(
            @ApiParam(name = "archive_status", value = "档案包状态", defaultValue = "Acquired")
            @RequestParam(value = "archive_status") ArchiveStatus archiveStatus,
            @ApiParam(name = "from", value = "起始日期", defaultValue = "2015-10-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "since") Date since,
            @ApiParam(name = "to", value = "截止日期", defaultValue = "2015-12-31")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(value = "to") Date to,
            @ApiParam(name = "page", value = "页面号，从0开始", defaultValue = "0")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "page_size", value = "页面记录数", defaultValue = "10")
            @RequestParam(value = "page_size") int pageSize) throws ParseException {

        Pageable pageable = new PageRequest(page, pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("fromTime",since);
        map.put("toTime",to);
        map.put("archiveStatus",archiveStatus);
        List<JsonPackage> jsonPackageList = jsonPackageService.searchArchives(map, pageable);

        return convertToModels(jsonPackageList, new ArrayList<MJsonPackage>(jsonPackageList.size()),MJsonPackage.class,null);
    }

    /**
     * 接收档案包。
     *
     * @param packageCrypto zip密码密文, file 请求体中文件参数名
     */
    @RequestMapping(value = "/packages", method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public void savePackage(
            @ApiParam(required = false, name = "file_string", value = "JSON档案包字符串")
            @RequestParam(value = "user_name") String fileString,
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(required = true, name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(required = true, name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5") String md5) throws Exception {

        if (StringUtils.isEmpty(fileString)) throw new ApiException(ErrorCode.MissParameter, "file");

        String privateKey = securityClient.getUserSecurityByLoginCode(userName).getPrivateKey();
        if (null == privateKey) throw new ApiException(ErrorCode.GenerateUserKeyFailed);
        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        jsonPackageService.receive(new ByteArrayInputStream(fileString.getBytes()), unzipPwd);
    }


    /**
     * 获取档案包。
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/packages/{id}", method = {RequestMethod.POST})
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息")
    public MJsonPackage retrievePackage(
            @ApiParam(name = "id", value = "档案包编号", defaultValue = "v1.0")
            @PathVariable(value = "id") String id) {
        JsonPackage jsonPackage = jsonPackageService.getJsonPackage(id);
        return convertToModel(jsonPackage, MJsonPackage.class, null);
    }

    @RequestMapping(value = "/packages/{id}", method = {RequestMethod.DELETE})
    @ApiOperation(value = "删除档案", response = Object.class, notes = "删除一个数据包")
    public void deletePackage(
           @ApiParam(name = "id", value = "档案包编号")
           @PathVariable(value = "id") String id) {
        jsonPackageService.deletePackage(id);
    }
}

