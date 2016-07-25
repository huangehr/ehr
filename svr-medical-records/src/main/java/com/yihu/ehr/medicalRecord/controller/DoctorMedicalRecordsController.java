package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorMedicalRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "DoctorMedicalRecords", description = "医生病历关联接口")
public class DoctorMedicalRecordsController extends EnvelopRestEndPoint {

    @Autowired
    DoctorMedicalRecordService dMRService;

    /**
     * 新增医生病历关联
     *
     * @param  doctorMedicalRecords { "doctorId": "string",
                                      "isCreator": "string", 0:非创建者， 1：创建者
                                      "recordId": 0,
                                      "recordType": "string" 0:线上诊断， 1：线下诊断， 2：用户上传
                                    }
     * @return MrDoctorMedicalRecordsEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("建立医生病历关联")
    public MrDoctorMedicalRecordsEntity saveRelation(
            @ApiParam(name="doctorMedicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String doctorMedicalRecords) throws Exception
    {
        MrDoctorMedicalRecordsEntity doctorMedicalRecord = toEntity(doctorMedicalRecords,MrDoctorMedicalRecordsEntity.class);

        doctorMedicalRecord = dMRService.saveDoctorMedicalRecord(doctorMedicalRecord);
        return convertToModel(doctorMedicalRecord,MrDoctorMedicalRecordsEntity.class);
    }

    /**
     * 获取该医生所有有关联的病历信息
     *
     * @param doctorId
     * @param doctorId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.GET)
    @ApiOperation("获取该医生所有有关联的病历")
    public List<MrDoctorMedicalRecordsEntity> getRelationInfo(
            @ApiParam(name="doctor_id",value="医生Id",defaultValue = "")
            @PathVariable(value="doctor_id") String doctorId) throws Exception
    {
        return dMRService.getInfoByDocIdrecId(doctorId);
    }

    /**
     * 查看与病历关联的医生Id
     *
     * @param recordId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecord,method = RequestMethod.GET)
    @ApiOperation("查看与病历关联的医生Id")
    public List<MrDoctorMedicalRecordsEntity> getDoctorInfo(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @PathVariable(value="record_id") int recordId) throws Exception
    {
        return dMRService.getDoctorInfoByRecordId(recordId);
    }

    /**
     * （创建者）删除病历时，清除所有的医生-病历关联
     *
     * @param recordId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecord,method = RequestMethod.DELETE)
    @ApiOperation("数据元全删除")
    public boolean deleteRelations(
            @ApiParam(name="record_id",value="病历ID",defaultValue = "")
            @PathVariable(value="record_id")int recordId) throws Exception
    {
        dMRService.deleteRecords(recordId);
        return true;
    }

    /**
     * 取消医生-病历关联（非创建者）
     *
     * @param doctorId
     * @param recordId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorMedicalRecords,method = RequestMethod.DELETE)
    @ApiOperation("医生病历取消关联")
    public boolean cancelRelation(
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId") String doctorId,
            @ApiParam(name="recordId",value="recordId",defaultValue = "")
            @RequestParam(value="recordId") int recordId) throws Exception
    {
        dMRService.deleteRecordRelation(doctorId,recordId);
        return true;
    }
}