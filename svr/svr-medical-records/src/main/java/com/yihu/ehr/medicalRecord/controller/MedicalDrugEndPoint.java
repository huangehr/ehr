package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalDrug;
import com.yihu.ehr.medicalRecord.service.MedicalDrugService;
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
 * Created by shine on 2016/7/19.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "用药信息", description = "用药信息管理")
public class MedicalDrugEndPoint extends BaseRestEndPoint {
    @Autowired
    MedicalDrugService medicalDrugService;

    @ApiOperation("获取用药信息")
    @RequestMapping(value =  ServiceApi.MedicalRecords.MedicalDrug, method = RequestMethod.GET)
    public List<MedicalDrug> getMedicalDrug(
            @ApiParam(name = "record_id", value = "病历号")
            @PathVariable(value = "record_id")String recordId) throws Exception
    {
            return medicalDrugService.getMedicalDrug(recordId);
    }

    @ApiOperation("导入用药信息")
    @RequestMapping(value =  ServiceApi.MedicalRecords.ImportMedicalPrescription, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean importMedicalPrescription(
            @ApiParam(name = "record_id", value = "病历号")
            @PathVariable(value = "record_id")String recordId,
            @ApiParam(name = "json", value = "药品集合")
            @RequestBody String json) throws Exception
    {
        List<Map<String,String>>tmp=toEntity(json,List.class);
        List<MedicalDrug> list = new ArrayList<>();
        for(int i=0;i<tmp.size();i++){
            if(tmp.get(i)!=null && tmp.get(i).get("recordsId")!=null) {
                MedicalDrug mrMedicalDrugEntity = new MedicalDrug();
                mrMedicalDrugEntity.setRecordRowkey(recordId);
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugName"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugSpecifications"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugUse"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugQuantity"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugDosage"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugFrequency"));
                list.add(mrMedicalDrugEntity);
            }
        }
        return  medicalDrugService.importMedicalPrescription(list);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDrug,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存病历用药记录")
    public boolean saveMedicalDrug(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="json",value="用药记录JSON")
            @RequestBody String json) throws Exception
    {
        MedicalDrug obj = toEntity(json,MedicalDrug.class);
        return  medicalDrugService.saveMedicalDrug(recordId,obj);
    }


    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDrug,method = RequestMethod.DELETE)
    @ApiOperation("删除病历用药记录")
    public boolean deleteMedicalDrug(
            @ApiParam(name="record_id",value="病历id")
            @PathVariable(value="record_id") String recordId,
            @ApiParam(name="drug_id",value="用药id")
            @RequestParam(value="drug_id",required = true) String drugId) throws Exception
    {
        return  medicalDrugService.deleteMedicalDrug(recordId,drugId);
    }
}
