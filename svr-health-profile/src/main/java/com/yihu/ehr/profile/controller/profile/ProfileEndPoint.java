package com.yihu.ehr.profile.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.service.PatientInfoBaseService;
import com.yihu.ehr.profile.service.PatientInfoDetailService;
import com.yihu.ehr.profile.service.ProfileCDAService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 档案接口。提供就诊数据的原始档案，以CDA文档配置作为数据内容架构。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "健康档案服务", description = "提供档案搜索及完整档案下载")
public class ProfileEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PatientInfoBaseService patient;

    @Autowired
    PatientInfoDetailService patientDetail;

    @Autowired
    ProfileCDAService profileCDAService;

    @Autowired
    XTransformClient transform;

    private String file2String(String path) throws IOException {
        String folder=System.getProperty("java.io.tmpdir");
        String filePath = folder+path;
        File file = new File(filePath);
        return FileUtil.readAsString(file);
    }

    @ApiOperation("用户基本信息OK")
    @RequestMapping(value = ServiceApi.Profiles.ProfileInfo, method = RequestMethod.GET)
    public Map<String,Object> profileInfo(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="422428197704250025")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "version", value = "版本号",defaultValue="56395d75b854")
            @RequestParam(value = "version", required = false) String version) throws Exception {

        Map<String,Object> re = patient.getPatientInfo(demographic_id);
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

    @ApiOperation("用户患病史OK")
    @RequestMapping(value = ServiceApi.Profiles.ProfileHistory, method = RequestMethod.GET)
    public String ProfileHistory(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="422428197704250025")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {

        return file2String("/json/pastHistory.json");
    }

    @ApiOperation("主要健康问题OK")
    @RequestMapping(value = ServiceApi.Profiles.HealthProblem, method = RequestMethod.GET)
    public List<Map<String,Object>> HealthProblem(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="422428197704250025")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getHealthProblem(demographic_id);
    }

    @ApiOperation("就诊过的疾病OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalDisease, method = RequestMethod.GET)
    public List<Map<String,String>> MedicalDisease(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientDisease(demographic_id);
    }

    @ApiOperation("就诊过区域OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalArea, method = RequestMethod.GET)
    public List<Map<String,String>> MedicalArea(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientArea(demographic_id);
    }

    @ApiOperation("就诊过年份OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalYear, method = RequestMethod.GET)
    public List<String> MedicalYear(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientYear(demographic_id);
    }

    @ApiOperation("门诊/住院事件(时间轴)OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvents, method = RequestMethod.GET)
    public List<Map<String,Object>> MedicalEvents(
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

        List<Map<String,Object>> re = patient.getPatientMzZyEvents(demographicId, eventsType, year, area, hpId, diseaseId);
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

    @ApiOperation("某次就诊事件")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvent, method = RequestMethod.GET)
    public Map<String,Object> MedicalEvent(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="42017976-4_0000110721_ZY120000608919_1450165169000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {
        return patient.getMedicalEvent(profile_id);
    }

    @ApiOperation("个人用药统计")
    @RequestMapping(value = ServiceApi.Profiles.DrugStat, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugStat(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return null;
    }



    /******************************** CDA档案接口 ****************************************************/
    @ApiOperation("cda分类")
    @RequestMapping(value = ServiceApi.Profiles.CDAClass, method = RequestMethod.GET)
    public List<Map<String,Object>> CDAClass(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="42017976-4_0000145400_ZY010000806438_1454115983000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {

        return profileCDAService.getCDAClass(profile_id);
    }

    @ApiOperation("CDA数据")
    @RequestMapping(value = ServiceApi.Profiles.CDAData, method = RequestMethod.GET)
    public Map<String,Object> CDAData(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="42017976-4_0000145400_ZY010000806438_1454115983000")
            @RequestParam(value = "profile_id", required = true) String profile_id,
            @ApiParam(name = "cda_document_id", value = "模板ID",defaultValue="0dae000656b2e9480dc3568e6a372b59")
            @RequestParam(value = "cda_document_id", required = true) String cda_document_id) throws Exception {

        return profileCDAService.getCDAData(profile_id, cda_document_id);
    }

    @ApiOperation("完整CDA文档")
    @RequestMapping(value = ServiceApi.Profiles.CDADocument, method = RequestMethod.GET)
    public Map<String,Object> CDADocument(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="42017976-4_0000145400_ZY010000806438_1454115983000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {

        return  profileCDAService.getCDADocument(profile_id);
    }

    @ApiOperation("获取cda_document_id")
    @RequestMapping(value = ServiceApi.Profiles.CDADocumentId, method = RequestMethod.GET)
    public String CDADocumentId(
            @ApiParam(name = "event_no", value = "事件号",defaultValue="ZY010000806438") @RequestParam(value = "event_no", required = true) String event_no,
            @ApiParam(name = "cda_code", value = "模板类别",defaultValue="HSDC02.02") @RequestParam(value = "cda_code", required = true) String cda_code) throws Exception {
        return profileCDAService.getCDADocumentId(event_no, cda_code);
    }

    /******************************** 处方 ***********************************************************/
    @ApiOperation("处方主表")
    @RequestMapping(value = ServiceApi.Profiles.DrugMaster, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugMaster(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("处方笺")
    @RequestMapping(value = ServiceApi.Profiles.DrugSign, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugSign(
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("中药处方")
    @RequestMapping(value = ServiceApi.Profiles.DrugDetailChinese, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugDetailChinese(
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("西药处方")
    @RequestMapping(value = ServiceApi.Profiles.DrugDetailWestern, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugDetailWestern(
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    /******************************** 门诊 ***********************************************************/
    @ApiOperation("门诊诊断")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientDiagnosis, method = RequestMethod.GET)
    public List<Map<String,Object>> OutpatientDiagnosis(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("门诊症状")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientSymptom, method = RequestMethod.GET)
    public List<Map<String,Object>> OutpatientSymptom(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("门诊费用汇总")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientCostMaster, method = RequestMethod.GET)
    public List<Map<String,Object>> OutpatientCostMaster(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("门诊费用明细")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientCostDetail, method = RequestMethod.GET)
    public List<Map<String,Object>> OutpatientCostDetail(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    /******************************** 住院 ***********************************************************/
    @ApiOperation("住院诊断")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedDiagnosis, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedDiagnosis(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院症状")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedSymptom, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedSymptom(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院费用汇总")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedCostMaster, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedCostMaster(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院费用明细")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedCostDetail, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedCostDetail(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院临时医嘱")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedOrdersTemporary, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedOrdersTemporary(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院长期医嘱")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedOrdersLongtime, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedOrdersLongtime(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }

    @ApiOperation("住院死亡记录")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedDeath, method = RequestMethod.GET)
    public List<Map<String,Object>> HospitalizedDeath(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        return null;
    }


}
