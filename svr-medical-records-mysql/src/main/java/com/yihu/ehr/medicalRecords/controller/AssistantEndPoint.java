package com.yihu.ehr.medicalRecords.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecords.dao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MedicalReportDao;
import com.yihu.ehr.medicalRecords.model.Entity.*;
import com.yihu.ehr.medicalRecords.service.*;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Created by linz on 2016/8/3
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "辅助检查", description = "辅助检查")
public class AssistantEndPoint extends BaseRestEndPoint {

    @Autowired
    MedicalRecordService medicalRecordService;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    DoctorService doctorService;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    MedicalReportService medicalReportService;

    @Autowired
    MedicalReportDao medicalReportDao;

    @Autowired
    DocumentRelationDao documentRelationDao;
    /**************************** 病历辅助检查图上传******************************************/
    @ApiOperation("病历辅助检查图上传")
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalRecordImg, method = RequestMethod.POST)
    public void getPatientDiagnosis(
            @ApiParam(name = "recordId", value = "病历ID", required = true)
            @RequestParam(value = "recordId") String recordId,
            @ApiParam(name = "json", value = "内容", required = true)
            @RequestParam(value = "json") String json) throws Exception{
        Map<String,Object> dataMap = objectMapper.readValue(json,Map.class);
        MrMedicalRecordsEntity mrMedicalRecordsEntity =  medicalRecordService.getMedicalRecord(recordId);
        //图片素材表
        MrDocumentEntity mrDocumentEntity = new MrDocumentEntity();
        //辅助检查报告
        MrMedicalReportEntity mrMedicalReportEntity = new MrMedicalReportEntity();
        //关系表
        MrDocumentRelationEntity mrDocumentRelationEntity = new MrDocumentRelationEntity();

        String fileList = dataMap.get("fileStringList")+"";
        String files[] = fileList.split(",");
        if(files.length<=0){
            throw new RuntimeException("不存在的图像！");
        }
        String path="";
        for(String file:files){
            byte[] bytes = Base64.getDecoder().decode(file);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            ObjectNode objectNode = fastDFSUtil.upload(inputStream,"","");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1)+",";

            mrDocumentEntity.setCreater(mrMedicalRecordsEntity.getDoctorId());
            MrDoctorsEntity mrDoctorsEntity = doctorService.getDoctor(mrMedicalRecordsEntity.getDoctorId());
            if(mrDoctorsEntity!=null){
                mrDocumentEntity.setCreaterName(mrDoctorsEntity.getName().toString());
            }
            mrDocumentEntity.setPatientId(mrMedicalRecordsEntity.getPatientId());
            mrDocumentEntity.setPatientName(mrMedicalRecordsEntity.getPatientName());
            mrDocumentEntity.setFileUrl(path);
            mrDocumentEntity.setFileType("1");
            mrDocumentEntity.setCreateTime(DateUtil.getSysDateTime());
            mrDocumentEntity.setDocumentContent("");
            documentDao.save(mrDocumentEntity);

            mrMedicalReportEntity.setRecordId(dataMap.get("recordId")+"");
            mrMedicalReportEntity.setReportContent(dataMap.get("reportContent")+"");
            mrMedicalReportEntity.setReportDatetime(Timestamp.valueOf(dataMap.get("reportDatetime") + ""));
            mrMedicalReportEntity.setReportName(dataMap.get("reportName")+"");
            medicalReportDao.save(mrMedicalReportEntity);
            if(mrDocumentEntity != null){
                mrDocumentRelationEntity.setFileId(mrDocumentEntity.getId());
                mrDocumentRelationEntity.setOwnerId(mrMedicalReportEntity.getId()+"");
                mrDocumentRelationEntity.setCreateTime(DateUtil.getSysDateTime());
                mrDocumentRelationEntity.setOwnerType("0"); //素材类型图片
                documentRelationDao.save(mrDocumentRelationEntity);
            }
        }
    }
}
