package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrLabelClassEntity;
import com.yihu.ehr.medicalRecord.model.MrLabelEntity;
import com.yihu.ehr.medicalRecord.service.DoctorLabelService;
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
 * Created by shine on 2016/7/27.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "医生标签", description = "医生标签管理")
public class DoctorLabelController extends BaseRestEndPoint {
    @Autowired
    DoctorLabelService doctorLabelService;

    @ApiOperation("新增医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClass, method = RequestMethod.POST)
    public MrLabelClassEntity addDoctorLabelsClass(@ApiParam(name = "DoctorLabelsClass", value = "医生标签类别")
                                    @RequestParam(value = "DoctorLabelsClass", required = true)String DoctorLabelsClass){
        MrLabelClassEntity mrLabelClassEntity=toEntity(DoctorLabelsClass,MrLabelClassEntity.class);
        return  doctorLabelService.addDoctorLabelsClass(mrLabelClassEntity);
    }

    @ApiOperation("更新医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClass, method = RequestMethod.PUT)
    public boolean updateDoctorLabelsClass(@ApiParam(name = "DoctorLabelsClass", value = "医生标签类别")
                                    @RequestParam(value = "DoctorLabelsClass", required = true)String DoctorLabelsClass){
        MrLabelClassEntity mrLabelClassEntity=toEntity(DoctorLabelsClass,MrLabelClassEntity.class);
        return  doctorLabelService.updateDoctorLabelsClass(mrLabelClassEntity);
    }

    @ApiOperation("删除医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClass, method = RequestMethod.DELETE)
    public boolean deleteDoctorLabelsClass(@ApiParam(name = "id", value = "id")
                                    @RequestParam(value = "id", required = true)int id){
        return  doctorLabelService.deleteDoctorLabelsClass(id);
    }

    @ApiOperation("获取医生标签类别")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelClass, method = RequestMethod.GET)
    public List<MrLabelClassEntity> getDoctorLabelsClass(@ApiParam(name = "doctorId", value = "doctorId")
                                            @RequestParam(value = "doctorId", required = true)String doctorId){
        return  doctorLabelService.getDoctorLabelsClass(doctorId);
    }


    @ApiOperation("新增医生标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabel, method = RequestMethod.POST)
    public MrLabelEntity addDoctorLabels(@ApiParam(name = "MedicalLabels", value = "医生标签")
                                                   @RequestParam(value = "MedicalLabels", required = true)String  DoctorLabels){
        MrLabelEntity mrLabelEntity=toEntity( DoctorLabels,MrLabelEntity.class);
        return  doctorLabelService.addDoctorLabels(mrLabelEntity);
    }

    @ApiOperation("更新医生标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabel, method = RequestMethod.PUT)
    public boolean updateDoctorLabels(@ApiParam(name = "DoctorLabels", value = "医生标签")
                                           @RequestParam(value = "DoctorLabels", required = true)String DoctorLabels){
        MrLabelEntity mrLabelEntity=toEntity(DoctorLabels,MrLabelEntity.class);
        return  doctorLabelService.updateDoctorLabels(mrLabelEntity);
    }

    @ApiOperation("删除医生标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabel, method = RequestMethod.DELETE)
    public boolean deleteDoctorLabels(@ApiParam(name = "id", value = "id")
                                           @RequestParam(value = "id", required = true)int id){
        return  doctorLabelService.deleteDoctorLabels(id);
    }

    @ApiOperation("获取医生标签")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabel, method = RequestMethod.GET)
    public List<MrLabelEntity> getDoctorLabels(@ApiParam(name = "doctorId", value = "doctorId")
                                                         @RequestParam(value = "doctorId", required = true)String doctorId){
        return  doctorLabelService.getDoctorLabels(doctorId);
    }

    @ApiOperation("医生标签使用次数")
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorLabelUsed, method = RequestMethod.PUT)
    public boolean DoctorLabelUsed(
                                   @ApiParam(name = "ID", value = "ID")
                                   @RequestParam(value = "ID", required = true)int ID){
        return  doctorLabelService.DoctorLabelUsed(ID);
    }
}
