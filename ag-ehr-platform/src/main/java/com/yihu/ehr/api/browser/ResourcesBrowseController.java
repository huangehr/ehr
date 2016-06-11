package com.yihu.ehr.api.browser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.ResourcesBrowseClient;
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

/**
 * @author linaz
 * @created 2016.05.25 14:51
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/profile")
@Api(value = "rsBrowse", description = "业务资源浏览接口")
public class ResourcesBrowseController {

    @Autowired
    private ResourcesBrowseClient resourcesBrowseClient;

    @Autowired
    private ObjectMapper objectMapper;


    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public String getPatientInfo(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientInfo(demographicId));
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
    public String getHealthProblem(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {

        return objectMapper.writeValueAsString(resourcesBrowseClient.getHealthProblem(demographicId));
    }


    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public String getMedicalEvents(
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

        return objectMapper.writeValueAsString(resourcesBrowseClient.getMedicalEvents(demographicId,eventsType,year,area,hpId,diseaseId));
    }

    @ApiOperation("就诊过的疾病")
    @RequestMapping(value = "/home/getPatientDisease", method = RequestMethod.GET)
    public String getPatientDisease(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientDisease(demographicId));
    }

    @ApiOperation("就诊过的城市")
    @RequestMapping(value = "/home/getPatientArea", method = RequestMethod.GET)
    public String getPatientArea(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientArea(demographicId));
    }

    @ApiOperation("就诊过的年份")
    @RequestMapping(value = "/home/getPatientYear", method = RequestMethod.GET)
    public String getPatientYear(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientYear(demographicId));
    }


    /******************** CDA相关接口 ***************************************/
    @ApiOperation("档案详情 -- CDA分类")
    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    public String getPatientCdaInfo(
            @ApiParam(name = "profileId", value = "档案ID") @RequestParam(value = "profileId", required = false) String profileId,
            @ApiParam(name = "eventNo", value = "事件号") @RequestParam(value = "eventNo", required = false) String eventNo) throws Exception {

        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientCdaInfo(profileId,eventNo));
    }

    @ApiOperation("档案详情 -- CDA数据")
    @RequestMapping(value = "/cda/getCDAData", method = RequestMethod.GET)
    public String getPatientCdaInfo(
            @ApiParam(name = "profileId", value = "档案ID") @RequestParam(value = "profileId", required = false) String profileId,
            @ApiParam(name = "eventNo", value = "事件号") @RequestParam(value = "eventNo", required = false) String eventNo,
            @ApiParam(name = "templateId", value = "模板ID") @RequestParam(value = "templateId", required = true) String templateId) throws Exception {
        return objectMapper.writeValueAsString(resourcesBrowseClient.getPatientCdaInfo(profileId,eventNo,templateId));
    }

    @ApiOperation("档案详情 -- CDA模板")
    @RequestMapping(value = "/cda/getPatientCdaTemplate", method = RequestMethod.GET)
    public String getPatientCdaTemplate(
            @ApiParam(name = "templateId", value = "模板ID") @RequestParam(value = "templateId", required = true) String templateId) throws Exception {

        return resourcesBrowseClient.getPatientCdaTemplate(templateId);
    }

    /************************************
     * 详细数据
     *****************************************************************/
    @ApiOperation("历史用药 - 药品清单")
    @RequestMapping(value = "/detail/getDrugList", method = RequestMethod.GET)
    public Envelop getDrugList(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "type", value = "0中药 1西药") @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesBrowseClient.getDrugList(demographicId,hpId,type,startTime,endTime,page,size);
    }

    @ApiOperation("历史用药 - 用药统计")
    @RequestMapping(value = "/detail/getDrugListStat", method = RequestMethod.GET)
    public String getDrugListStat(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return objectMapper.writeValueAsString(resourcesBrowseClient.getDrugListStat(demographicId, hpId));
    }

    @ApiOperation("门诊费用清单")
    @RequestMapping(value = "/detail/getOutpatientCost", method = RequestMethod.GET)
    public Envelop getOutpatientCost(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesBrowseClient.getOutpatientCost(demographicId, startTime, endTime, page, size);
    }

    @ApiOperation("住院费用")
    @RequestMapping(value = "/detail/getHospitalizedCost", method = RequestMethod.GET)
    public Envelop getHospitalizedCost(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesBrowseClient.getHospitalizedCost(demographicId,startTime,endTime,page,size);
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

        return resourcesBrowseClient.getHealthIndicators(demographicId,hpId,medicalIndexId,startTime,endTime,page,size);
    }

    @ApiOperation("左侧指标列表导航")
    @RequestMapping(value = "/health/getHealthIndicatorItems", method = RequestMethod.GET)
    public String getHealthIndicatorItems(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = false) String demographicId,
            @ApiParam(name = "hpId", value = "指标项ID（逗号分隔）") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return file2String("/json/indicatorItems.json");
    }


    @ApiOperation("档案搜索接口")
    @RequestMapping(value = "/medic/getArchives", method = RequestMethod.GET)
    public String getArchives(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "archiveType", value = "就诊类型(传空默认搜索全部数据)") @RequestParam(value = "archiveType", required = false) String archiveType,
            @ApiParam(name = "name", value = "诊断记录名称") @RequestParam(value = "name", required = false) String name) throws Exception {
        return file2String("/json/archiveSearch.json");

    }

    private String file2String(String path) throws IOException {
        String folder=System.getProperty("java.io.tmpdir");
        String filePath = folder+path;
        File file = new File(filePath);
        return FileUtil.readAsString(file);
    }


}
