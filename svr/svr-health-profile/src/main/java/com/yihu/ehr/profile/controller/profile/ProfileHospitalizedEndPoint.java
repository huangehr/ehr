package com.yihu.ehr.profile.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
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
 * 档案住院接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.22
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileHospitalizedEndPoint", description = "住院接口", tags = {"档案影像服务 - 住院接口"})
public class ProfileHospitalizedEndPoint extends BaseRestEndPoint {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ProfileInfoBaseService patient;

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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedDiagnosis, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedSymptom, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedCost, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedCostDetail, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedOrdersTemporary, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedOrdersLongtime, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.hospitalizedDeath, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.surgery, demographic_id, profile_id,event_no, page,size,saas_org);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

}
