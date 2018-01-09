package com.yihu.ehr.basic.report.controller;

import com.yihu.ehr.basic.report.service.JsonReportService;
import com.yihu.ehr.basic.report.service.QcDailyReportResolveService;
import com.yihu.ehr.basic.security.service.UserSecurityService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.JsonReport;
import com.yihu.ehr.entity.security.UserSecurity;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "JsonReport", description = "质控包数据上传解析", tags = {"质控包数据上传解析"})
public class QcDailyReportResolveController extends EnvelopRestEndPoint {

    public final static String FileDataPath = "ehr/data";
    public final static String FileReportPath = "ehr/report";
    private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;

    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private JsonReportService reportService;
    @Autowired
    private QcDailyReportResolveService qcDailyReportResolveService;
    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    @RequestMapping(value = "/report/receiveReportFile", method = RequestMethod.POST)
    @ApiOperation(value = "接收质控包")
    Envelop receiveReportFile(
            @ApiParam(name = "reportFile", value = "质控包", allowMultiple = true)
            @RequestParam(value = "reportFile") File reportFile,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "encrypt_pwd", value = "解压密码,二次加密")
            @RequestParam(value = "encrypt_pwd") String encryptPwd,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            @ApiParam(name = "type", value = "文件包类型 1 质控包 2 日报包")
            @RequestParam(value = "type", defaultValue = "1", required = true) int type
    ) throws Exception {
        Envelop envelop = new Envelop();
        String password = null;
        try {
            UserSecurity key = userSecurityService.getKeyByOrgCode(orgCode);
            if (key == null || key.getPrivateKey() == null) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
            }
            password = RSA.decrypt(encryptPwd, RSA.genPrivateKey(key.getPrivateKey()));
            InputStream in = new FileInputStream(reportFile);
            JsonReport jsonReport = reportService.receive(in, password, encryptPwd, md5, orgCode, type);
            if (jsonReport != null) {
                envelop.setSuccessFlg(true);
            } else {
                envelop.setSuccessFlg(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }

    @RequestMapping(value = "qcBeginDate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiParam(value = "设置质控统计起始时间，质控任务根据起始时间分析档案，形成质控报表")
    public Envelop setQcBeginDate(@ApiParam(name = "date", value = "统计起始时间")
                                  @RequestParam(value = "date") String date) {
        return qcDailyReportResolveService.setQcBeginDate(date);
    }


}