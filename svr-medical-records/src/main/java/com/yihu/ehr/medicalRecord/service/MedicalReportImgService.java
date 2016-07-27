package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalReporImgtDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrFileDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrFileRelationDao;
import com.yihu.ehr.medicalRecord.model.MrFileEntity;
import com.yihu.ehr.medicalRecord.model.MrFileRelationEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class MedicalReportImgService {

    @Autowired
    MedicalReporImgtDao medicalReportImgDao;

    @Autowired
    MrFileDao fileDao;

    @Autowired
    MrFileRelationDao fileRelationDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    /**
     * 查询图片信息
     */
    public List<MrMedicalReportImgEntity> getMedicalReportInfoByReportId(int ReportId){

         return  medicalReportImgDao.findByreportIdOrderBySort(ReportId);
    }

    /**
     * 批量创建数据元
     */
    public boolean createReportImg(List<Map<String, Object>> imgList){

        Map<String, Object> map;

        for(int i=0; i<imgList.size(); i++){
            map = imgList.get(i);

            MrMedicalReportImgEntity mmr = new MrMedicalReportImgEntity();

            mmr.setReportId(Integer.valueOf((String) map.get("reportId")));
            mmr.setReportImgUrl((String) map.get("reportImgUrl"));
            mmr.setReportFastdfsImgUrl((String) map.get("reportFastdfsImgUrl"));
            mmr.setSort(Integer.valueOf((String) map.get("sort")));

            mmr = medicalReportImgDao.save(mmr);

            MrFileRelationEntity relationEntity = new MrFileRelationEntity();

            Date date = new Date();
            Timestamp t = new Timestamp(date.getTime());

            relationEntity.setCreateTime(t);
            relationEntity.setFileId((String) map.get("id"));
            relationEntity.setOwnerId(String.valueOf(mmr.getReportId()));
            relationEntity.setOwnerType("0");//所属类型为病历报告

            fileRelationDao.save(relationEntity);
        }
        return true;
    }

    /**
     * 删除数据元
     *
     * @param reportId String 数据元ID
     */
    public void deleteImgs(int reportId) throws Exception {

        List<MrMedicalReportImgEntity> imgList = medicalReportImgDao.findByreportIdOrderBySort(reportId);
        List<MrFileRelationEntity> relationList = fileRelationDao.findByownerId(String.valueOf(reportId));

        for(MrFileRelationEntity relation: relationList){

            List<MrFileRelationEntity> thisFileCount = fileRelationDao.findByfileId(relation.getFileId());

            if(thisFileCount.size() == 1){

                MrFileEntity fileEntity = fileDao.findByid(Integer.valueOf(thisFileCount.get(0).getFileId()));

                fileDao.delete(fileEntity);

                String storagePath = fileEntity.getFilePath();
                String groupName = storagePath.split(":")[0];
                String remoteFileName = storagePath.split(":")[1];
                fastDFSUtil.delete(groupName,remoteFileName);
            }
        }

        medicalReportImgDao.delete(imgList);
        fileRelationDao.delete(relationList);
    }
}