package com.yihu.ehr.medicalRecords.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorTemplateEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrLabelClassEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrLabelEntity;
import com.yihu.ehr.medicalRecords.service.DoctorLabelService;
import com.yihu.ehr.medicalRecords.service.DoctorService;
import com.yihu.ehr.medicalRecords.service.DoctorTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "医生相关接口", description = "医生相关接口")
public class DoctorEndPoint extends BaseRestEndPoint {

    @Autowired
    DoctorService doctorService;

    @Autowired
    DoctorLabelService doctorLabelService;

    @Autowired
    DoctorTemplateService dTService;

    @Autowired
    ObjectMapper objectMapper;

    /*********************************************************************************/
    @ApiOperation("获取医生所有病历")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorRecords, method = RequestMethod.GET)
    public List<MedicalRecordDTO> getPatientRecords(
            @ApiParam(name = "doctor_id", value = "医生id")
            @PathVariable(value = "doctor_id") String doctorId,
            @ApiParam(name = "filter", value = "患者信息JSON")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "label", value = "标签列表,逗号分隔")
            @RequestParam(value = "label", required = false) String label,
            @ApiParam(name = "medical_time_from", value = "就诊时间范围开始")
            @RequestParam(value = "medical_time_from", required = false) String medicalTimeFrom,
            @ApiParam(name = "medical_time_end", value = "就诊时间范围结束")
            @RequestParam(value = "medical_time_end", required = false) String medicalTimeEnd,
            @ApiParam(name = "record_type", value = "病历类型 0线上诊断",defaultValue = "0")
            @RequestParam(value = "record_type", required = true) String recordType,
            @ApiParam(name = "page", value = "page")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "size")
            @RequestParam(value = "size", required = false) int size) throws Exception
    {
        return doctorService.getDoctorRecords(filter, label, medicalTimeFrom, medicalTimeEnd, recordType, doctorId, page, size);
    }

    @ApiOperation("获取医生诊断")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorICD10, method = RequestMethod.GET)
    public Map<String,String>  getDoctorDiagnosis(
            @ApiParam(name = "doctor_id", value = "医生ID")
            @PathVariable(value = "doctor_id") String doctorId) throws Exception
    {
        return doctorService.getDoctorDiagnosis(doctorId);
    }

    /********************************* 医生信息 **************************************************/
    @ApiOperation("获取医生信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorInfo, method = RequestMethod.GET)
    public MrDoctorsEntity getDoctorInfo(
            @ApiParam(name = "doctor_id", value = "医生ID")
            @PathVariable(value = "doctor_id") String doctorId) throws Exception
    {
        return doctorService.getDoctor(doctorId);
    }

    @ApiOperation("新增医生信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorInfoManage, method = RequestMethod.POST)
    public boolean addDoctorInfo( @ApiParam(name = "json", value = "医生信息")
                                    @RequestParam(value = "json", required = true) String json) throws Exception
    {
        MrDoctorsEntity doctor = toEntity(json,MrDoctorsEntity.class);
        return doctorService.saveDoctor(doctor);
    }

    @ApiOperation("更新医生信息")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorInfoManage, method = RequestMethod.PUT)
    public boolean updateDoctorInfo(
            @ApiParam(name = "json", value = "医生信息")
            @RequestParam(value = "json", required = true)String json) throws Exception
    {
        MrDoctorsEntity doctor=toEntity(json,MrDoctorsEntity.class);
        return doctorService.saveDoctor(doctor);
    }

    /********************************* 医生标签类别 *********************************************/
    @ApiOperation("获取医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClass, method = RequestMethod.GET)
    public List<MrLabelClassEntity> getDoctorLabelsClass(@ApiParam(name = "doctor_id", value = "医生ID")
                                                         @PathVariable(value = "doctor_id")String doctorId) throws Exception
    {
        return  doctorLabelService.getDoctorLabelsClass(doctorId);
    }

    @ApiOperation("新增医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClassManage, method = RequestMethod.POST)
    public MrLabelClassEntity addDoctorLabelsClass(@ApiParam(name = "json", value = "医生标签类别")
                                                   @RequestParam(value = "json", required = true)String json)throws Exception
    {
        MrLabelClassEntity mrLabelClassEntity = toEntity(json,MrLabelClassEntity.class);
        return  doctorLabelService.addDoctorLabelsClass(mrLabelClassEntity);
    }

    @ApiOperation("更新医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClassManage, method = RequestMethod.PUT)
    public boolean updateDoctorLabelsClass(@ApiParam(name = "json", value = "医生标签类别")
                                           @RequestParam(value = "json", required = true) String json)throws Exception
    {
        MrLabelClassEntity mrLabelClassEntity=toEntity(json,MrLabelClassEntity.class);
        return  doctorLabelService.updateDoctorLabelsClass(mrLabelClassEntity);
    }

    @ApiOperation("删除医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClassManage, method = RequestMethod.DELETE)
    public boolean deleteDoctorLabelsClass(@ApiParam(name = "id", value = "id")
                                           @RequestParam(value = "id", required = true)int id) throws Exception
    {
        return  doctorLabelService.deleteDoctorLabelsClass(id);
    }


    /********************************* 医生标签 *********************************************/
    @ApiOperation("获取医生标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabel, method = RequestMethod.GET)
    public List<MrLabelEntity> getDoctorLabels(@ApiParam(name = "doctor_id", value = "医生ID")
                                               @PathVariable(value = "doctor_id") String doctorId,
                                               @ApiParam(name = "label_type", value = "标签类型",defaultValue = "0")
                                               @RequestParam(value = "label_type", required = true) String labelType,
                                               @ApiParam(name = "label_class", value = "标签类别",defaultValue = "0")
                                               @RequestParam(value = "label_class", required = true) String labelClass) throws Exception
    {
        return doctorLabelService.getDoctorLabels(doctorId, labelType, labelClass);
    }



    /****************************** 医生模板 *******************************************************/
    @ApiOperation("根据医生ID获取模板")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorTemplate,method = RequestMethod.GET)
    public List<MrDoctorTemplateEntity> getTemplate(
            @ApiParam(name="doctor_id",value="医生Id")
            @PathVariable(value = "doctor_id") String doctor_id) throws Exception
    {
        return dTService.getTemplateByDoctorId(doctor_id);
    }

    /**
     * 保存模板操作（先清空，再插入）
     *
     * @param doctor_id
     * @param templateList [{"doctorId":"String"
     *                     ,"code":"String"
     *                     ,"name":"String"
     *                     }]
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorTemplate,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("批量保存模板")
    public boolean saveTemplate(
            @ApiParam(name="doctor_id",value="医生Id")
            @PathVariable(value = "doctor_id") String doctor_id,
            @ApiParam(name="template_list",value="数据元JSON")
            @RequestBody String templateList) throws Exception
    {
        List templateInfoList = objectMapper.readValue(templateList, new TypeReference<List>() {});
        dTService.saveTemplate(doctor_id, templateInfoList);
        return true;
    }
}
