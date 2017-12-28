package com.yihu.ehr.report.scheduler;

import com.yihu.ehr.entity.report.JsonReport;
import com.yihu.ehr.report.service.JsonReportService;
import com.yihu.ehr.report.service.QcDailyReportResolveService;
import com.yihu.ehr.report.service.QcDailyStatisticsService;
import com.yihu.ehr.report.service.QuotaStatisticsService;
import com.yihu.ehr.util.CopyFileUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


/**
 * 质控定时任务
 * 此处应尽里只有调度代码。
 */
@Component
public class QcReportScheduler {

    public final static String FileDataPath = "ehr/data";
    public final static String FileReportPath = "ehr/report";
    private static final Logger log = LoggerFactory.getLogger(QcReportScheduler.class);
    private final static String TempPath = System.getProperty("java.io.tmpdir") + File.separator;
    @Autowired
    QcDailyStatisticsService qcDailyStatisticsService;
    @Autowired
    JsonReportService jsonReportService;
    @Autowired
    private QcDailyReportResolveService qcDailyReportResolveService;
    @Autowired
    private QuotaStatisticsService quotaStatisticsService;

    public void qcETLScheduler() throws Exception {
        qcDailyReportResolveService.loadQcData();
    }

    /**
     * 每天1点 执行一次
     * 解析由 ESB 系统上传上来的压缩包文件并入库
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 1 * * ?")
//	@Scheduled(cron = "0 0/1 * * * ?")
    public void resolveFileScheduler() throws Exception {
        resolveFile();
    }


    /**
     * 每天3点 执行一次
     * 统计数据质量的完整性 及时性
     *
     * @throws Exception
     */
    @Scheduled(cron = "0 0 3 * * ?")
//	@Scheduled(cron = "0 0/2 * * * ?")//每2分钟
    public void statisticalScheduler() throws Exception {
        List<Object> objectList = null;
        List<JsonReport> jsonReportList = null;
        try {
            //  统计当天采集过来的数据    待修改为昨天
            objectList = jsonReportService.getStatistOrgData(new Date());
            if (!objectList.isEmpty()) {
                for (Object object : objectList) {
                    String orgCode = object.toString();
                    for (int i = 1; i <= 7; i++) {
                        long start = System.currentTimeMillis();
                        quotaStatisticsService.statisticQuotaData(String.valueOf(i), orgCode, DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
                        long end = System.currentTimeMillis();
                        String message = "机构：" + orgCode + "，  " + String.valueOf(i) + "统计数据质量的完整性 及时性- 调度执行完成，总耗时：" + (end - start);
                        System.out.println(message);
                        log.debug(message);
                    }
                }
            }

            jsonReportList = jsonReportService.getJsonReportData(new Date());
            if (!jsonReportList.isEmpty()) {
                for (JsonReport jsonReport : jsonReportList) {
                    jsonReport.setStatus(2);
                    jsonReport.setStatisDate(new Date());
                    jsonReportService.updateJsonReport(jsonReport);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void resolveFile() {
        String filter = "status=0";
        List<JsonReport> jsonReports = null;
        try {
            jsonReports = jsonReportService.search(filter);
            if (!jsonReports.isEmpty()) {
                for (JsonReport jsonReport : jsonReports) {
                    long start = System.currentTimeMillis();
                    zipReportFileResolve(jsonReport.getId(), jsonReport.getType());
                    long end = System.currentTimeMillis();
                    String message = "机构：" + jsonReport.getOrgCode() + "，质控数据包文件解析入库  - 调度执行完成，总耗时：" + (end - start);
                    System.out.println(message);
                    log.debug(message);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void zipReportFileResolve(Integer id, int type) {
        try {
            JsonReport jsonReport = jsonReportService.getJsonReport(id);
            String password = jsonReport.getPwd();
            InputStream in = jsonReportService.downloadFile(id);
            String toFile = TempPath + FileReportPath + "/" + System.currentTimeMillis() + ".zip";
            String toDir = null;
            String orgCode = "";
            if (type == 1) {
                toDir = TempPath + FileDataPath;
                CopyFileUtil.copyFile(in, toFile, true);
                File file = new File(toFile);
                orgCode = qcDailyReportResolveService.resolveFile(file, password, toDir);
            } else if (type == 2) {
                toDir = TempPath + FileReportPath;
                CopyFileUtil.copyFile(in, toFile, true);
                File file = new File(toFile);
                orgCode = qcDailyReportResolveService.resolveReportFile(file, password, toDir);
            }
            jsonReport.setStatus(1);
            jsonReport.setReceiveDate(new Date());
            jsonReport.setOrgCode(orgCode);
            jsonReportService.updateJsonReport(jsonReport);
            FileUtils.deleteDirectory(new File(toDir));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
