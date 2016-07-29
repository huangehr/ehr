package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorMedicalRecordService;
import com.yihu.ehr.medicalRecord.service.MedicalRecordService;
import com.yihu.ehr.medicalRecord.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalRecords", description = "病历服务接口")
public class MedicalRecordsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    MedicalRecordService mRService;
    @Autowired
    PatientService patientService;
    @Autowired
    DoctorMedicalRecordService dMRService;

    /**
     * 新建病历，同时建立医生病历关联
     *
     * @param recordType 0:线上诊断，1: 线下诊断 2:用户上传
     * @param medicalRecord {"doctorId": "string",
                             "firstRecordId": int,（仅限复诊）
                             "medicalDiagnosis": "string",
                             "medicalDiagnosisCode": "string",
                             "medicalSuggest": "string",
                             "patientAllergy": "string",
                             "patientCondition": "string",
                             "patientHistoryFamily": "string",
                             "patientHistoryNow": "string",
                             "patientHistoryPast": "string",
                             "patientId": "String",
                             "patientPhysical": "string"
                            }
     * @return MrMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新建病历")
    public MrMedicalRecordsEntity saveRecord(
            @ApiParam(name="recordType",value="recordType",defaultValue = "")
            @RequestParam(value="recordType",required = false)String recordType,
            @ApiParam(name="medicalRecord",value="病历JSON",defaultValue = "")
            @RequestBody String medicalRecord) throws Exception
    {
        MrMedicalRecordsEntity mrMedicalRecord = toEntity(medicalRecord,MrMedicalRecordsEntity.class);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        mrMedicalRecord.setMedicalTime(t);

        mrMedicalRecord = mRService.saveMedicalRecord(mrMedicalRecord);

        MrDoctorMedicalRecordsEntity drRelation = new MrDoctorMedicalRecordsEntity();
        drRelation.setRecordId(String.valueOf(mrMedicalRecord.getId()));
        drRelation.setDoctorId(mrMedicalRecord.getDoctorId());
        drRelation.setIsCreator("1");  //0:非创建者，1:创建者
        drRelation.setRecordType(recordType);
        dMRService.saveDoctorMedicalRecord(drRelation);

        return convertToModel(mrMedicalRecord,MrMedicalRecordsEntity.class);
    }

    /**
     * 更新病历
     *
     * @param medicalRecord {"doctorId": "string",
                             "firstRecordId": int,（仅限复诊）
                             "medicalDiagnosis": "string",
                             "medicalDiagnosisCode": "string",
                             "medicalSuggest": "string",
                             "patientAllergy": "string",
                             "patientCondition": "string",
                             "patientHistoryFamily": "string",
                             "patientHistoryNow": "string",
                             "patientHistoryPast": "string",
                             "patientId": int,
                             "patientPhysical": "string"
                            }
     * @return MrMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecord,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新数据元")
    public MrMedicalRecordsEntity updateRecord(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalRecord) throws Exception
    {
        MrMedicalRecordsEntity mrMedicalRecord = toEntity(medicalRecord,MrMedicalRecordsEntity.class);
        mrMedicalRecord = mRService.saveMedicalRecord(mrMedicalRecord);
        return convertToModel(mrMedicalRecord,MrMedicalRecordsEntity.class);
    }

    /**
     * 获取最近一次病历
     *
     * @param patientId    病人Id
     * @param doctorId     医生Id
     * @return MrMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordByLastOne,method = RequestMethod.GET)
    @ApiOperation("根据医生ID和病人ID获取最近的一次病历")
    public MrMedicalRecordsEntity getRecordByLastOne(
            @ApiParam(name="patientId",value="patientId",defaultValue = "")
            @RequestParam(value="patientId",required = false)String patientId,
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId",required = false)String doctorId) throws Exception
    {

        return mRService.getRecordByLastOne(patientId, doctorId);
    }

    /**
     * 根据病历Id获取病历
     *
     * @param id 病历Id
     * @return MrMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordId,method = RequestMethod.GET)
    @ApiOperation("根据ID获取病历")
    public MrMedicalRecordsEntity getRecords(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")int id) throws Exception
    {
        return mRService.getMedicalRecord(id);
    }

    /**
     * 根据病历Id删除病历，同时清除与该病历有关的关系表中数据
     *
     * @param id 病历Id
     * @return true
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordId,method = RequestMethod.DELETE)
    @ApiOperation("删除数据元")
    public boolean deleteRecord(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")int id) throws Exception
    {
        mRService.deleteRecord(id);
        dMRService.deleteRecords(id);
        return true;
    }

    /**
     * 拷贝病历
     *
     * @param patientId  病人Id
     * @param doctorId   医生Id
     * @param id         病历Id
     * @param recordType 病历类型
     * @param firstRecordId 初诊id
     * @return MrMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordCopy,method = RequestMethod.GET)
    @ApiOperation("病历拷贝")
    public MrMedicalRecordsEntity copyRecord(
            @ApiParam(name="patientId",value="病人Id",defaultValue = "")
            @RequestParam(value="patientId",required = false)String patientId,
            @ApiParam(name="doctorId",value="医生Id",defaultValue = "")
            @RequestParam(value="doctorId",required = false)String doctorId,
            @ApiParam(name="id",value="病历Id",defaultValue = "")
            @RequestParam(value="id",required = false)int id,
            @ApiParam(name="recordType",value="病历类型",defaultValue = "")
            @RequestParam(value="recordType",required = false)String recordType,
            @ApiParam(name="firstRecordId",value="firstRecordId",defaultValue = "")
            @RequestParam(value="firstRecordId",required = false)Integer firstRecordId) throws Exception
    {

        MrMedicalRecordsEntity mrMedicalRecord = mRService.copyRecord(patientId, doctorId, id, firstRecordId);

        MrDoctorMedicalRecordsEntity drRelation = new MrDoctorMedicalRecordsEntity();
        drRelation.setRecordId(String.valueOf(mrMedicalRecord.getId()));
        drRelation.setDoctorId(mrMedicalRecord.getDoctorId());
        drRelation.setIsCreator("1");  //0:非创建者，1:创建者
        drRelation.setRecordType(recordType);
        dMRService.saveDoctorMedicalRecord(drRelation);

        return convertToModel(mrMedicalRecord,MrMedicalRecordsEntity.class);
    }

    /**
     * 根据患者Id获取病历
     *
     * @param patientId 患者id
     * @return List<MrMedicalRecordsEntity>
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordPatId,method = RequestMethod.GET)
    @ApiOperation("根据患者ID获取病历")
    public List<Map> getRecordsByPatientId(
            @ApiParam(name="patient_id",value="医生id",defaultValue = "")
            @PathVariable(value="patient_id")String patientId) throws Exception
    {
        return mRService.getRecordsBypatId(patientId);
    }

    /**
     * 根据医生Id获取病历一览表(医生病历库)
     *
     * @param doctorId 医生id
     * @return List<Map>
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordDocId,method = RequestMethod.GET)
    @ApiOperation("根据医生ID医生病历库")
    public List<Map> getRecordList(
            @ApiParam(name="doctor_id",value="医生id",defaultValue = "")
            @PathVariable(value="doctor_id")String doctorId) throws Exception
    {
        return mRService.getListBydocId(doctorId);
    }
}