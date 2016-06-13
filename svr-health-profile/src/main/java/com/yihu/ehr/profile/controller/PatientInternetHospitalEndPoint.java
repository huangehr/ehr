package com.yihu.ehr.profile.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.profile.service.BasisConstant;
import com.yihu.ehr.profile.service.PatientInternetHospitalService;
import com.yihu.ehr.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 互联网医院病人相关信息接口
 * Created by lyr on 2016/6/13.
 */
@Api(value="PatientInternetHospital",description = "互联网医院病人门诊相关信息接口")
@RequestMapping(value= ApiVersion.Version1_0 + "/patients")
@RestController
public class PatientInternetHospitalEndPoint extends BaseRestEndPoint {

    @Autowired
    PatientInternetHospitalService patientServie;

    @ApiOperation("获取病人基础信息")
    @RequestMapping(value="/{patient_id}/baseinfo",method= RequestMethod.GET)
    public Map<String,Object> getPatientBaseInfo(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.patientInfo,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }

    @ApiOperation("获取病人门诊挂号信息")
    @RequestMapping(value="/{patient_id}/registeredinfo",method= RequestMethod.GET)
    public Map<String,Object> getPatientRegisteredInfo(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.patientEvent,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }


    @ApiOperation("获取病人门诊诊断纪录")
    @RequestMapping(value="/{patient_id}/diagnosticrecord",method= RequestMethod.GET)
    public Map<String,Object> getPatientDiagnosticRecord (
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.outpatientDiagnosis,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }

    @ApiOperation("获取病人门诊症状纪录")
    @RequestMapping(value="/{patient_id}/symptomrecord",method= RequestMethod.GET)
    public Map<String,Object> getPatientSymptomRecord (
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.outpatientDiagnosis,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }

    @ApiOperation("获取病人门诊主处方")
    @RequestMapping(value="/{patient_id}/mainprescription",method= RequestMethod.GET)
    public Map<String,Object> getPatientMainPrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.outpatientDiagnosis,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }

    @ApiOperation("获取病人门诊西药处方")
    @RequestMapping(value="/{patient_id}/westernprescription",method= RequestMethod.GET)
    public List<Map<String,Object>> getPatientWesternPrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.medicationWestern,query);

        return resource;
    }

    @ApiOperation("获取病人门诊中药处方")
    @RequestMapping(value="/{patient_id}/chineseprescription",method= RequestMethod.GET)
    public List<Map<String,Object>> getPatientChinesePrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.medicationChinese,query);

        return resource;
    }

    @ApiOperation("获取病人门诊处方签图片")
    @RequestMapping(value="/{patient_id}/prescriptionsignpicture",method= RequestMethod.GET)
    public List<Map<String,Object>> getPatientPrescriptionSignPicture(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.medicationChinese,query);

        return resource;
    }

    @ApiOperation("获取病人门诊总费用")
    @RequestMapping(value="/{patient_id}/totalcost",method= RequestMethod.GET)
    public Map<String,Object> getPatientTotalCost(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.outpatientCost,query);

        return resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>();
    }

    @ApiOperation("获取病人门诊费用清单")
    @RequestMapping(value="/{patient_id}/costlist",method= RequestMethod.GET)
    public List<Map<String,Object>> getPatientCostList(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true)
            String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id")
            String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true)
            String event_no) throws Exception
    {
        String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
        List<Map<String,Object>> resource = patientServie.getEhrResource(BasisConstant.outpatientCost,query);

        return resource;
    }
}
