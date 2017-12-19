package com.yihu.ehr.profile.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.model.MedicationStat;
import com.yihu.ehr.profile.service.*;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 档案事件接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.22
 */
@RestController
@Api(value = "档案事件接口", description = "档案事件接口")
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProfileEventEndPoint extends BaseRestEndPoint {

    @Autowired
    private PatientInfoBaseService patient;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private PatientEventService patientEvent;
    @Autowired
    private PatientInfoDetailService patientDetail;
    @Autowired
    private XTransformClient transform;
    @Autowired
    private IndicatorsService indicatorsService;

    @ApiOperation("患者基本信息OK")
    @RequestMapping(value = ServiceApi.Profiles.ProfileInfo, method = RequestMethod.GET)
    public Map<String, Object> profileInfo(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "version", value = "版本号", defaultValue = "59083976eebd")
            @RequestParam(value = "version", required = false) String version) {
        return patient.getPatientInfo(demographic_id, version);
    }

    @ApiOperation("患者患病史JSON")
    @RequestMapping(value = ServiceApi.Profiles.ProfileHistory, method = RequestMethod.GET)
    public String ProfileHistory(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue = "362321200108017313")
            @RequestParam(value = "demographic_id") String demographic_id) {

        return "[{\"pastHistoryType\":\"家族病史\",\"pastHistoryContents\":\"也就是医学中常常提到的家族史，也指某一种病的患者的家族成员（较大范围的家族成员，不仅限于祖孙等直系亲属）中发病情况。家族病史分为阴性跟阳性。 1)阴性（即没有发现同样病的患者）。临床上无家族史 2)阳性（即发现有同样病的患者）。比如：临床上讲糖尿病家族史、高血压病家族史、遗传型疾病家族史等。\"},\n" +
                "  {\"pastHistoryType\":\"传染史\",\"pastHistoryContents\":\"传染史..\"},\n" +
                "  {\"pastHistoryType\":\"家族史\",\"pastHistoryContents\":\"家族史..\"},\n" +
                "  {\"pastHistoryType\":\"手术史\",\"pastHistoryContents\":\"手术史..\"}]";
    }

    @ApiOperation("门诊/住院事件(时间轴)OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvents, method = RequestMethod.GET)
    public List<Map<String, Object>> MedicalEvents(
            @ApiParam(name = "demographic_id", value = "身份证号", required = true, defaultValue="422724197105101686")
            @RequestParam(value = "demographic_id") String demographic_id,
            @ApiParam(name = "events_type", value = "就诊事件类别")
            @RequestParam(value = "events_type", required = false) String events_type) {
        return patientEvent.getPatientEvents(demographic_id, events_type);
    }

    @ApiOperation("全文检索（待需求）")
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


		
    /*@ApiOperation("就诊过的疾病OK")
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
    }   */



    @ApiOperation("某次就诊事件OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicalEvent, method = RequestMethod.GET)
    public Map<String,Object> MedicalEvent(
            @ApiParam(name = "org_code", value = "机构代码",defaultValue="dm201705")
            @RequestParam(value = "org_code", required = true) String org_code,
            @ApiParam(name = "event_no", value = "事件号",defaultValue="23126080")
            @RequestParam(value = "event_no", required = true) String event_no,
            @ApiParam(name = "version", value = "版本号",defaultValue="57623f01b2d9")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Map<String,Object> re = patientEvent.getMedicalEvent(org_code,event_no);
        return adapterOne(version,re);
    }

    @ApiOperation("患者常用药物OK")
    @RequestMapping(value = ServiceApi.Profiles.MedicationUsed, method = RequestMethod.GET)
    public List<Map<String, Object>> MedicationUsed(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="422724197105101686")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题",defaultValue="GM")
            @RequestParam(value = "hp_id", required = false) String hp_id) throws Exception {

        return patientDetail.getMedicationUsed(demographic_id, hp_id);
    }

    @ApiOperation("患者用药清单（待需求）")
    @RequestMapping(value = ServiceApi.Profiles.MedicationStat, method = RequestMethod.GET)
    public List<MedicationStat> MedicalStat(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue="422724197105101686")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题")
            @RequestParam(value = "hp_id", required = false) String hp_id) throws Exception {

        return patientDetail.getMedicationStat(demographic_id, hp_id);
    }



    /***************************** 指标 ***************************************************/
    @ApiOperation("获取某个健康问题指标（待需求）")
    @RequestMapping(value = ServiceApi.Profiles.IndicatorsClass, method = RequestMethod.GET)
    public List<Map<String,Object>> IndicatorsClass(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
            @RequestParam(value = "demographic_id", required = true) String demographic_id,
            @ApiParam(name = "hp_id", value = "健康问题",defaultValue = "BXB")
            @RequestParam(value = "hp_id", required = true) String hp_id,
            @ApiParam(name = "indicator_type", value = "指标类别")
            @RequestParam(value = "indicator_type", required = false) String indicator_type) throws Exception{

        return indicatorsService.getIndicatorsClass(demographic_id,hp_id,indicator_type);
    }

    @ApiOperation("获取指标数据（待需求）")
    @RequestMapping(value = ServiceApi.Profiles.IndicatorsData, method = RequestMethod.GET)
    public Envelop IndicatorsData(
            @ApiParam(name = "demographic_id", value = "身份证号",defaultValue = "422724197105101686")
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

    /**
     * 单条记录转适配
     * @return
     */
    private Map<String,Object> adapterOne(String version,Map<String,Object> obj) throws Exception {
        if(version != null) {
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
    private List<Map<String,Object>> adapterBatch(String version,List<Map<String,Object>> list) throws Exception {
        if(version!=null) {
            MStdTransformDto stdTransformDto = new MStdTransformDto();
            stdTransformDto.setVersion(version);
            stdTransformDto.setSource(mapper.writeValueAsString(list));
            return transform.stdTransformList(mapper.writeValueAsString(stdTransformDto));
        }
        else{
            return list;
        }
    }
}
