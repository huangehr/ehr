package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.Entity.MrSystemDictEntity;
import com.yihu.ehr.medicalRecord.model.Entity.MrSystemDictEntryEntity;
import com.yihu.ehr.medicalRecord.service.MrSystemDictEntryService;
import com.yihu.ehr.medicalRecord.service.MrSystemDictService;
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
 * Created by hzp on 2016/7/29.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "字典服务", description = "字典服务")
public class DictEndPoint extends BaseRestEndPoint {

    @Autowired
    MrSystemDictService systemDictService;

    @Autowired
    MrSystemDictEntryService systemDictEntryService;

    /**************************** 获取诊断字典ICD10 ******************************************/
    @ApiOperation("获取诊断字典ICD10")
    @RequestMapping(value = ServiceApi.MedicalRecords.ICD10Dict, method = RequestMethod.GET)
    public Map<String,String> getPatientDiagnosis(
            @ApiParam(name = "filter", value = "过滤")
            @RequestParam(value = "filter", required = true) String filter) throws Exception{
        return null;
    }


    /**************************** 系统字典 ***************************************************/
    @ApiOperation("增加系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.POST)
    public boolean addMrSystemDict(
            @ApiParam(name = "SystemDict", value = "系统字典信息")
            @RequestParam(value = "SystemDict", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return systemDictService.addMrSystemDict(mrSystemDictEntity);
    }

    @ApiOperation("删除系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.DELETE)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictCode", value = "系统字典代码")
            @RequestParam(value = "MrSystemDictCode", required = true)String dictCode){
        return systemDictService.deleteMrSystemDict(dictCode);
    }

    @ApiOperation("更新系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.PUT)
    public boolean updateMrSystemDict(
            @ApiParam(name = "SystemDict", value = "系统字典信息")
            @RequestParam(value = "SystemDict", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return systemDictService.updateMrSystemDict(mrSystemDictEntity);
    }

    /****************************** 系统字典项 *************************************************/
    @ApiOperation("增加系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.POST)
    public boolean addMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictEntry", value = "系统字典项信息")
            @RequestParam(value = "MrSystemDictEntry", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return systemDictEntryService.addMrSystemDictEntry(mrSystemDictEntry);
    }

    @ApiOperation("删除系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.DELETE)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictCode", value = "系统字典代码")
            @RequestParam(value = "MrSystemDictCode", required = true)String DictCode,
            @ApiParam(name = "MrSystemDictEntryCode", value = "系统字典项代码")
            @RequestParam(value = "MrSystemDictEntryCode", required = true)String Code){
        return systemDictEntryService.deleteMrSystemDictEntry(DictCode,Code);
    }

    @ApiOperation("查询字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.GET)
    public List<MrSystemDictEntryEntity> searchSystemDictEntry(
            @ApiParam(name = "filter", value = "过滤")
            @RequestParam(value = "filter", required = true) String filter)throws  Exception{
        return systemDictEntryService.searchSystemDictEntry(filter);
    }

    @ApiOperation("更新系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.PUT)
    public boolean updateMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictEntry", value = "系统字典项信息")
            @RequestParam(value = "MrSystemDictEntry", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return systemDictEntryService.updataMrSystemDictEntry(mrSystemDictEntry);
    }
}
