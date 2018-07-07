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
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Long result = govFirstPageReportService.countEmergencyAttendance(orgCode, date);
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
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Long result = govFirstPageReportService.countHospitalizationAttendance(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计平均住院日")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.AverageHospitalStayDay, method = RequestMethod.GET)
    public Envelop averageHospitalStayDay(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Double result = govFirstPageReportService.averageHospitalStayDay(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院人次人头比")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatHospitalizationHeadToHeadRatio, method = RequestMethod.GET)
    public Envelop statHospitalizationHeadToHeadRatio(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.statHospitalizationHeadToHeadRatio(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计门急诊费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatEmergencyExpense, method = RequestMethod.GET)
    public Envelop statEmergencyExpense(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.statEmergencyExpense(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.StatHospitalizationExpense, method = RequestMethod.GET)
    public Envelop statHospitalizationExpense(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.statHospitalizationExpense(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计门急诊人均医药费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.AverageEmergencyExpense, method = RequestMethod.GET)
    public Envelop averageEmergencyExpense(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.averageEmergencyExpense(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "按月统计住院人均医药费用")
    @RequestMapping(value = ServiceApi.GovFirsPage.Report.AverageHospitalizationExpense, method = RequestMethod.GET)
    public Envelop averageHospitalizationExpense(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam String orgCode,
            @ApiParam(name = "date", value = "日期（年月），格式：yyyy-MM-dd", required = true)
            @RequestParam String date) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            String result = govFirstPageReportService.averageHospitalizationExpense(orgCode, date);
            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
