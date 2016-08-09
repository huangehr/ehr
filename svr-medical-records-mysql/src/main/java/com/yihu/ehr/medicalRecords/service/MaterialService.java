package com.yihu.ehr.medicalRecords.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecords.dao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MatericalDao;
import com.yihu.ehr.medicalRecords.model.Entity.*;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by hzp on 2016/8/1.
 * 素材管理类
 */
@Service
public class MaterialService extends BaseJpaService<MrTextEntity, MatericalDao> {

    @Autowired
    MatericalDao matericalDao;

    @Autowired
    DocumentDao documentDao;

    @Autowired
    DoctorService doctorService;

    @Autowired
    PatientService patientService;

    @Autowired
    DocumentRelationDao documentRelationDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    /**
     * 上传文本素材
     * @return
     * @throws Exception
     */
    public boolean uploadTextMaterial(String creator,String businessClass,String content,String patientId) throws Exception
    {
        MrTextEntity mrTextEntity = new MrTextEntity();

        mrTextEntity.setCreateTime(DateUtil.getSysDateTime());
        mrTextEntity.setBusinessClass(businessClass);
        mrTextEntity.setContent(content);
        mrTextEntity.setCreater(creator);
        mrTextEntity.setPatientId(patientId);
        mrTextEntity.setUsageCount(1);
        MrPatientsEntity patient = patientService.getPatient(patientId);
        if(patient!=null){
            mrTextEntity.setPatientName(patient.getName().toString());
        }
        MrDoctorsEntity mrDoctorsEntity = doctorService.getDoctor(creator);
        if(mrDoctorsEntity!=null){
            mrTextEntity.setCreaterName(mrDoctorsEntity.getName().toString());
        }
        mrTextEntity = matericalDao.save(mrTextEntity);

        return true;
    }

    /**
     * 获取文本素材
     */
    public List<String> getTextMaterial(String creatorId,String businessClass,String patientId,int page, int size) throws Exception{

        List<String> testList = new ArrayList<>();
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        List<MrTextEntity> mrTextEntities = matericalDao.findByCreaterAndBusinessClassAndPatientId(creatorId, businessClass, patientId, new PageRequest(page-1, size,sort));

        if(mrTextEntities!=null && mrTextEntities.size() > 0)
        {
            for(MrTextEntity mrTextEntity : mrTextEntities ){
                testList.add(mrTextEntity.getContent());
            }
        }

        return testList;
    }

    /**
     * 上传图片素材,图片上传时就执行图片的保存及与医生的关联的建立。
     * @return
     * @throws Exception
     */
    public boolean uploadImgMaterial(String documentName,String creatorId,String patientId,String jsonData) throws Exception
    {
        //jsonData 直接是图片二进制流,不是JSON格式字串
        byte[] bytes = Base64.getDecoder().decode(jsonData);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = documentName.substring(documentName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);

        MrDocumentEntity mrDocumentEntity = new MrDocumentEntity();
        mrDocumentEntity.setCreater(creatorId);
        mrDocumentEntity.setPatientId(patientId);
        mrDocumentEntity.setFileUrl(path);
        mrDocumentEntity.setFileType("1");
        mrDocumentEntity.setCreateTime(DateUtil.getSysDateTime());
        mrDocumentEntity.setDocumentContent("");
        MrPatientsEntity patient = patientService.getPatient(patientId);
        if(patient!=null){
            mrDocumentEntity.setPatientName(patient.getName().toString());
        }
        MrDoctorsEntity mrDoctorsEntity = doctorService.getDoctor(creatorId);
        if(mrDoctorsEntity!=null){
            mrDocumentEntity.setCreaterName(mrDoctorsEntity.getName().toString());
        }

        mrDocumentEntity = documentDao.save(mrDocumentEntity);
        if(mrDocumentEntity != null){
            MrDocumentRelationEntity mrDocumentRelationEntity = new MrDocumentRelationEntity();
            mrDocumentRelationEntity.setFileId(mrDocumentEntity.getId());
            mrDocumentRelationEntity.setOwnerId(creatorId);
            mrDocumentRelationEntity.setCreateTime(DateUtil.getSysDateTime());
            mrDocumentRelationEntity.setOwnerType("1"); //素材类型图片

            documentRelationDao.save(mrDocumentRelationEntity);
        }

        return true;
    }



    /**
     * 获取图片素材
     */
    public List<MrDocumentEntity> getImgMaterial(String creatorId, String patientId, String dataFrom, int page, int size) throws Exception{

        List<MrDocumentEntity> documentEntityList = new ArrayList<MrDocumentEntity>();
        DocumentDao repo = (DocumentDao) getJpaRepository();

        documentEntityList = repo.findByCreaterAndPatientIdAndCreateTime(creatorId, patientId, dataFrom, new PageRequest(page, size));
        if(documentEntityList!=null && documentEntityList.size() > 0)
        {
            return documentEntityList;
        }

        return null;
    }
}
