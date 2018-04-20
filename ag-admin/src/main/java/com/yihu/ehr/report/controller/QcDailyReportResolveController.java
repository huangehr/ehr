package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.report.service.*;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReportResolve", description = "质控包数据文件解析", tags = {"报表管理-质控包数据文件解析"})
public class QcDailyReportResolveController extends ExtendController<QcDailyReport> {

    @Autowired
    QcDailyReportClient qcDailyReportClient;

    @RequestMapping(value = "/report/receiveReportFile", method = RequestMethod.POST)
    @ApiOperation(value = "接收质控包", notes = "从ESB平台接收质控包数据文件")
    public Envelop receiveReportFile(
            @ApiParam(name = "pack", value = "档案包", allowMultiple = true)
            @RequestPart() MultipartFile pack,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            @ApiParam(name = "type", value = "文件包类型 1 质控包, 2 日报包")
            @RequestParam(value = "type",defaultValue = "1", required = true) int type,
            HttpServletRequest request) throws Exception {

        File file = null;
        try {
            file = File.createTempFile("tmp", null);
            pack.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        qcDailyReportClient.receiveReportFile(file,orgCode,packageCrypto,md5,type);
        file.deleteOnExit();
        return success("解压入库成功");
    }

}
