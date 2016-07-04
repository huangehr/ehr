package com.yihu.ehr.profile.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.model.MedicationStat;
import com.yihu.ehr.profile.service.*;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    IndicatorsService indicatorsService;

    /**
     * 单条记录转适配
     * @return
     */
    private Map<String,Object> adapterOne(String version,Map<String,Object> obj) throws Exception
    {
        if(version!=null)
        {
            MStdTransformDto stdTransformDto = new MStdTransformDto();
            stdTransformDto.setSource(mapper.writeValueAsString(obj));
            stdTransformDto.setVersion(version);
            return transform.stdTransform(mapper.writeValueAsString(stdTransformDto));
        }
        else{
            return obj;
        }
    }

    /**
     * 多条记录转适配
     * @return
     */
    private List<Map<String,Object>> adapterBatch(String version,List<Map<String,Object>> list) throws Exception
    {
        if(version!=null)
        {
            MStdTransformDto stdTransformDto = new MStdTransformDto();
            stdTransformDto.setVersion(version);
            stdTransformDto.setSource(mapper.writeValueAsString(list));
            return transform.stdTransformList(mapper.writeValueAsString(stdTransformDto));
        }
        else{
            return list;
        }
    }


    @ApiOperation("全文检索")
    @RequestMapping(value = ServiceApi.Profiles.ProfileLucene, method = RequestMethod.GET)
    public Envelop ProfileLucene(
            @ApiParam(name = "startTime", value = "开始时间")
            @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间")
            @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(name = "lucene", value = "全文检索内容")
            @RequestParam(value = "lucene", required = false) List<String> lucene,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号",defaultValue="56395d75b854")
            @RequestParam(value = "version", required = false) String version) throws Exception {

        Envelop re = patient.getProfileLucene(startTime,endTime,lucene,page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("患者基本信息OK")
    @RequestMapping(value = ServiceApi.Profiles.ProfileInfo, method = RequestMethod.GET)
    public Map<String,Object> profileInfo(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "version", value = "版本号",defaultValue="56395d75b854")
            @RequestParam(value = "version", required = false) String version) throws Exception {

        Map<String,Object> re = patient.getPatientInfo(demographic_id);
        return adapterOne(version,re);
    }

    @ApiOperation("患者患病史JSON")
    @RequestMapping(value = ServiceApi.Profiles.ProfileHistory, method = RequestMethod.GET)
    public String ProfileHistory(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {

        return "[{\"pastHistoryType\":\"家族病史\",\"pastHistoryContents\":\"也就是医学中常常提到的家族史，也指某一种病的患者的家族成员（较大范围的家族成员，不仅限于祖孙等直系亲属）中发病情况。家族病史分为阴性跟阳性。 1)阴性（即没有发现同样病的患者）。临床上无家族史 2)阳性（即发现有同样病的患者）。比如：临床上讲糖尿病家族史、高血压病家族史、遗传型疾病家族史等。\"},\n" +
                "  {\"pastHistoryType\":\"传染史\",\"pastHistoryContents\":\"传染史..\"},\n" +
                "  {\"pastHistoryType\":\"家族史\",\"pastHistoryContents\":\"家族史..\"},\n" +
                "  {\"pastHistoryType\":\"手术史\",\"pastHistoryContents\":\"手术史..\"}]";
    }

    @ApiOperation("主要健康问题OK")
    @RequestMapping(value = ServiceApi.Profiles.HealthProblem, method = RequestMethod.GET)
    public List<Map<String,Object>> HealthProblem(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getHealthProblem(demographic_id);
    }

    @ApiOperation("就诊过的疾病OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalDisease, method = RequestMethod.GET)
    public List<Map<String,String>> MedicalDisease(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientDisease(demographic_id);
    }

    @ApiOperation("就诊过区域OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalArea, method = RequestMethod.GET)
    public List<Map<String,String>> MedicalArea(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientArea(demographic_id);
    }

    @ApiOperation("就诊过年份OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalYear, method = RequestMethod.GET)
    public List<String> MedicalYear(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id) throws Exception {
        return patient.getPatientYear(demographic_id);
    }

    @ApiOperation("门诊/住院事件(时间轴)OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvents, method = RequestMethod.GET)
    public List<Map<String,Object>> MedicalEvents(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "events_type", value = "就诊事件类别")
            @RequestParam(value = "events_type", required = false) String events_type,
            @ApiParam(name = "year", value = "年份")
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @ApiParam(name = "area", value = "地区")
            @RequestParam(value = "area", required = false) String area,
            @ApiParam(name = "hp_id", value = "健康问题id")
            @RequestParam(value = "hp_id", required = false) String hp_id,
            @ApiParam(name = "disease_id", value = "疾病id")
            @RequestParam(value = "disease_id", required = false) String disease_id,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {

        List<Map<String,Object>> re = patient.getPatientEvents(demographic_id, events_type, year, area, hp_id, disease_id);
        return adapterBatch(version,re);
    }

    @ApiOperation("某次就诊事件OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvent, method = RequestMethod.GET)
    public Map<String,Object> MedicalEvent(
            @ApiParam(name = "org_code", value = "机构代码",defaultValue="41872607-9")
            @RequestParam(value = "org_code", required = true) String org_code,
            @ApiParam(name = "event_no", value = "档案ID",defaultValue="30000001")
            @RequestParam(value = "event_no", required = true) String event_no,
            @ApiParam(name = "version", value = "版本号",defaultValue="57623f01b2d9")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Map<String,Object> re = patient.getMedicalEvent(org_code,event_no);
        return adapterOne(version,re);
    }

    @ApiOperation("患者常用药物OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationUsed, method = RequestMethod.GET)
    public List<Map<String, Object>> MedicationUsed(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题",defaultValue="GM")
            @RequestParam(value = "hp_id", required = false) String hp_id) throws Exception {

        return patientDetail.getMedicationUsed(demographic_id, hp_id);
    }

    @ApiOperation("患者用药清单OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationStat, method = RequestMethod.GET)
    public List<MedicationStat> MedicalStat(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="360101200006011131")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题")
            @RequestParam(value = "hp_id", required = false) String hp_id) throws Exception {

        return patientDetail.getMedicationStat(demographic_id, hp_id);
    }


    /******************************** CDA档案接口 ****************************************************/
    @ApiOperation("CDA分类OK")
    @RequestMapping(value = ServiceApi.Profiles.CDAClass, method = RequestMethod.GET)
    public List<Map<String,Object>> CDAClass(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {

        return profileCDAService.getCDAClass(profile_id);
    }

    @ApiOperation("CDA数据OK")
    @RequestMapping(value = ServiceApi.Profiles.CDAData, method = RequestMethod.GET)
    public Map<String,Object> CDAData(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id,
            @ApiParam(name = "cda_document_id", value = "模板ID",defaultValue="0dae0006568a12a20dc35654490ab357")
            @RequestParam(value = "cda_document_id", required = true) String cda_document_id) throws Exception {

        return profileCDAService.getCDAData(profile_id, cda_document_id);
    }

    @ApiOperation("完整CDA文档OK")
    @RequestMapping(value = ServiceApi.Profiles.CDADocument, method = RequestMethod.GET)
    public Map<String,Object> CDADocument(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue="41872607-9_1000000_30000001_1465894742000")
            @RequestParam(value = "profile_id", required = true) String profile_id) throws Exception {

        return  profileCDAService.getCDADocument(profile_id);
    }

    @ApiOperation("获取cda_document_idOK")
    @RequestMapping(value = ServiceApi.Profiles.CDADocumentId, method = RequestMethod.GET)
    public Map<String, Object> CDADocumentId(
            @ApiParam(name = "org_code", value = "机构代码",defaultValue="41872607-9")
            @RequestParam(value = "org_code", required = true) String org_code,
            @ApiParam(name = "event_no", value = "事件号",defaultValue="30000001")
            @RequestParam(value = "event_no", required = true) String event_no,
            @ApiParam(name = "cda_code", value = "模板类别",defaultValue="HSDC01.04")
            @RequestParam(value = "cda_code", required = true) String cda_code) throws Exception {
        return profileCDAService.getCDADocumentId(org_code,event_no, cda_code);
    }

    /******************************** 处方 ***********************************************************/
    @ApiOperation("处方主表OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationMaster, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugMaster(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "version", value = "版本号",defaultValue = "57623f01b2d9")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        if(demographic_id==null&&profile_id==null&&prescription_no==null)
        {
            throw new Exception("非法传参！");
        }

        List<Map<String,Object>> re = patientDetail.getMedicationMaster(demographic_id, profile_id, prescription_no);

        return adapterBatch(version,re);
    }

    //1.西药处方；2.中药处方
    @ApiOperation("根据处方编号获取处方细表数据OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationDetail, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugDetail(
            @ApiParam(name = "prescription_no", value = "处方编号",defaultValue = "00000002")
            @RequestParam(value = "prescription_no", required = true) String prescription_no,
            @ApiParam(name = "type", value = "类型",defaultValue = "2")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        List<Map<String,Object>> re = patientDetail.getMedicationDetail(prescription_no, type);

        return adapterBatch(version,re);
    }

    @ApiOperation("处方笺OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationPrescription, method = RequestMethod.GET)
    public List<Map<String,Object>> DrugPrescription(
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "41872607-9_1000000_20000002_1465894742000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "prescription_no", value = "处方编号")
            @RequestParam(value = "prescription_no", required = false) String prescription_no) throws Exception
    {
        return patientDetail.getPrescription(profile_id,prescription_no);
    }

    @ApiOperation("中药处方OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationDetailChinese, method = RequestMethod.GET)
    public Envelop DrugDetailChinese(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题")
            @RequestParam(value = "hp_id", required = false) String hp_id,
            @ApiParam(name = "start_time", value = "开始时间")
            @RequestParam(value = "start_time", required = false) String start_time,
            @ApiParam(name = "end_time", value = "结束时间")
            @RequestParam(value = "end_time", required = false) String end_time,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getMedicationList("2", demographic_id, hp_id, start_time, end_time, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("西药处方OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationDetailWestern, method = RequestMethod.GET)
    public Envelop DrugDetailWestern(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题")
            @RequestParam(value = "hp_id", required = false) String hp_id,
            @ApiParam(name = "start_time", value = "开始时间")
            @RequestParam(value = "start_time", required = false) String start_time,
            @ApiParam(name = "end_time", value = "结束时间")
            @RequestParam(value = "end_time", required = false) String end_time,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getMedicationList("1", demographic_id, hp_id, start_time, end_time, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    /******************************** 门诊 ***********************************************************/
    @ApiOperation("门诊诊断OK")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientDiagnosis, method = RequestMethod.GET)
    public Envelop OutpatientDiagnosis(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.outpatientDiagnosis, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("门诊症状OK")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientSymptom, method = RequestMethod.GET)
    public Envelop OutpatientSymptom(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.outpatientSymptom, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("门诊费用汇总OK")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientCostMaster, method = RequestMethod.GET)
    public Envelop OutpatientCostMaster(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.outpatientCost, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("门诊费用明细NON")
    @RequestMapping(value = ServiceApi.Profiles.OutpatientCostDetail, method = RequestMethod.GET)
    public Envelop OutpatientCostDetail(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.outpatientCostDetail, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    /******************************** 住院 ***********************************************************/
    @ApiOperation("住院诊断OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedDiagnosis, method = RequestMethod.GET)
    public Envelop HospitalizedDiagnosis(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "330101200006011945")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedDiagnosis, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院症状OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedSymptom, method = RequestMethod.GET)
    public Envelop HospitalizedSymptom(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedSymptom, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院费用汇总OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedCostMaster, method = RequestMethod.GET)
    public Envelop HospitalizedCostMaster(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedCost, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院费用明细NON")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedCostDetail, method = RequestMethod.GET)
    public Envelop HospitalizedCostDetail(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedCostDetail, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院临时医嘱OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedOrdersTemporary, method = RequestMethod.GET)
    public Envelop HospitalizedOrdersTemporary(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedOrdersTemporary, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院长期医嘱OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedOrdersLongtime, method = RequestMethod.GET)
    public Envelop HospitalizedOrdersLongtime(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedOrdersLongtime, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("住院死因链情况OK")
    @RequestMapping(value = ServiceApi.Profiles.HospitalizedDeath, method = RequestMethod.GET)
    public Envelop HospitalizedDeath(
            @ApiParam(name = "demographic_id", value = "身份证号")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID",defaultValue = "jkzl_10003180_30000001_1467275158000")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedDeath, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    /********************** 检查检验 ***********************************************/
    @ApiOperation("检查报告单OK")
    @RequestMapping(value = ServiceApi.Profiles.ExaminationReport, method = RequestMethod.GET)
    public Envelop ExaminationReport(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.examinationReport, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("检查报告单图片OK")
    @RequestMapping(value = ServiceApi.Profiles.ExaminationImg, method = RequestMethod.GET)
    public Envelop ExaminationImg(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.examinationImg, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("检验报告单OK")
    @RequestMapping(value = ServiceApi.Profiles.LaboratoryReport, method = RequestMethod.GET)
    public Envelop LaboratoryReport(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryReport, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("检验报告单图片OK")
    @RequestMapping(value = ServiceApi.Profiles.LaboratoryImg, method = RequestMethod.GET)
    public Envelop LaboratoryImg(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryImg, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("检验报告项目OK")
    @RequestMapping(value = ServiceApi.Profiles.LaboratoryProject, method = RequestMethod.GET)
    public Envelop LaboratoryProject(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryProject, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("检验药敏OK")
    @RequestMapping(value = ServiceApi.Profiles.LaboratoryAllergy, method = RequestMethod.GET)
    public Envelop LaboratoryAllergy(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryAllergy, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    @ApiOperation("手术记录OK")
    @RequestMapping(value = ServiceApi.Profiles.Surgery, method = RequestMethod.GET)
    public Envelop Surgery(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographic_id,
            @ApiParam(name = "profile_id", value = "档案ID")
            @RequestParam(value = "profile_id", required = false) String profile_id,
            @ApiParam(name = "event_no", value = "事件号")
            @RequestParam(value = "event_no", required = false) String event_no,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.surgery, demographic_id, profile_id,event_no, page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

    /***************************** 指标 ***************************************************/
    @ApiOperation("获取某个健康问题指标OK")
    @RequestMapping(value = ServiceApi.Profiles.IndicatorsClass, method = RequestMethod.GET)
    public List<Map<String,Object>> IndicatorsClass(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题",defaultValue = "BXB")
            @RequestParam(value = "hp_id", required = true) String hp_id,
            @ApiParam(name = "indicator_type", value = "指标类别")
            @RequestParam(value = "indicator_type", required = false) String indicator_type) throws Exception{

        return indicatorsService.getIndicatorsClass(demographic_id,hp_id,indicator_type);
    }

    @ApiOperation("获取指标数据OK")
    @RequestMapping(value = ServiceApi.Profiles.IndicatorsData, method = RequestMethod.GET)
    public Envelop IndicatorsData(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "420521195812172917")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "indicator_code", value = "指标代码")
            @RequestParam(value = "indicator_code", required = false) String indicator_code,
            @ApiParam(name = "date_from", value = "指标开始时间")
            @RequestParam(value = "date_from", required = false) String date_from,
            @ApiParam(name = "date_end", value = "指标结束时间")
            @RequestParam(value = "date_end", required = false) String date_end,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception{

        Envelop re = indicatorsService.getIndicatorsData(demographic_id,indicator_code,date_from,date_end,page,size);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }
}
