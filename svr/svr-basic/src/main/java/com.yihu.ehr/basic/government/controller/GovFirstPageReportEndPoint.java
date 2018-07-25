package com.yihu.ehr.basic.government.controller;

import com.yihu.ehr.basic.government.service.GovFirstPageReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张进军
 * @date 2018/7/6 13:46
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "首页报表接口", tags = {"政府服务平台--首页报表接口"})
public class GovFirstPageReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private GovFirstPageReportService govFirstPageReportService;

    @ApiOperation(value = "按月统计门急诊人次")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.CountEmergencyAttendance, method = RequestMethod.GET)
    public Envelop countEmergencyAttendance(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Long result = govFirstPageReportService.countEmergencyAttendance(orgArea, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院人次")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.CountHospitalizationAttendance, method = RequestMethod.GET)
    public Envelop countHospitalizationAttendance(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Long result = govFirstPageReportService.countHospitalizationAttendance(orgArea, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计门急诊费用、环比增幅")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatEmergencyExpense, method = RequestMethod.GET)
    public Envelop statEmergencyExpense(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            String emergencyExpense = govFirstPageReportService.statEmergencyExpense(orgArea, date);
            String emergencyExpenseRatio = govFirstPageReportService.statEmergencyExpenseRatio(orgArea, date);
            result.put("emergencyExpense", emergencyExpense);
            result.put("emergencyExpenseRatio", emergencyExpenseRatio);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院费用、环比增幅")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatHospitalizationExpense, method = RequestMethod.GET)
    public Envelop statHospitalizationExpense(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            String hospitalizationExpense = govFirstPageReportService.statHospitalizationExpense(orgArea, date);
            String hospitalizationExpenseRatio = govFirstPageReportService.statHospitalizationExpenseRatio(orgArea, date);
            result.put("hospitalizationExpense", hospitalizationExpense);
            result.put("hospitalizationExpenseRatio", hospitalizationExpenseRatio);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计门急诊医药费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatEmergencyMedicineExpense, method = RequestMethod.GET)
    public Envelop statEmergencyMedicineExpense(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.statEmergencyMedicineExpense(orgArea, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院医药费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatHospitalizationMedicineExpense, method = RequestMethod.GET)
    public Envelop statHospitalizationMedicineExpense(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.statHospitalizationMedicineExpense(orgArea, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "统计近6个月的门急诊、住院人次趋势")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatAttendanceTrend, method = RequestMethod.GET)
    public Envelop statAttendanceTrend(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> emergencyAttendanceTrend = govFirstPageReportService.statAttendanceTrend(orgArea, date, 0);
            Map<String, Object> hospitalizationAttendanceTrend = govFirstPageReportService.statAttendanceTrend(orgArea, date, 1);
            result.put("emergencyAttendanceTrend", emergencyAttendanceTrend);
            result.put("hospitalizationAttendanceTrend", hospitalizationAttendanceTrend);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "统计近6个月的门急诊、住院费用趋势")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatExpenseTrend, method = RequestMethod.GET)
    public Envelop statExpenseTrend(
            @ApiParam(name = "orgArea", value = "区县编码，全市则不传")
            @RequestParam(required = false) String orgArea,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> emergencyExpenseTrend = govFirstPageReportService.statExpenseTrend(orgArea, date, 0);
            Map<String, Object> hospitalizationExpenseTrend = govFirstPageReportService.statExpenseTrend(orgArea, date, 1);
            result.put("emergencyExpenseTrend", emergencyExpenseTrend);
            result.put("hospitalizationExpenseTrend", hospitalizationExpenseTrend);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
