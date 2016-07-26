package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.MedicalReporImgtDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        for(int i=1; i<=imgList.size(); i++){
            map = imgList.get(i-1);

            MrMedicalReportImgEntity mmr = new MrMedicalReportImgEntity();

            mmr.setReportId(Integer.valueOf((String) map.get("reportId")));
            mmr.setReportImgUrl((String) map.get("reportImgUrl"));
            mmr.setReportFastdfsImgUrl((String) map.get("reportFastdfsImgUrl"));
            mmr.setSort(Integer.valueOf((String) map.get("sort")));

            medicalReportImgDao.save(mmr);
        }
        return true;
    }

    /**
     * 删除数据元
     *
     * @param reportId String 数据元ID
     */
    public void deleteImgs(int reportId){

        List<MrMedicalReportImgEntity> imgList = medicalReportImgDao.findByreportIdOrderBySort(reportId);

        medicalReportImgDao.delete(imgList);
    }
}