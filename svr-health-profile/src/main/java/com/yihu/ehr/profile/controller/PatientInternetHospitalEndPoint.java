package com.yihu.ehr.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.service.BasisConstant;
import com.yihu.ehr.profile.service.PatientInternetHospitalService;
import com.yihu.ehr.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.shaded.org.mortbay.util.ajax.JSONObjectConvertor;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
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
    public String getPatientBaseInfo(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.patientInfo,query,false,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊挂号信息")
    @RequestMapping(value="/{patient_id}/registeredinfo",method= RequestMethod.GET)
    public String getPatientRegisteredInfo(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.patientEvent,query,true,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }


    @ApiOperation("获取病人门诊诊断纪录")
    @RequestMapping(value="/{patient_id}/diagnosticrecord",method= RequestMethod.GET)
    public String getPatientDiagnosticRecord (
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.outpatientDiagnosis,query,true,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊症状纪录")
    @RequestMapping(value="/{patient_id}/symptomrecord",method= RequestMethod.GET)
    public String getPatientSymptomRecord (
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.outpatientDiagnosis,query,true,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊主处方")
    @RequestMapping(value="/{patient_id}/mainprescription",method= RequestMethod.GET)
    public String getPatientMainPrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.outpatientDiagnosis,query,true,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊西药处方")
    @RequestMapping(value="/{patient_id}/westernprescription",method= RequestMethod.GET)
    public String getPatientWesternPrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.medicationWestern,query,true,version);

            return getSuccessString("data",resource);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊中药处方")
    @RequestMapping(value="/{patient_id}/chineseprescription",method= RequestMethod.GET)
    public String getPatientChinesePrescription(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.medicationChinese,query,true,version);

            return getSuccessString("data",resource);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊处方签图片")
    @RequestMapping(value="/{patient_id}/prescriptionsignpicture",method= RequestMethod.GET)
    public String getPatientPrescriptionSignPicture(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.medicationChinese,query,true,version);

            return getSuccessString("data",resource);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊总费用")
    @RequestMapping(value="/{patient_id}/totalcost",method= RequestMethod.GET)
    public String getPatientTotalCost(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.outpatientCost,query,true,version);

            return getSuccessString("data",resource.size() > 0 ? resource.get(0) : new HashMap<String, Object>());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    @ApiOperation("获取病人门诊费用清单")
    @RequestMapping(value="/{patient_id}/costlist",method= RequestMethod.GET)
    public String getPatientCostList(
            @ApiParam(name="org_code",value="机构代码",required = true)
            @RequestParam(value="org_code",required = true) String org_code,
            @ApiParam(name="patient_id",value="病人代码",required = true)
            @PathVariable(value="patient_id") String patient_id,
            @ApiParam(name="event_no",value="事件代码",required = true)
            @RequestParam(value="event_no",required = true) String event_no,
            @ApiParam(name="version",value="标准版本")
            @RequestParam(value="version") String version) throws Exception
    {
        try
        {
            String query = "{\"q\":\"org_code:" + org_code + " AND patient_id:" + patient_id + " AND event_no:"+ event_no + "\"}";
            List<Map<String,Object>> resource = patientServie.getEhrResourceVersion(BasisConstant.outpatientCost,query,true,version);

            return getSuccessString("data",resource);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return getErrorString("处理异常，参数不正确");
        }
    }

    /**
     * 返回成功信息
     * @param key 数据标识
     * @param data 数据
     * @return
     * @throws JSONException
     */
    private String getSuccessString(String key,Object data) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("status",0);
        json.put(key,data);

        return json.toString();
    }

    /**
     * 返回错误信息
     * @param msg 返回消息
     * @return
     * @throws JSONException
     */
    private String getErrorString(String msg) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("status",1);
        json.put("msg",msg);

        return json.toString();
    }
}
