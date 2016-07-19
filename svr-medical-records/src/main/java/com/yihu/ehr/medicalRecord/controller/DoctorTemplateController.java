package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorTemplateEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import com.yihu.ehr.medicalRecord.service.DoctorTemplateService;
import com.yihu.ehr.medicalRecord.service.MedicalRecordService;
import com.yihu.ehr.medicalRecord.service.MedicalReportService;
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
@Api(value = "DoctorTemplate", description = "医生病历模板接口")
public class DoctorTemplateController extends EnvelopRestEndPoint {

    @Autowired
    DoctorTemplateService dTService;

    @RequestMapping(value = ServiceApi.MedicalRecords.DocTmpByDocId,method = RequestMethod.GET)
    @ApiOperation("根据医生ID获取模板")
    public List<MrDoctorTemplateEntity> MedicalLabelByRecordId(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") int id) throws Exception
    {
        return dTService.getTemplateByDoctorId(id);
    }
}