package com.yihu.ehr.medicalRecords.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecords.model.Entity.MrIcd10DictEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrSystemDictEntryEntity;
import com.yihu.ehr.medicalRecords.service.MrIcd10DictService;
import com.yihu.ehr.medicalRecords.service.MrSystemDictEntryService;
import com.yihu.ehr.medicalRecords.service.MrSystemDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/8/3
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "字典服务", description = "字典服务")
public class DictEndPoint extends BaseRestEndPoint {

    @Autowired
    MrSystemDictService systemDictService;

    @Autowired
    MrSystemDictEntryService systemDictEntryService;

    @Autowired
    MrIcd10DictService mrIcd10DictService;

    /**************************** 获取诊断字典ICD10 ******************************************/
    @ApiOperation("获取诊断字典ICD10")
    @RequestMapping(value = ServiceApi.MedicalRecords.ICD10Dict, method = RequestMethod.GET)
    public List<MrIcd10DictEntity> getPatientDiagnosis(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = true) String filter,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = true) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = true) Integer size) throws Exception{
        return mrIcd10DictService.getIcd10List(filter,page,size);
    }


    /**************************** 系统字典 ***************************************************/
    @ApiOperation("增加系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.POST)
    public boolean addMrSystemDict(
            @ApiParam(name = "json", value = "系统字典信息")
            @RequestParam(value = "json", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return systemDictService.addMrSystemDict(mrSystemDictEntity);
    }

    @ApiOperation("删除系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.DELETE)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "dict_code", value = "系统字典代码")
            @RequestParam(value = "dict_code", required = true)String dictCode){
        return systemDictService.deleteMrSystemDict(dictCode);
    }

    @ApiOperation("更新系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.PUT)
    public boolean updateMrSystemDict(
            @ApiParam(name = "json", value = "系统字典信息")
            @RequestParam(value = "json", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return systemDictService.updateMrSystemDict(mrSystemDictEntity);
    }

    /****************************** 系统字典项 *************************************************/
    @ApiOperation("增加系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.POST)
    public boolean addMrSystemDictEntry(
            @ApiParam(name = "json", value = "系统字典项信息")
            @RequestParam(value = "json", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return systemDictEntryService.addMrSystemDictEntry(mrSystemDictEntry);
    }

    @ApiOperation("删除系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.DELETE)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "dict_code", value = "系统字典代码")
            @RequestParam(value = "dict_code", required = true)String DictCode,
            @ApiParam(name = "dict_entry_code", value = "系统字典项代码")
            @RequestParam(value = "dict_entry_code", required = true)String Code){
        return systemDictEntryService.deleteMrSystemDictEntry(DictCode,Code);
    }

    @ApiOperation("查询字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.GET)
    public List<MrSystemDictEntryEntity> searchSystemDictEntry(
            @ApiParam(name = "dict_code", value = "字典代码")
            @RequestParam(value = "dict_code", required = true) String dictCode)throws  Exception{
        return systemDictEntryService.getDict(dictCode);
    }

    @ApiOperation("更新系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.PUT)
    public boolean updateMrSystemDictEntry(
            @ApiParam(name = "json", value = "系统字典项信息")
            @RequestParam(value = "json", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return systemDictEntryService.updataMrSystemDictEntry(mrSystemDictEntry);
    }
}
