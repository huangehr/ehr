package com.yihu.ehr.medicalRecords.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecords.dao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MatericalDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentRelationEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrTextEntity;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    DocumentRelationDao documentRelationDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

    /**
     * 上传文本素材
     * @return
     * @throws Exception
     */
    public boolean uploadTextMaterial(String creator,String creatorName,String businessClass,String content,String patientId,String patientName) throws Exception
    {
        MrTextEntity mrTextEntity = new MrTextEntity();

        mrTextEntity.setCreateTime(DateUtil.getSysDateTime());
        mrTextEntity.setBusinessClass(businessClass);
        mrTextEntity.setContent(content);
        mrTextEntity.setCreater(creator);
        mrTextEntity.setCreaterName(creatorName);
        mrTextEntity.setPatientId(patientId);
        mrTextEntity.setPatientName(patientName);
        mrTextEntity.setUsageCount(1);
        mrTextEntity = matericalDao.save(mrTextEntity);

        return true;
    }

    /**
     * 获取文本素材
     */
    public List<String> getTextMaterial(String creatorId,String businessClass,String patientId,int page, int size) throws Exception{

        List<String> testList = new ArrayList<>();
        page = page - 1;
        List<MrTextEntity> mrTextEntities = matericalDao.findByCreaterAndBusinessClassAndPatientId(creatorId, businessClass, patientId, new PageRequest(page, size));

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
    public boolean uploadImgMaterial(String documentName,String creatorId,String creatorName,String patientId,String patientName,String jsonData) throws Exception
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
        mrDocumentEntity.setCreaterName(creatorName);
        mrDocumentEntity.setPatientId(patientId);
        mrDocumentEntity.setPatientName(patientName);
        mrDocumentEntity.setFileUrl(path);
        mrDocumentEntity.setFileType("JPG");
        mrDocumentEntity.setCreateTime(DateUtil.getSysDateTime());
        mrDocumentEntity.setDocumentContent("");

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
