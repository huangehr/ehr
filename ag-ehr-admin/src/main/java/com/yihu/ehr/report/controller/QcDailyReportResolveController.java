package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.*;
import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDataset;
import com.yihu.ehr.entity.report.QcDailyReportDatasets;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDatasets;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.report.service.*;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.QcDatasetsParser;
import com.yihu.ehr.util.QcMetadataParser;
import com.yihu.ehr.util.ResolveJsonFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReportResolve", description = "质控包数据文件解析", tags = {"报表管理-质控包数据文件解析"})
public class QcDailyReportResolveController extends ExtendController<QcDailyReport> {

    private final  static String fileContainName = "datasets";
    @Autowired
    QcDailyReportClient qcDailyReportClient;
    @Autowired
    private QcDailyReportResolveService qcDailyReportResolveService;
    @Autowired
    SecurityClient securityClient;

    @RequestMapping(value = "/report/zipFileResolve", method = RequestMethod.POST)
    @ApiOperation(value = "质控包数据文件接收解析入库", notes = "质控包数据文件接收解析入库")
    public Envelop savePackageWithOrg(
            @ApiParam(name = "zipFile", value = "文件压缩包", allowMultiple = true)
            @RequestParam(name = "zipFile",required = true) File zipFile,
            @ApiParam(name = "orgCode",required = true, value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "packageCrypto",required = true, value = "文件包解压密码,二次加密")
            @RequestParam(value = "packageCrypto") String packageCrypto,
            @ApiParam(name = "md5", value = "文件包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            HttpServletRequest request) throws Exception {

        try {
            MKey key = securityClient.getOrgKey(orgCode);
            if (key == null ||  key.getPrivateKey()==null) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
            }
            String password = RSA.decrypt(packageCrypto, RSA.genPrivateKey(key.getPrivateKey()));
            qcDailyReportResolveService.resolveFile(zipFile, "");
            //入库后统计
            qcDailyReportClient.statisticQuotaDataReportData("2",orgCode,DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            qcDailyReportClient.statisticQuotaDataReportData("3",orgCode,DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            qcDailyReportClient.statisticQuotaDataReportData("4",orgCode,DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
            Envelop envelop = success("解压入库成功");
            envelop.setObj(null);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.GetEventDataReport, method = RequestMethod.POST)
    @ApiOperation(value = "日报数据采集上传")
    public Envelop  getEventDataReport(
            @ApiParam(name = "eventsData", value = "采集json数据", defaultValue = "")
            @RequestParam(name = "eventsData",required = true) String collectionData) {
        try {
            QcDailyEventsModel eventsModel = objectMapper.readValue(collectionData, QcDailyEventsModel.class);
            MQcDailyReport qcDailyReport = new MQcDailyReport();
            List<MQcDailyReportDetail> totalList = new ArrayList<>();
            List<MQcDailyReportDetail> realList = new ArrayList<>();
            if(eventsModel != null){
                if(StringUtils.isEmpty(eventsModel.getCreate_date())){
                    return failed("采集时间不能为空");
                }
                if(StringUtils.isEmpty(eventsModel.getOrg_code())){
                    return failed("机构编码不能为空");
                }
                Date createDate = DateUtil.parseDate(eventsModel.getCreate_date(), "yyyy-MM-dd hh:mm:ss");
                qcDailyReport.setOrgCode(eventsModel.getOrg_code());
                qcDailyReport.setCreateDate(createDate );
                qcDailyReport.setInnerVersion(eventsModel.getInner_version());
                qcDailyReport.setRealHospitalNum(eventsModel.getReal_hospital_num());
                qcDailyReport.setTotalHospitalNum(eventsModel.getTotal_hospital_num());
                qcDailyReport.setRealOutpatientNum(eventsModel.getReal_outpatient_num());
                qcDailyReport.setTotalOutpatientNum(eventsModel.getTotal_outpatient_num());
                qcDailyReport = qcDailyReportClient.add(objectMapper.writeValueAsString(qcDailyReport));
                if(eventsModel.getTotal_hospital() != null){
                    qcDailyReportResolveService.addList(totalList, eventsModel.getTotal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                }
                if(eventsModel.getReal_hospital() != null){
                    qcDailyReportResolveService.addList(realList, eventsModel.getReal_hospital(), qcDailyReport.getId(), createDate, "hospital");
                }
                if(eventsModel.getTotal_outpatient() != null){
                    qcDailyReportResolveService.addList(totalList, eventsModel.getTotal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                }
                if(eventsModel.getReal_outpatient() != null){
                    qcDailyReportResolveService.addList(realList, eventsModel.getReal_outpatient(), qcDailyReport.getId(), createDate, "outpatient");
                }
                List<MQcDailyReportDetail> dailyReportDetailList = qcDailyReportResolveService.checkRealListFromTotal(totalList, realList);
                qcDailyReportClient.addQcDailyReportDetailList(objectMapper.writeValueAsString(dailyReportDetailList));
            }
            //入库后统计
            Date date = new Date();
            qcDailyReportClient.statisticQuotaDataReportData("1",eventsModel.getOrg_code(), DateUtil.formatDate(date,"yyyy-MM-dd"));
            qcDailyReportClient.statisticQuotaDataReportData("5",eventsModel.getOrg_code(),DateUtil.formatDate(date,"yyyy-MM-dd"));
            qcDailyReportClient.statisticQuotaDataReportData("6",eventsModel.getOrg_code(),DateUtil.formatDate(date,"yyyy-MM-dd"));
            qcDailyReportClient.statisticQuotaDataReportData("7",eventsModel.getOrg_code(),DateUtil.formatDate(date,"yyyy-MM-dd"));

            Envelop envelop = success("解析入库成功");
            envelop.setObj(null);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }




}
