package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.comom.FileService;
import com.yihu.ehr.medicalRecords.dao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MatericalDao;
import com.yihu.ehr.medicalRecords.model.Entity.*;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzp on 2016/8/1.
 * 素材管理类
 */
@Service
public class MaterialService {

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
    FileService fileService;

    @Value("${fast-dfs.public-server}")
    String fastDFSUrl;

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
    public String uploadImgMaterial(String creatorId,String patientId,String content,String extension) throws Exception
    {
        //上传图片，生成缩略图
        String path = fileService.uploadImage(content,extension);

        //文件记录
        MrDocumentEntity mrDocumentEntity = new MrDocumentEntity();
        mrDocumentEntity.setCreater(creatorId);
        mrDocumentEntity.setPatientId(patientId);
        mrDocumentEntity.setFileUrl(path);
        mrDocumentEntity.setFileType("1");
        mrDocumentEntity.setCreateTime(DateUtil.getSysDateTime());
        mrDocumentEntity.setDocumentContent("");
        //患者姓名
        MrPatientsEntity patient = patientService.getPatient(patientId);
        if(patient!=null){
            mrDocumentEntity.setPatientName(patient.getName().toString());
        }
        //医生姓名
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

        return path;
    }



    /**
     * 获取图片素材
     */
    public List<MrDocumentEntity> getImgMaterial(String creatorId, String patientId,  int page, int size) throws Exception{

        List<MrDocumentEntity> documentEntityList = new ArrayList<MrDocumentEntity>();

        documentEntityList = documentDao.findByCreaterAndPatientId(creatorId, patientId, new PageRequest(page, size));
        if(documentEntityList!=null && documentEntityList.size() > 0)
        {
            return documentEntityList;
        }

        return null;
    }

    /**
     * 通过Ids获取文件列表
     */
    public List<MrDocumentEntity> getImgMaterialByIds(String ids) throws Exception{
        List<MrDocumentEntity> re = documentDao.findByIds(ids.split(","));

        if(re!=null && re.size()>0)
        {
            //完整http路径
            for(MrDocumentEntity item :re)
            {
                String url = item.getFileUrl();
                url = fastDFSUrl + url;
                item.setFileUrl(url);
            }
        }

        return re;
    }
}
