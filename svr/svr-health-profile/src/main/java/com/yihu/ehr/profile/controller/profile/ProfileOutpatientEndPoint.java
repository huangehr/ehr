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
 * 档案门诊接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.23
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案门诊接口")
public class ProfileOutpatientEndPoint extends BaseRestEndPoint {

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
            @RequestParam(value = "prescription_no", required = false) String prescription_no,
            @ApiParam(name = "reproduce", value = "是否重新生成",defaultValue = "false")
            @RequestParam(value = "reproduce", required = false,defaultValue = "false") boolean reproduce) throws Exception
    {
        return patientDetail.getPrescription(profile_id,prescription_no,reproduce);
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


}
