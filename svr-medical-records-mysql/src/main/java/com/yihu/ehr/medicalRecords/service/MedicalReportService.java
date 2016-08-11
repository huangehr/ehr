package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MedicalReportDao;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalReportDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentRelationEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalReportEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzp on 2016/8/4.
 * 辅助检查报告
 */
@Service
@Transactional
public class MedicalReportService {

    @Autowired
    MedicalReportDao medicalReportDao;

    @Autowired
    DocumentRelationDao documentRelationDao;

    /**
     * 获取辅助检查报告
     */
    public MedicalReportDTO getMedicalReport(String recordId, String reportId) throws Exception
    {
        String fileUrlList = "";

        MrMedicalReportEntity re = medicalReportDao.findByRecordIdAndId(recordId,Integer.parseInt(reportId));
        if(re == null){
            return null;
        }
        MedicalReportDTO medicalReportDTO = new MedicalReportDTO();
        medicalReportDTO.setId(re.getId());
        medicalReportDTO.setReportName(re.getReportName());
        medicalReportDTO.setRecordId(re.getRecordId());
        medicalReportDTO.setReportContent(re.getReportContent());
        medicalReportDTO.setReportDatetime(re.getReportDatetime());

        List<MrDocumentRelationEntity> mrDocumentRelationEntityList = documentRelationDao.findByOwnerId(reportId);
        if(mrDocumentRelationEntityList.size() != 0){
            for(MrDocumentRelationEntity mrDocumentRelationEntity : mrDocumentRelationEntityList) {
                fileUrlList += mrDocumentRelationEntity.getFileId() + ";";
            }
        }
        medicalReportDTO.setFileUrlList(fileUrlList==""?"":fileUrlList.substring(0,fileUrlList.length()-1));

        return medicalReportDTO;
    }

    /**
     * 获取某病历下所有的辅助检查报告
     */
    public List<MedicalReportDTO>  getMedicalReports(String recordId) throws Exception
    {
        List<MedicalReportDTO> medicalReportDTOList = new ArrayList<MedicalReportDTO>();

        List<MrMedicalReportEntity> re = medicalReportDao.findByRecordId(recordId);
        if(re == null || re.size() == 0){
            return new ArrayList<MedicalReportDTO>();
        }

        for(MrMedicalReportEntity mrMedicalReportEntity :re ){
            String fileUrlList = "";
            MedicalReportDTO medicalReportDTO = new MedicalReportDTO();
            medicalReportDTO.setId(mrMedicalReportEntity.getId());
            medicalReportDTO.setReportName(mrMedicalReportEntity.getReportName());
            medicalReportDTO.setRecordId(mrMedicalReportEntity.getRecordId());
            medicalReportDTO.setReportContent(mrMedicalReportEntity.getReportContent());
            medicalReportDTO.setReportDatetime(mrMedicalReportEntity.getReportDatetime());

            List<MrDocumentRelationEntity> mrDocumentRelationEntityList = documentRelationDao.findByOwnerId(String.valueOf(mrMedicalReportEntity.getId()) );
            if(mrDocumentRelationEntityList.size() != 0){
                for(MrDocumentRelationEntity mrDocumentRelationEntity : mrDocumentRelationEntityList) {
                    fileUrlList += mrDocumentRelationEntity.getFileId() + ";";
                };
            }
            medicalReportDTO.setFileUrlList(fileUrlList);
            medicalReportDTOList.add(medicalReportDTO);
        }

        return medicalReportDTOList;
    }


    /**
     * 保存辅助检查报告
     */
    public boolean saveMedicalReport(String recordId,MedicalReportDTO obj) throws Exception
    {
        MrMedicalReportEntity mrMedicalReportEntity = new MrMedicalReportEntity();
        mrMedicalReportEntity.setId(obj.getId());
        mrMedicalReportEntity.setRecordId(recordId);
        mrMedicalReportEntity.setReportName(obj.getReportName());
        mrMedicalReportEntity.setReportContent(obj.getReportContent());
        mrMedicalReportEntity.setReportDatetime(obj.getReportDatetime());
        mrMedicalReportEntity = medicalReportDao.save(mrMedicalReportEntity);

        int reportId = mrMedicalReportEntity.getId();
        String fileUrlList = obj.getFileUrlList();
        if(!StringUtils.isEmpty(fileUrlList))
        {
            String[] fileIds = fileUrlList.split(";");
            for(String fileId : fileIds){
                MrDocumentRelationEntity mrDocumentRelationEntity = new MrDocumentRelationEntity();
                mrDocumentRelationEntity.setOwnerId(String.valueOf(reportId));
                mrDocumentRelationEntity.setOwnerType("0");
                mrDocumentRelationEntity.setCreateTime(obj.getReportDatetime());
                mrDocumentRelationEntity.setFileId(Integer.parseInt(fileId));

                documentRelationDao.save(mrDocumentRelationEntity);
            }
        }

        return true;
    }

    /**
     * 根据报告id删除辅助检查报告
     */
    public boolean deleteReportById(String recordId,String reportId) throws Exception
    {
        //medicalReportDao.deleteFileByReportId(reportId);
        documentRelationDao.deleteByOwnerId(reportId);
        medicalReportDao.deleteById(Integer.parseInt(reportId));
        return true;
    }
}