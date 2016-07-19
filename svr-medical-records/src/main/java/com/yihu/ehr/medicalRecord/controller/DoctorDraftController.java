package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrDoctorTemplateEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import com.yihu.ehr.medicalRecord.service.DoctorDraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "DoctorDraft", description = "医生文本草稿接口")
public class DoctorDraftController extends EnvelopRestEndPoint {

    @Autowired
    DoctorDraftService dDService;

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public MrDoctorDraftEntity saveDoctorDraft(
            @ApiParam(name="doctorDraft",value="数据元JSON",defaultValue = "")
            @RequestBody String doctorDraft) throws Exception
    {
        MrDoctorDraftEntity mrDoctorDraft = toEntity(doctorDraft,MrDoctorDraftEntity.class);

        mrDoctorDraft.setUsageCount(1);
        mrDoctorDraft = dDService.saveDoctorDraft(mrDoctorDraft);
        return convertToModel(mrDoctorDraft,MrDoctorDraftEntity.class);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.PUT)
    @ApiOperation("更新使用次数")
    public MrDoctorDraftEntity updateDoctorDraft(
            @ApiParam(name="id",value="id",defaultValue = "")
            @RequestParam(value="id") int id) throws Exception
    {
        return dDService.updateDraftBy(id);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.GET)
    @ApiOperation("根据医生ID获取文本草稿")
    public List<MrDoctorDraftEntity> MedicalLabelByRecordId(
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId") int doctorId,
            @ApiParam(name="type",value="type",defaultValue = "")
            @RequestParam(value="type") String type) throws Exception
    {
        return dDService.getDraftByDoctorId(doctorId, type);
    }
}