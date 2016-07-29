package com.yihu.ehr.medicalRecord.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorTemplateEntity;
import com.yihu.ehr.medicalRecord.service.DoctorTemplateService;
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
@Api(value = "DoctorTemplate", description = "医生病历模板接口")
public class DoctorTemplateController extends EnvelopRestEndPoint {

    @Autowired
    DoctorTemplateService dTService;

    /**
     * 根据医生ID获取模板
     *
     * @param  doctor_id
     * @return List<MrDoctorTemplateEntity>
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorTemplate,method = RequestMethod.GET)
    @ApiOperation("根据医生ID获取模板")
    public List<MrDoctorTemplateEntity> getTemplate(
            @ApiParam(name="doctorId",value="医生Id",defaultValue = "")
            @PathVariable(value = "doctor_id") String doctor_id) throws Exception
    {
        return dTService.getTemplateByDoctorId(doctor_id);
    }

    /**
     * 保存模板操作（先清空，再插入）
     *
     * @param doctor_id
     * @param templateInfo [{"doctorId":"String"
     *                     ,"code":"String"
     *                     ,"name":"String"
     *                     }]
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DcotorTemplateManage,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("批量保存模板")
    public boolean saveTemplate(
            @ApiParam(name="doctor_id",value="医生Id",defaultValue = "")
            @PathVariable(value = "doctor_id") String doctor_id,
            @ApiParam(name="templateInfo",value="数据元JSON",defaultValue = "")
            @RequestBody String templateInfo) throws Exception
    {
        List templateInfoList = objectMapper.readValue(templateInfo, new TypeReference<List>() {});
        dTService.saveTemplate(doctor_id, templateInfoList);
        return true;
    }
}