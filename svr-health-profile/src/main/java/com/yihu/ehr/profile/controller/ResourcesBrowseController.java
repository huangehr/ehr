package com.yihu.ehr.profile.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.profile.service.PatientInfoBaseService;
import com.yihu.ehr.profile.service.PatientInfoDetailService;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.25 14:51
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/browse")
@Api(value = "rsBrowse", description = "业务资源浏览接口")
public class ResourcesBrowseController {

    @Autowired
    PatientInfoBaseService patient;

    @Autowired
    PatientInfoDetailService patientDetail;

    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public Map<String,Object> getPatientInfo(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientInfo(demographicId);
    }


    @ApiOperation("门户 - 病史信息（临时数据）")
    @RequestMapping(value = "/home/getDiseaseHistory", method = RequestMethod.GET)
    public String getDiseaseHistory(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return file2String("/json/pastHistory.json");
    }


    @ApiOperation("门户 - 主要健康问题")
    @RequestMapping(value = "/home/getHealthProblem", method = RequestMethod.GET)
    public List<Map<String,Object>> getHealthProblem(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {

        return patient.getHealthProblem(demographicId);
    }


    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public List<Map<String,Object>> getMedicalEvents(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "eventsType", value = "就诊事件类别")
            @RequestParam(value = "eventsType", required = false) String eventsType,
            @ApiParam(name = "year", value = "年份")
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @ApiParam(name = "area", value = "地区")
            @RequestParam(value = "area", required = false) String area,
            @ApiParam(name = "hpId", value = "健康问题id")
            @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "diseaseId", value = "疾病id")
            @RequestParam(value = "diseaseId", required = false) String diseaseId) throws Exception {

        return patient.getMedicalEvents(demographicId,eventsType,year,area,hpId,diseaseId);
    }

    @ApiOperation("就诊过的疾病")
    @RequestMapping(value = "/home/getPatientDisease", method = RequestMethod.GET)
    public List<String> getPatientDisease(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientDisease(demographicId);
    }

    @ApiOperation("就诊过的城市")
    @RequestMapping(value = "/home/getPatientArea", method = RequestMethod.GET)
    public List<String> getPatientArea(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientArea(demographicId);
    }

    @ApiOperation("就诊过的年份")
    @RequestMapping(value = "/home/getPatientYear", method = RequestMethod.GET)
    public List<String> getPatientYear(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientYear(demographicId);
    }


    /******************** CDA相关接口 ***************************************/
    @ApiOperation("档案搜索接口")
    @RequestMapping(value = "/medic/getArchives", method = RequestMethod.GET)
    public String getArchives(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "archiveType", value = "就诊类型(传空默认搜索全部数据)") @RequestParam(value = "archiveType", required = false) String archiveType,
            @ApiParam(name = "name", value = "诊断记录名称") @RequestParam(value = "name", required = false) String name) throws Exception {
        return file2String("/json/archiveSearch.json");

    }

    @ApiOperation("右侧档案模版展示")
    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    public String getPatientCdaInfo(
            @ApiParam(name = "profileId", value = "档案ID") @RequestParam(value = "profileId", required = true) String profileId) throws Exception {

        return "";
    }

    /************************************
     * 疾病事件
     *****************************************************************/
    @ApiOperation("疾病事件 - 历史用药 - 药品清单")
    @RequestMapping(value = "/disease/getDrugList", method = RequestMethod.GET)
    public Envelop getDrugList(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        //return file2String("/json/DrugList.json");
        return patientDetail.getDrugList(demographicId,hpId,startTime,endTime,page,size);
    }

    @ApiOperation("疾病事件 - 历史用药 - 用药统计")
    @RequestMapping(value = "/disease/getDrugListStat", method = RequestMethod.GET)
    public List<Map<String,Object>> getDrugListStat(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return patientDetail.getDrugListStat(demographicId, hpId);
    }


    /************************************
     * 健康指标
     *****************************************************************/
    //根据档案ID获取相关指标


    //获取指标指标
    @ApiOperation("门户 - 相关健康指标")
    @RequestMapping(value = "/health/getHealthIndicators", method = RequestMethod.GET)
    public Envelop getHealthIndicators(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康问题") @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "medicalIndexId", value = "指标项ID") @RequestParam(value = "medicalIndexId", required = false) String medicalIndexId,
            @ApiParam(name = "startTime", value = "查询开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "查询结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        //return file2String("/json/HealthIndicators.json");
        return patientDetail.getHealthIndicators(demographicId,hpId,medicalIndexId,startTime,endTime,page,size);
    }

    @ApiOperation("左侧指标列表导航")
    @RequestMapping(value = "/health/getHealthIndicatorItems", method = RequestMethod.GET)
    public String getHealthIndicatorItems(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = false) String demographicId,
            @ApiParam(name = "hpId", value = "指标项ID（逗号分隔）") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return file2String("/json/indicatorItems.json");
    }









    private String file2String(String path) throws IOException {
        String folder=System.getProperty("java.io.tmpdir");
        String filePath = folder+path;
        File file = new File(filePath);
        return FileUtil.readAsString(file);
    }


}
