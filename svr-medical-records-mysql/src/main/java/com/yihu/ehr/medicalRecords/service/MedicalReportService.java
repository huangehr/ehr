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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    MaterialService materialService;

    @Autowired
    DocumentRelationDao documentRelationDao;

    /**
     * 获取辅助检查报告
     */
    public MedicalReportDTO getMedicalReport(String recordId, String reportId) throws Exception
    {
        String fileIds = "";

        MrMedicalReportEntity re = medicalReportDao.findByRecordIdAndId(recordId, Integer.parseInt(reportId));
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
                fileIds += mrDocumentRelationEntity.getFileId() + ",";
            }
        }
        medicalReportDTO.setFileIds(fileIds == "" ? "" : fileIds.substring(0, fileIds.length() - 1));

        return medicalReportDTO;
    }

    /**
     * 获取某病历下所有的辅助检查报告(含图片路径)
     */
    public List<MedicalReportDTO>  getMedicalReports(String recordId) throws Exception
    {
        List<MedicalReportDTO> medicalReportDTOList = new ArrayList<MedicalReportDTO>();
        List<Map> mapList = new ArrayList<>();
        List<MrMedicalReportEntity> re = medicalReportDao.findByRecordId(recordId);
        if(re == null || re.size() == 0){
            return null;
        }

        for(MrMedicalReportEntity mrMedicalReportEntity :re ){
            String fileIds = "";
            MedicalReportDTO medicalReportDTO = new MedicalReportDTO();
            medicalReportDTO.setId(mrMedicalReportEntity.getId());
            medicalReportDTO.setReportName(mrMedicalReportEntity.getReportName());
            medicalReportDTO.setRecordId(mrMedicalReportEntity.getRecordId());
            medicalReportDTO.setReportContent(mrMedicalReportEntity.getReportContent());
            medicalReportDTO.setReportDatetime(mrMedicalReportEntity.getReportDatetime());

            List<MrDocumentRelationEntity> mrDocumentRelationEntityList = documentRelationDao.findByOwnerId(String.valueOf(mrMedicalReportEntity.getId()) );
            if(mrDocumentRelationEntityList.size() != 0){
                for(MrDocumentRelationEntity mrDocumentRelationEntity : mrDocumentRelationEntityList) {
                    fileIds += mrDocumentRelationEntity.getFileId() + ",";
                };
                medicalReportDTO.setFileIds(fileIds == "" ? "" : fileIds.substring(0, fileIds.length() - 1));
            }

            //get documents conent url and murl
            List<MrDocumentEntity> reDocuments =  materialService.getImgMaterialByIds(medicalReportDTO.getFileIds());
            for(MrDocumentEntity mrDocumentEntity : reDocuments){
                Map fileMap = new HashMap<>();
                fileMap.put("id",mrDocumentEntity.getId());

                String fileUrl = mrDocumentEntity.getFileUrl();
                fileMap.put("url",fileUrl);

                String fileMUrl = fileUrl.substring(0,fileUrl.length()-4)+ "_200*200" + fileUrl.substring(fileUrl.length()-4,fileUrl.length());
                fileMap.put("murl",fileMUrl);

                mapList.add(fileMap);
            }
            medicalReportDTO.setFileMaps(mapList);

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
        String re = obj.getFileIds();
        if(!StringUtils.isEmpty(re))
        {
            String[] fileIds = re.split(",");
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