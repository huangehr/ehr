package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalDrugEntity;
import com.yihu.ehr.medicalRecord.service.MedicalDrugService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shine on 2016/7/19.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "用药信息", description = "用药信息管理")
public class MedicalDrugController extends BaseRestEndPoint {
    @Autowired
    MedicalDrugService medicalDrugService;

    @ApiOperation("获取用药信息")
    @RequestMapping(value = "/medical_drug", method = RequestMethod.GET)
    public List<MrMedicalDrugEntity> getMedicalDrugInformationByRecordsId(
            @ApiParam(name = "recordsId", value = "病历号")
            @RequestParam(value = "recordsId", required = true)int recordsId){
            return medicalDrugService.getMedicalDrugInformationByRecordsId(recordsId);
    }

    @ApiOperation("提交用药信息")
    @RequestMapping(value = "/medical_drug", method = RequestMethod.POST)
    public boolean postMedicalDrugInformationByRecordsId(
            @ApiParam(name = "MedicalDrug", value = "药品集")
            @RequestParam(value = "MedicalDrug", required = true)String MedicalDrugs){
        List<Map<String,String>>tmp=toEntity(MedicalDrugs,List.class);
        List<MrMedicalDrugEntity>m=new ArrayList<>();
        for(int i=0;i<tmp.size();i++){
            if(tmp.get(i)!=null) {
                MrMedicalDrugEntity mrMedicalDrugEntity = new MrMedicalDrugEntity();
                mrMedicalDrugEntity.setId(Integer.parseInt(tmp.get(i).get("id")));
                mrMedicalDrugEntity.setRecordsId(Integer.parseInt(tmp.get(i).get("recordsId")));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugName"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugSpecifications"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugUse"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugQuantity"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugDosage"));
                mrMedicalDrugEntity.setDrugName(tmp.get(i).get("drugFrequency"));
                m.add(mrMedicalDrugEntity);
            }
        }
        return  medicalDrugService.postMedicalDrug(m);
    }


}
