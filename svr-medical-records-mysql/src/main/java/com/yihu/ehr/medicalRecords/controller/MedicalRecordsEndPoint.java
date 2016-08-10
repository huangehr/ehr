package com.yihu.ehr.medicalRecords.controller;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalInfoEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.service.MedicalInfoService;
import com.yihu.ehr.medicalRecords.service.MedicalLabelService;
import com.yihu.ehr.medicalRecords.service.MedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalRecords", description = "病历服务接口")
public class MedicalRecordsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    MedicalRecordService recordService;

    @Autowired
    MedicalInfoService medicalInfoService;

    @Autowired
    MedicalLabelService medicalLabelService;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     *
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemAccess,method = RequestMethod.GET)
    @ApiOperation("系统接入接口")
    public MrMedicalRecordsEntity systemAccess(
            @ApiParam(name="patient_id",value="病人ID",defaultValue = "U20160322000004")
            @RequestParam(value="patient_id",required = true)String patientId,
            @ApiParam(name="doctor_id",value="医生ID",defaultValue = "D20160322000001")
            @RequestParam(value="doctor_id",required = true)String userId,
            @ApiParam(name="json",value="校验json",defaultValue = "{\"id\":\"1\",\"uid\":\"D20160322000001\",\"imei\":\"864394010501239\",\"token\":\"7e124976b092dd17662fe228e5b63172\",\"platform\":\"4\"}")
            @RequestParam(value="json",required = true)String json) throws Exception
    {
        try {
            return recordService.systemAccess(patientId, userId, json);
        }
        catch (Exception ex)
        {
            Message.error(ex.getMessage());
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.AddRecord,method = RequestMethod.POST)
    @ApiOperation("新增病历")
    public MrMedicalRecordsEntity addRecord(
            @ApiParam(name="doctor_id",value="医生ID",defaultValue = "D20160322000001")
            @RequestParam(value="doctor_id",required = true)String doctorId,
            @ApiParam(name="patient_id",value="患者ID",defaultValue = "350524199208115544")
            @RequestParam(value="patient_id",required = true)String patientId,
            @ApiParam(name="first_record_id",value="首诊病历ID",defaultValue = "1")
            @RequestParam(value="first_record_id",required = false)String firstRecordId) throws Exception
    {
        return recordService.addRecord(doctorId, patientId,firstRecordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.PUT)
    @ApiOperation("修改病历")
    public boolean editRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @PathVariable(value="record_id")String recordId,
            @ApiParam(name="json",value="修改内容",defaultValue = "{\"medicalSuggest\":\"修改治疗建议\"}")
            @RequestParam(value="json",required = true)String json) throws Exception
    {
        Map<String,String> map = toEntity(json,Map.class);
        return recordService.editRecord(recordId, map);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.DELETE)
    @ApiOperation("删除病历")
    public boolean deleteRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @PathVariable(value="record_id")String recordId) throws Exception
    {
        return recordService.deleteRecord(recordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.GET)
    @ApiOperation("获取病历")
    public MrMedicalRecordsEntity getMedicalRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @PathVariable(value="record_id")String recordId) throws Exception
    {
        return recordService.getMedicalRecord(recordId);
    }


    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalShare,method = RequestMethod.GET)
    @ApiOperation("病历分享")
    public boolean shareRecord(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="patient_id",value="患者ID",defaultValue = "350524199208115544")
            @RequestParam(value="patient_id",required = true)String patientId,
            @ApiParam(name="doctor_id",value="医生ID")
            @RequestParam(value="doctor_id",required = true)String doctorId) throws Exception
    {
        return recordService.shareRecord(recordId,patientId,doctorId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordRelated,method = RequestMethod.GET)
    @ApiOperation("获取关联病历")
    public List<MrMedicalRecordsEntity> getMedicalRecordRelated(
            @ApiParam(name="first_record_id",value="首诊病历ID",defaultValue = "1")
            @RequestParam(value="first_record_id",required = true)String firstRecordId) throws Exception
    {
        return recordService.getMedicalRecordRelated(firstRecordId);
    }

    /******************************* 病情信息 ************************************************/
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalInfo,method = RequestMethod.POST)
    @ApiOperation("病情保存")
    public boolean saveMedicalInfo(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="json",value="病情列表",defaultValue = "[{\"code\":\"patient_condition\",\"name\":\"病情主诉\",\"value\":\"病情主诉内容\"},{\"code\":\"patient_history_now\",\"name\":\"现病史\",\"value\":\"现病史内容\"}]")
            @RequestParam(value="json",required = true)String json) throws Exception
    {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, MrMedicalInfoEntity.class);
        List<MrMedicalInfoEntity> list = objectMapper.readValue(json,javaType);
        if(list!=null)
        {
            for(MrMedicalInfoEntity item:list)
            {
                item.setRecordId(recordId);
            }
        }
        return medicalInfoService.saveMedicalInfo(recordId, list);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalInfo,method = RequestMethod.GET)
    @ApiOperation("获取病情")
    public List<MrMedicalInfoEntity> getMedicalInfo(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @RequestParam(value="record_id",required = true)String recordId) throws Exception
    {
        return medicalInfoService.getMedicalInfo(recordId);
    }

    /******************************* 病历标签 *****************************************************/
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabel,method = RequestMethod.GET)
    @ApiOperation("获取病历标签")
    public List<MrMedicalLabelEntity> getMedicalLabelByRecordId(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @PathVariable(value="record_id")String recordId) throws Exception
    {
        return medicalLabelService.getMedicalLabelByRecordId(recordId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabel,method = RequestMethod.POST)
    @ApiOperation("批量保存病历标签")
    public boolean saveMedicalLabel(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "1")
            @RequestParam(value="record_id",required = true)String recordId,
            @ApiParam(name="doctor_id",value="医生ID",defaultValue = "D20160322000001")
            @RequestParam(value="doctor_id",required = true)String doctorId,
            @ApiParam(name="labels",value="标签数组",defaultValue = "医生标签1,测试标签2")
            @RequestParam(value="labels",required = true)String[] labels) throws Exception
    {
        return medicalLabelService.saveMedicalLabel(recordId,doctorId,labels);
    }

    @RequestMapping(value = "/medicalRecords/record/label/{id}",method = RequestMethod.DELETE)
    @ApiOperation("根据病历标签关系id删除病历标签")
    public boolean deleteMedicalLabelById(
            @ApiParam(name="id",value="病历标签关系id")
            @PathVariable(value="id")Integer id) throws Exception
    {
        return medicalLabelService.deleteMedicalLabelById(id);
    }



}