package com.yihu.ehr.report.controller;

import ch.qos.logback.core.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.JsonReport;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.org.feign.SecurityClient;
import com.yihu.ehr.report.service.QcDailyReportResolveService;
import com.yihu.ehr.report.service.JsonReportService;
import com.yihu.ehr.util.CopyFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;


@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "JsonReport", description = "质控包数据上传解析", tags = {"质控包数据上传解析"})
public class QcDailyReportResolveController extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private JsonReportService jsonReportService;
    @Autowired
    private SecurityClient securityClient;
    @Autowired
    private JsonReportService reportService;
    @Autowired
    private QcDailyReportResolveService qcDailyReportResolveService;
    @Autowired
    FastDFSUtil fastDFSUtil;

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;
    private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;
    public final static String FileDataPath = "ehr/data";
    public final static String FileReportPath = "ehr/report";




    @RequestMapping(value ="/report/receiveReportFile", method = RequestMethod.POST)
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
            @RequestParam(value = "type",defaultValue = "1", required = true) int type
    ) throws Exception {
        Envelop envelop = new Envelop();
        String password = null;
        try {
            MKey key = securityClient.getOrgKey(orgCode);
            if (key == null ||  key.getPrivateKey()==null) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
            }
            password = RSA.decrypt(encryptPwd, RSA.genPrivateKey(key.getPrivateKey()));
            InputStream in =new FileInputStream(reportFile);
            JsonReport jsonReport = reportService.receive(in, password,encryptPwd, md5, orgCode,type);
            if(jsonReport != null){
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return envelop;
    }


    @RequestMapping(value = "/report/zipFileResolve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "质控包数据文件接收解析入库", notes = "质控包数据文件接收解析入库")
    public void zipFileResolve(
            @ApiParam(name = "id", value = "质控包编号", defaultValue = "")
            @RequestParam(required = false) Integer id) throws Exception {
        try {
            JsonReport jsonReport = jsonReportService.getJsonReport(id);
            String orgCode = jsonReport.getOrgCode();
            String password = jsonReport.getPwd();
            InputStream in = jsonReportService.downloadFile(id);
            String toFile = TempPath + FileDataPath +  "/" + System.currentTimeMillis() +".zip";
            String toDir = TempPath + FileDataPath;
            CopyFileUtil.copyFile(in,toFile,true);
            File file = new File(toFile);
            qcDailyReportResolveService.resolveFile(file, password,toDir);
            jsonReportService.updateJsonReport(id,1);
            FileUtils.deleteDirectory(new File(toDir));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/report/zipReportFileResolve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "日报包数据文件接收解析入库", notes = "日报包数据文件接收解析入库")
    public void zipReportFileResolve(
            @ApiParam(name = "id", value = "质控包编号", defaultValue = "")
            @RequestParam(required = false) Integer id) throws Exception {
        try {
            JsonReport jsonReport = jsonReportService.getJsonReport(id);
            String orgCode = jsonReport.getOrgCode();
            String password = jsonReport.getPwd();
            InputStream in = jsonReportService.downloadFile(id);
            String toFile = TempPath + FileReportPath +  "/" + System.currentTimeMillis() +".zip";
            String toDir = TempPath + FileDataPath;
            CopyFileUtil.copyFile(in,toFile,true);
            File file = new File(toFile);
            qcDailyReportResolveService.resolveReportFile(file, password, toDir);
            jsonReportService.updateJsonReport(id,1);
            FileUtils.deleteDirectory(new File(toDir));
        }catch (Exception e){
            e.printStackTrace();
        }
    }






}