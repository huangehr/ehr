package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecord.service.MedicalLabelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "病历标签", description = "病历标签管理")
public class MedicalLabelController extends BaseRestEndPoint {
    @Autowired
    MedicalLabelService medicalLabelService;

    @ApiOperation("批量保存病历标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabels, method = RequestMethod.POST)
    public boolean addMedicalLabels(@ApiParam(name = "MedicalLabels", value = "病历标签集")
                                    @RequestParam(value = "MedicalLabels", required = true)String MedicalLabels){
        List<Map<String,String>>tmp=toEntity(MedicalLabels,List.class);
        List<MrMedicalLabelEntity>m=new ArrayList<>();
        for(int i=0;i<tmp.size();i++){
            if(tmp.get(i)!=null) {
                MrMedicalLabelEntity mrMedicalLabelEntity = new MrMedicalLabelEntity();
                mrMedicalLabelEntity.setRecordsId(tmp.get(i).get("recordsId"));
                mrMedicalLabelEntity.setLabel(tmp.get(i).get("label"));
                m.add(mrMedicalLabelEntity);
            }
        }
        return  medicalLabelService.addMedicalLabels(m);
    }

    @ApiOperation("获取病历标签by RecordId")
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabels, method = RequestMethod.GET)
    public List<MrMedicalLabelEntity> getMedicalLabelInformationByID(
            @ApiParam(name = "RecordId", value = "病历表编号")
            @PathVariable(value = "Record_id")String RecordId){
        return medicalLabelService.getMedicalLabelInformationByRecordId(RecordId);
    }

    @ApiOperation("更新病历标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalLabels, method = RequestMethod.PUT)
    public boolean updateMedicalLabel(
            @ApiParam(name = "Labels", value = "病历标签")
            @RequestParam(value = "Labels")String MedicalLabels)throws Exception{
        List<Map<String,String>>tmp=toEntity(MedicalLabels,List.class);
        List<MrMedicalLabelEntity>m=new ArrayList<>();
        for(int i=0;i<tmp.size();i++){
            if(tmp.get(i)!=null) {
                MrMedicalLabelEntity mrMedicalLabelEntity = new MrMedicalLabelEntity();
                mrMedicalLabelEntity.setRecordsId(tmp.get(i).get("recordsId"));
                mrMedicalLabelEntity.setLabel(tmp.get(i).get("label"));
                m.add(mrMedicalLabelEntity);
            }
        }
        return medicalLabelService.updateMedicalLabel(m);
    }

    @ApiOperation("获取RecordId by Labels")
    @RequestMapping(value = ServiceApi.MedicalRecords.getRecordIdByLabels, method = RequestMethod.GET)
    public List<String> getRecordIdByLabels(
            @ApiParam(name = "Labels", value = "病历表编号")
            @RequestParam(value = "Labels")List<String> Labels){
        String[] arr = (String[])Labels.toArray(new String[Labels.size()]);
        return medicalLabelService.getRecordIdByLabels(arr);
    }
}
