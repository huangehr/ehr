package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.medicalRecord.service.MrDiagnosisDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "诊断字典", description = "诊断字典管理")
public class MrDiagnosisDictController extends BaseRestEndPoint {

    @Autowired
    MrDiagnosisDictService mrDiagnosisDictService;

    @ApiOperation("增加诊断字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.DiagnosisDict, method = RequestMethod.POST)
    public boolean addMrDiagnosisDict(
            @ApiParam(name = "DiagnosisDictInformation", value = "诊段字典信息")
            @RequestParam(value = "DiagnosisDictInformation", required = true) String json){
        MrDiagnosisDictEntity mrDiagnosisDict=toEntity(json,MrDiagnosisDictEntity.class);
        return mrDiagnosisDictService.addMrDiagnosisDict(mrDiagnosisDict);
    }

    @ApiOperation("删除诊断字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.DiagnosisDict, method = RequestMethod.GET)
    public boolean deleteMrDiagnosisDict(
            @ApiParam(name = "MrDiagnosisDictCode", value = "诊段字典代码")
            @RequestParam(value = "MrDiagnosisDictCode", required = true) String code){
        return mrDiagnosisDictService.deleteMrDiagnosisDict(code);
    }

    @ApiOperation("更新诊断字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.DiagnosisDict, method = RequestMethod.POST)
    public boolean updateMrDiagnosisDict(
            @ApiParam(name = "DiagnosisDictInformation", value = "诊段字典信息")
            @RequestParam(value = "DiagnosisDictInformation", required = true) String json)throws Exception{
        //MrDiagnosisDictEntity mrDiagnosisDict=toEntity(json,MrDiagnosisDictEntity.class);
        MrDiagnosisDictEntity mrDiagnosisDict=objectMapper.readValue(json, MrDiagnosisDictEntity.class);
        return mrDiagnosisDictService.updateMrDiagnosisDict(mrDiagnosisDict);
    }
}
