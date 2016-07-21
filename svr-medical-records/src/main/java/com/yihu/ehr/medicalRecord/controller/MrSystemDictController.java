package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
import com.yihu.ehr.medicalRecord.model.MrSystemDictEntity;
import com.yihu.ehr.medicalRecord.service.MrSystemDictService;
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
@Api(value = "系统字典", description = "系统字典管理")
public class MrSystemDictController extends BaseRestEndPoint {

    @Autowired
    MrSystemDictService mrSystemDictService;

    @ApiOperation("增加系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.POST)
    public boolean addMrSystemDict(
            @ApiParam(name = "SystemDict", value = "系统字典信息")
            @RequestParam(value = "SystemDict", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return mrSystemDictService.addMrSystemDict(mrSystemDictEntity);
    }

    @ApiOperation("删除系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.GET)
    public boolean deleteMrSystemDictEntry(
            @ApiParam(name = "MrSystemDictCode", value = "系统字典代码")
            @RequestParam(value = "MrSystemDictCode", required = true)String dictCode){
        return mrSystemDictService.deleteMrSystemDict(dictCode);
    }

    @ApiOperation("更新系统字典")
    @RequestMapping(value = ServiceApi.MedicalRecords.SystemDict, method = RequestMethod.PUT)
    public boolean updateMrSystemDict(
            @ApiParam(name = "SystemDict", value = "系统字典信息")
            @RequestParam(value = "SystemDict", required = true) String json){
        MrSystemDictEntity mrSystemDictEntity=toEntity(json,MrSystemDictEntity.class);
        return mrSystemDictService.updateMrSystemDict(mrSystemDictEntity);
    }
}
