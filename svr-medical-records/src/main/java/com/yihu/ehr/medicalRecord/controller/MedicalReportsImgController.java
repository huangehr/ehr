package com.yihu.ehr.medicalRecord.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import com.yihu.ehr.medicalRecord.service.MedicalReportImgService;
import com.yihu.ehr.medicalRecord.service.MedicalReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalReportsImg", description = "病历报告图片服务接口")
public class MedicalReportsImgController extends EnvelopRestEndPoint {

    @Autowired
    MedicalReportImgService mRService;

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public boolean createMetadata(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReportImgs) throws Exception
    {
        List imgList = objectMapper.readValue(medicalReportImgs, new TypeReference<List>() {});
        mRService.createReportImg(imgList);
        return true;
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.GET)
    @ApiOperation("根据病历报告ID获取图片URL")
    public List<MrMedicalReportImgEntity> MedicalLabelByRecordId(
            @ApiParam(name="id",value="id",defaultValue = "")
            @RequestParam(value="id") int id) throws Exception
    {
        return mRService.getMedicalReportInfoByReportId(id);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.DELETE)
    @ApiOperation("批量删除数据元")
    public boolean deleteMetadataBatch(
            @ApiParam(name="reportId",value="数据元ID",defaultValue = "")
            @RequestParam(name="reportId") int reportId) throws Exception
    {
        mRService.deleteImgs(reportId);
        return true;
    }
}