package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalDraftEntity;
import com.yihu.ehr.medicalRecord.service.DoctorDraftService;
import com.yihu.ehr.medicalRecord.service.MedicalDraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalDraft", description = "医生对话草稿接口")
public class MedicalDraftController extends EnvelopRestEndPoint {

    @Autowired
    MedicalDraftService mDService;

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDraft,method = RequestMethod.GET)
    @ApiOperation("根据医生ID获取文本草稿")
    public List<MrMedicalDraftEntity> MedicalLabelByRecordId(
            @ApiParam(name="doctorId",value="doctorId",defaultValue = "")
            @RequestParam(value="doctorId") int doctorId,
            @ApiParam(name="type",value="type",defaultValue = "")
            @RequestParam(value="type") String type) throws Exception
    {
        return mDService.getDraftByDoctorId(doctorId, type);
    }
}