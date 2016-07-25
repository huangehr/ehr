package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportEntity;
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

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalReports", description = "病历报告服务接口")
public class MedicalReportsController extends EnvelopRestEndPoint {

    @Autowired
    MedicalReportService mRService;

    /**
     * 新建病历报告
     *
     * @param medicalReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("新建病历报告")
    public MrMedicalReportEntity saveMedicalReport(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReport) throws Exception
    {
        MrMedicalReportEntity mrMedicalReport = toEntity(medicalReport,MrMedicalReportEntity.class);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        mrMedicalReport.setReportDatetime(t);

        mrMedicalReport = mRService.saveMedicalRecord(mrMedicalReport);
        return convertToModel(mrMedicalReport,MrMedicalReportEntity.class);
    }

    /**
     * 更新病历报告
     *
     * @param medicalReport
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("更新病历报告")
    public MrMedicalReportEntity updateReport(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReport) throws Exception
    {
        MrMedicalReportEntity mrMedicalReport = toEntity(medicalReport,MrMedicalReportEntity.class);

        mrMedicalReport = mRService.saveMedicalRecord(mrMedicalReport);
        return convertToModel(mrMedicalReport,MrMedicalReportEntity.class);
    }

    /**
     * 根据病历Id获取关联的病历报告
     *
     * @param recordId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.GET)
    @ApiOperation("根据病历ID获取病历报告")
    public List<MrMedicalReportEntity> getReport(
            @ApiParam(name="record_id",value="病历id",defaultValue = "")
            @PathVariable(value="record_id") int recordId) throws Exception
    {
        return mRService.getMedicalReportInfoByRecordId(recordId);
    }

    /**
     * 根据报告id删除病历报告
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DeleteMedicalReport,method = RequestMethod.DELETE)
    @ApiOperation("根据报告id删除病历报告")
    public boolean deleteReportById(
            @ApiParam(name="id",value="数据元ID",defaultValue = "")
            @PathVariable(value="id")int id) throws Exception
    {
        mRService.deleteReport(id);
        return true;
    }

    /**
     * 根据病历id删除报告
     *
     * @param recordId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReport,method = RequestMethod.DELETE)
    @ApiOperation("根据病历id删除报告")
    public boolean deleteReportByRecordId(
            @ApiParam(name="record_id",value="病历id",defaultValue = "")
            @PathVariable(value="record_id")int recordId) throws Exception
    {
        mRService.deleteReportByRecordId(recordId);
        return true;
    }
}