package com.yihu.ehr.profile.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.service.PatientInfoBaseService;
import com.yihu.ehr.profile.service.PatientInfoDetailService;
import com.yihu.ehr.util.rest.Envelop;
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
    ObjectMapper mapper;

    @Autowired
    PatientInfoBaseService patient;

    @Autowired
    PatientInfoDetailService patientDetail;

    @Autowired
    XTransformClient transform;

    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public Map<String,Object> getPatientInfo(
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="422428197704250025")
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "version", value = "版本号",defaultValue="56395d75b854")
            @RequestParam(value = "version", required = false) String version) throws Exception {

        Map<String,Object> re = patient.getPatientInfo(demographicId);
        if(version!=null)
        {
            MStdTransformDto stdTransformDto = new MStdTransformDto();
            stdTransformDto.setSource(mapper.writeValueAsString(re));
            stdTransformDto.setVersion(version);
            return transform.stdTransform(mapper.writeValueAsString(stdTransformDto));
        }
        else{
            return re;
        }
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
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {

        return patient.getHealthProblem(demographicId);
    }


    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public List<Map<String,Object>> getMedicalEvents(
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="420521195812172917")
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
            @RequestParam(value = "diseaseId", required = false) String diseaseId,
            @RequestParam(value = "version", required = false) String version) throws Exception {

        List<Map<String,Object>> re = patient.getMedicalEvents(demographicId,eventsType,year,area,hpId,diseaseId);
        if(version!=null)
        {
            MStdTransformDto stdTransformDto = new MStdTransformDto();
            stdTransformDto.setVersion(version);
            stdTransformDto.setSource(mapper.writeValueAsString(re));
            return transform.stdTransformList(mapper.writeValueAsString(stdTransformDto));
        }
        else{
            return re;
        }
    }

    @ApiOperation("就诊过的疾病")
    @RequestMapping(value = "/home/getPatientDisease", method = RequestMethod.GET)
    public List<Map<String,String>> getPatientDisease(
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientDisease(demographicId);
    }

    @ApiOperation("就诊过的城市")
    @RequestMapping(value = "/home/getPatientArea", method = RequestMethod.GET)
    public List<Map<String,String>> getPatientArea(
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientArea(demographicId);
    }

    @ApiOperation("就诊过的年份")
    @RequestMapping(value = "/home/getPatientYear", method = RequestMethod.GET)
    public List<String> getPatientYear(
            @ApiParam(name = "demographicId", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        return patient.getPatientYear(demographicId);
    }


    /******************** CDA相关接口 ***************************************/
    @ApiOperation("档案详情 -- CDA分类")
    @RequestMapping(value = "/cda/getCDAClass", method = RequestMethod.GET)
    public List<Map<String,String>> getCDAClass(
            @ApiParam(name = "profileId", value = "档案ID") @RequestParam(value = "profileId", required = false,defaultValue="42017976-4_0000145400_ZY010000806438_1454115983000") String profileId,
            @ApiParam(name = "eventNo", value = "事件号") @RequestParam(value = "eventNo", required = false) String eventNo) throws Exception {
        if(profileId == null && eventNo == null) {
            throw new Exception("非法传参！");
        }
        return patientDetail.getCDAClass(profileId,eventNo);
    }

    @ApiOperation("档案详情 -- CDA数据")
    @RequestMapping(value = "/cda/getCDAData", method = RequestMethod.GET)
    public Map<String,List<Map<String,Object>>> getCDAData(
            @ApiParam(name = "profileId", value = "档案ID",defaultValue="42017976-4_0000145400_ZY010000806438_1454115983000")
            @RequestParam(value = "profileId", required = true) String profileId,
            @ApiParam(name = "templateId", value = "模板ID",defaultValue="186")
            @RequestParam(value = "templateId", required = true) Integer templateId) throws Exception {

        return patientDetail.getCDAData(profileId, templateId);
    }


    @ApiOperation("公众版门户 -- 档案详情")
    @RequestMapping(value = "/cda/getDocument", method = RequestMethod.GET)
    public JsonNode getDocument(
            @ApiParam(name = "profileId", value = "档案ID",defaultValue="42017976-4_0000786438_ZY010000816319_1459496935000")
            @RequestParam(value = "profileId", required = false) String profileId,
            @ApiParam(name = "version", value = "cda版本",defaultValue="56395d75b854")
            @RequestParam(value = "version", required = false) String version) throws Throwable {

        JsonNode jsonNode =  patientDetail.getDocument(profileId,version);
        return jsonNode;
    }


    @ApiOperation("档案详情 -- 通过事件号获取templateId")
    @RequestMapping(value = "/cda/getPatientCdaTemplate", method = RequestMethod.GET)
    public String getPatientCdaTemplate(
            @ApiParam(name = "eventNo", value = "事件号",defaultValue="ZY010000805783") @RequestParam(value = "eventNo", required = true) String eventNo,
            @ApiParam(name = "cdaType", value = "模板类别",defaultValue="LSYZ") @RequestParam(value = "cdaType", required = true) String cdaType) throws Exception {

        return patientDetail.getCDATemplate(eventNo,cdaType);
    }

    /************************************
     * 详细数据
     *****************************************************************/
    /*@ApiOperation("历史用药 - 药品清单")
    @RequestMapping(value = "/detail/getDrugList", method = RequestMethod.GET)
    public Envelop getDrugList(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "type", value = "0中药 1西药") @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "version", required = false) String version) throws Exception {

        //return file2String("/json/DrugList.json");
        Envelop re = patientDetail.getDrugList(demographicId, hpId, type, startTime, endTime, page, size);
        if(version!=null) {
            re.setDetailModelList(transform.stdTransformList(re.getDetailModelList(), version));
        }

        return re;
    }*/

    @ApiOperation("历史用药 - 用药统计")
    @RequestMapping(value = "/detail/getDrugListStat", method = RequestMethod.GET)
    public List<Map<String,Object>> getDrugListStat(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "hpId", value = "健康代码") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return patientDetail.getDrugListStat(demographicId, hpId);
    }

    /*@ApiOperation("门诊费用清单")
    @RequestMapping(value = "/detail/getOutpatientCost", method = RequestMethod.GET)
    public Envelop getOutpatientCost(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        Envelop re = patientDetail.getDrugList(demographicId, hpId, type, startTime, endTime, page, size);
        if(version!=null) {
            re.setDetailModelList(transform.stdTransformList(re.getDetailModelList(), version));
        }
        return patientDetail.getOutpatientCost(demographicId, startTime, endTime, page, size);
    }

    @ApiOperation("住院费用")
    @RequestMapping(value = "/detail/getHospitalizedCost", method = RequestMethod.GET)
    public Envelop getHospitalizedCost(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "startTime", value = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return patientDetail.getHospitalizedCost(demographicId,startTime,endTime,page,size);
    }*/
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
