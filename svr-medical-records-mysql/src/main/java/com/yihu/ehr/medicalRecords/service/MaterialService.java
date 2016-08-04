package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.dao.hbaseDao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.hbaseDao.TextDao;
import com.yihu.ehr.medicalRecords.model.DTO.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by hzp on 2016/8/1.
 * 素材管理类
 */
@Service
public class MaterialService {

    @Autowired
    DocumentDao DocumentDao;

    @Autowired
    TextDao textDao;


    /**
     * 上传文本素材
     * @return
     * @throws Exception
     */
    public boolean uploadTextMaterial(String creator,String creatorName,String businessClass,String content,String patientId,String patientName) throws Exception
    {
        return true;
    }

    /**
     * 获取文本素材
     */
    public List<String> getTextMaterial(String creatorId,String businessClass,String patientId,int page, int size) throws Exception{
        return null;
    }

    /**
     * 上传图片素材
     * @return
     * @throws Exception
     */
    public boolean uploadImgMaterial() throws Exception
    {
        return true;
    }

    /**
     * 获取图片素材
     */
    public List<Document> getImgMaterial(String creatorId,String patientId,String dataFrom, int page, int size) throws Exception{
        return null;
    }
}
