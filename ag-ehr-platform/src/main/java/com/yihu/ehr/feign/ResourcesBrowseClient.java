package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.25 14:51
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/browse")
@Api(value = "rsBrowse", description = "业务资源浏览接口")

@FeignClient(name= MicroServices.HealthProfile)
@ApiIgnore
public interface ResourcesBrowseClient {

    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    Map<String,Object> getPatientInfo(
            @RequestParam(value = "demographicId", required = true) String demographicId);


    @RequestMapping(value = "/home/getHealthProblem", method = RequestMethod.GET)
    List<Map<String,Object>> getHealthProblem(
            @RequestParam(value = "demographicId", required = true) String demographicId);


    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    List<Map<String,Object>> getMedicalEvents(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "eventsType", required = false) String eventsType,
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "hpId", required = false) String hpId,
            @RequestParam(value = "diseaseId", required = false) String diseaseId);

    @RequestMapping(value = "/home/getPatientDisease", method = RequestMethod.GET)
    List<Map<String,String>> getPatientDisease(
            @RequestParam(value = "demographicId", required = true) String demographicId);

    @RequestMapping(value = "/home/getPatientArea", method = RequestMethod.GET)
    List<Map<String,String>> getPatientArea(
            @RequestParam(value = "demographicId", required = true) String demographicId);

    @RequestMapping(value = "/home/getPatientYear", method = RequestMethod.GET)
    List<String> getPatientYear(
            @RequestParam(value = "demographicId", required = true) String demographicId);


    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    List<Map<String,Object>> getPatientCdaInfo(
            @RequestParam(value = "profileId", required = false) String profileId,
            @RequestParam(value = "eventNo", required = false) String eventNo);

    @RequestMapping(value = "/cda/getCDAData", method = RequestMethod.GET)
    List<Map<String,Object>> getPatientCdaInfo(
            @RequestParam(value = "profileId", required = false) String profileId,
            @RequestParam(value = "eventNo", required = false) String eventNo,
            @RequestParam(value = "templateId", required = true) String templateId);

    @RequestMapping(value = "/cda/getPatientCdaTemplate", method = RequestMethod.GET)
    String getPatientCdaTemplate(
            @RequestParam(value = "templateId", required = true) String templateId);

    @RequestMapping(value = "/detail/getDrugList", method = RequestMethod.GET)
    Envelop getDrugList(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "hpId", required = false) String hpId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    @RequestMapping(value = "/detail/getDrugListStat", method = RequestMethod.GET)
    List<Map<String,Object>> getDrugListStat(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "hpId", required = false) String hpId);

    @RequestMapping(value = "/detail/getOutpatientCost", method = RequestMethod.GET)
    public Envelop getOutpatientCost(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) ;

    @RequestMapping(value = "/detail/getHospitalizedCost", method = RequestMethod.GET)
    Envelop getHospitalizedCost(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    @RequestMapping(value = "/health/getHealthIndicators", method = RequestMethod.GET)
    Envelop getHealthIndicators(
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @RequestParam(value = "hpId", required = false) String hpId,
            @RequestParam(value = "medicalIndexId", required = false) String medicalIndexId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);
}
