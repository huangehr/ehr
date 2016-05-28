package com.yihu.ehr.profile.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.profile.service.CD10Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 13:49
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ICD10关系接口管理", description = "ICD10关系接口管理")
public class IDC10RelationshipController {

    @Autowired
    private CD10Service icd10Service;

    @ApiOperation(value = "根据健康问题代码获取药品信息", notes = "根据健康问题代码获取药品信息")
    @RequestMapping(value = "/drug_dict", method = RequestMethod.GET)
    public List<MDrugDict> getDrugDictList(
            @ApiParam(value = "problem_code", defaultValue = "diabetes")
            @RequestParam("problem_code") String problemCode) throws Throwable {
        return icd10Service.getDrugDictList(problemCode);
    }

    @ApiOperation(value = "根据健康问题代码获取指标信息", notes = "根据健康问题代码获取指标信息")
    @RequestMapping(value = "/indicators_dict", method = RequestMethod.GET)
    public List<MIndicatorsDict> getIndicatorsDictList(
            @ApiParam(value = "problem_code", defaultValue = "diabetes")
            @RequestParam("problem_code") String problemCode) throws Throwable {
        return icd10Service.getIndicatorsDictList(problemCode);
    }

    @ApiOperation(value = "根据健康问题代码获取ICD10代码列表", notes = "根据健康问题代码获取ICD10代码列表")
    @RequestMapping(value = "/icd10_dict", method = RequestMethod.GET)
    public List<MIcd10Dict> getIcd10DictList(
            @ApiParam(value = "problem_code", defaultValue = "diabetes")
            @RequestParam("problem_code") String problemCode) throws Throwable {
        return icd10Service.getIcd10DictList(problemCode);
    }
}