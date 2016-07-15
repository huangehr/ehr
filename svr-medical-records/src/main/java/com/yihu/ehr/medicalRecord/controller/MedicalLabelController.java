package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import com.yihu.ehr.medicalRecord.service.MedicalLabelService;
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
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "病历标签", description = "病历标签管理")
public class MedicalLabelController extends BaseRestEndPoint {
    @Autowired
    MedicalLabelService medicalLabelService;

    @ApiOperation("批量保存病历标签")
    @RequestMapping(value = "/medical_record/label/addMedicalLabels", method = RequestMethod.POST)
    public boolean addMedicalLabels(@ApiParam(name = "MedicalLabels", value = "病历标签集")
                                    @RequestParam(value = "MedicalLabels", required = true)String MedicalLabels){
        List<Map<String,String>>tmp=toEntity(MedicalLabels,List.class);
        List<MrMedicalLabelEntity>m=new ArrayList<>();
        for(int i=0;i<tmp.size();i++){
            MrMedicalLabelEntity mrMedicalLabelEntity=new MrMedicalLabelEntity();
            mrMedicalLabelEntity.setRecordsId(Integer.parseInt(tmp.get(i).get("recordsId")));
            mrMedicalLabelEntity.setLabel(tmp.get(i).get("label"));
            m.add(mrMedicalLabelEntity);
        }
        return  medicalLabelService.addMedicalLabels(m);
    }

    @ApiOperation("获取病历标签by RecordId")
    @RequestMapping(value = "/medical_record/label/getMedicalLabelInformationByID", method = RequestMethod.GET)
    public List<MrMedicalLabelEntity> getMedicalLabelInformationByID(
            @ApiParam(name = "RecordId", value = "病历表编号")
            @RequestParam(value = "RecordId", required = true)String RecordId){
        return medicalLabelService.getMedicalLabelInformationByRecordId(RecordId);
    }
}
