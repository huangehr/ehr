package com.yihu.ehr.medicalRecords.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalDrugEntity;
import com.yihu.ehr.medicalRecords.service.MedicalDrugService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/19.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "用药信息", description = "用药信息管理")
public class MedicalDrugEndPoint extends BaseRestEndPoint {
    @Autowired
    MedicalDrugService medicalDrugService;

    @ApiOperation("获取用药信息")
    @RequestMapping(value =  ServiceApi.MedicalRecords.MedicalDrug, method = RequestMethod.GET)
    public List<MrMedicalDrugEntity> getMedicalDrug(
            @ApiParam(name = "record_id", value = "病历号",defaultValue = "1")
            @PathVariable(value = "record_id")String recordId) throws Exception
    {
            return medicalDrugService.getMedicalDrug(recordId);
    }

    @ApiOperation("导入用药信息")
    @RequestMapping(value =  ServiceApi.MedicalRecords.ImportMedicalPrescription, method = RequestMethod.POST)
    public boolean importMedicalPrescription(
            @ApiParam(name = "record_id", value = "病历号",defaultValue = "1")
            @PathVariable(value = "record_id")String recordId,
            @ApiParam(name = "json", value = "药品集合",defaultValue = "[{ \"drugName\": \"药品名称1\", \"drugSpecifications\": \"规格\", \"drugUse\": \"用法\", \"drugQuantity\": 1, \"drugUnit\": \"单位\", \"drugDosage\": \"用量\", \"drugFrequency\": \"频次\" },{ \"drugName\": \"药品名称2\", \"drugSpecifications\": \"规格\", \"drugUse\": \"用法\", \"drugQuantity\": 1, \"drugUnit\": \"单位\", \"drugDosage\": \"用量\", \"drugFrequency\": \"频次\" }]")
            @RequestParam(name = "json",required = true) String json) throws Exception
    {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, MrMedicalDrugEntity.class);
        List<MrMedicalDrugEntity> list = objectMapper.readValue(json,javaType);
        if(list!=null)
        {
            for(MrMedicalDrugEntity item:list)
            {
                item.setRecordId(recordId);
            }
        }
        return  medicalDrugService.importMedicalPrescription(recordId,list);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDrug,method = RequestMethod.POST)
    @ApiOperation("保存病历用药记录")
    public boolean saveMedicalDrug(
            @ApiParam(name="record_id",value="病历id",defaultValue = "1")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="json",value="用药记录JSON",defaultValue = "{ \"id\": 1, \"recordId\": \"1\", \"drugName\": \"药品名称\", \"drugSpecifications\": \"规格\", \"drugUse\": \"用法\", \"drugQuantity\": 1, \"drugUnit\": \"单位\", \"drugDosage\": \"用量\", \"drugFrequency\": \"频次\" }")
            @RequestParam(name = "json",required = true) String json) throws Exception
    {
        MrMedicalDrugEntity obj = toEntity(json,MrMedicalDrugEntity.class);
        if(obj!=null && obj.getRecordId()==null)
        {
            obj.setRecordId(recordId);
        }
        return  medicalDrugService.saveMedicalDrug(recordId,obj);
    }


    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDrug,method = RequestMethod.DELETE)
    @ApiOperation("删除病历用药记录")
    public boolean deleteMedicalDrug(
            @ApiParam(name="record_id",value="病历id",defaultValue = "1")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="drug_id",value="用药id",defaultValue = "1")
            @RequestParam(value="drug_id",required = true) String drugId) throws Exception
    {
        return  medicalDrugService.deleteMedicalDrug(recordId,drugId);
    }
}
