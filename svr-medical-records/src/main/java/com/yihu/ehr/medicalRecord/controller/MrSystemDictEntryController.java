package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrSystemDictEntryEntity;
import com.yihu.ehr.medicalRecord.service.MrSystemDictEntryService;
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
@Api(value = "系统字典项", description = "系统字典项管理")
public class MrSystemDictEntryController extends BaseRestEndPoint {

    @Autowired
    MrSystemDictEntryService mrSystemDictEntryService;

    @ApiOperation("增加系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.POST)
    public boolean addMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictEntry", value = "系统字典项信息")
            @RequestParam(value = "MrSystemDictEntry", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return mrSystemDictEntryService.addMrSystemDictEntry(mrSystemDictEntry);
    }

    @ApiOperation("删除系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.DELETE)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictCode", value = "系统字典代码")
            @RequestParam(value = "MrSystemDictCode", required = true)String DictCode,
            @ApiParam(name = "MrSystemDictEntryCode", value = "系统字典项代码")
            @RequestParam(value = "MrSystemDictEntryCode", required = true)String Code){
        return mrSystemDictEntryService.deleteMrSystemDictEntry(DictCode,Code);
    }

    @ApiOperation("更新系统字典项")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDictEntry, method = RequestMethod.PUT)
    public boolean updateMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictEntry", value = "系统字典项信息")
            @RequestParam(value = "MrSystemDictEntry", required = true) String json){
        MrSystemDictEntryEntity mrSystemDictEntry=toEntity(json,MrSystemDictEntryEntity.class);
        return mrSystemDictEntryService.updataMrSystemDictEntry(mrSystemDictEntry);
    }
}
