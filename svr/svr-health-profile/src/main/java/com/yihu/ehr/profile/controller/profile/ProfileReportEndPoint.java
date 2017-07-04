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
 * 档案检查检验接口
 * @author hzp
 * @version 1.0
 * @created 2017.06.23
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案检查检验接口", description = "档案检查检验接口")
public class ProfileReportEndPoint extends BaseRestEndPoint {

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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.examinationReport, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.examinationImg, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryReport, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryImg, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryProject, demographic_id, profile_id,event_no, page,size,saas_org);
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
            @ApiParam(name = "saas_org", value = "授权机构")
            @RequestParam(value = "saas_org", required = false) String saas_org,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Envelop re = patientDetail.getProfileSub(BasisConstant.laboratoryAllergy, demographic_id, profile_id,event_no, page,size,saas_org);
        re.setDetailModelList(adapterBatch(version,re.getDetailModelList()));
        return re;
    }

}
