package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.comom.FileService;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.dao.DocumentDao;
import com.yihu.ehr.medicalRecords.dao.DocumentRelationDao;
import com.yihu.ehr.medicalRecords.dao.MatericalDao;
import com.yihu.ehr.medicalRecords.model.Entity.*;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Base64;
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
    FileService fileService;

    @Value("${fast-dfs.public-server}")
    private String fastDFSUrl;

    private int defaultPage = 1;
    private int defaultSize = 10;

    /**
     * 上传文本素材
     * @return
     * @throws Exception
     */
    public boolean uploadTextMaterial(String creator,String businessClass,String content,String patientId) throws Exception
    {
        if(StringUtils.isEmpty(businessClass) && StringUtils.isEmpty(patientId))
        {
            Message.error("素材类型和患者Id不能同时为空！");
        }
        else if(!StringUtils.isEmpty(businessClass) && !StringUtils.isEmpty(patientId)){
            Message.error("素材类型和患者Id不能同时存在！");
        }

        MrTextEntity mrTextEntity = new MrTextEntity();
        mrTextEntity.setCreateTime(DateUtil.getSysDateTime());
        mrTextEntity.setContent(content);
        mrTextEntity.setCreater(creator);
        mrTextEntity.setUsageCount(1);


        MrDoctorsEntity mrDoctorsEntity = doctorService.getDoctor(creator);
        //医生姓名
        if(mrDoctorsEntity!=null){
            mrTextEntity.setCreaterName(mrDoctorsEntity.getName().toString());
        }

        //对话文本
        if(!StringUtils.isEmpty(patientId)) {
            mrTextEntity.setPatientId(patientId);
            MrPatientsEntity patient = patientService.getPatient(patientId);
            //患者姓名
            if (patient != null) {
                mrTextEntity.setPatientName(patient.getName().toString());
            }
        }
        else{  //素材文本
            //判断是否已经存在
            MrTextEntity obj =  matericalDao.findByCreaterAndBusinessClassAndContent(creator,businessClass,content);
            if(obj!=null)
            {
                obj.setUsageCount(obj.getUsageCount()+1);
                mrTextEntity = obj;
            }
            else{
                mrTextEntity.setBusinessClass(businessClass);
            }
        }
        matericalDao.save(mrTextEntity);

        return true;
    }

    /**
     * 获取文本素材
     */
    public List<String> getTextByClass(String creatorId,String businessClass,Integer page, Integer size) throws Exception{
        List<String> testList = new ArrayList<>();

        if(page==null)
        {
            page = defaultPage;
        }
        if(size==null)
        {
            size = defaultSize;
        }

        Sort sort = new Sort(Sort.Direction.DESC,"usageCount");
        List<MrTextEntity> mrTextEntities = matericalDao.findByCreaterAndBusinessClass(creatorId, businessClass, new PageRequest(page-1, size,sort));

        if(mrTextEntities!=null && mrTextEntities.size() > 0)
        {
            for(MrTextEntity mrTextEntity : mrTextEntities ){
                testList.add(mrTextEntity.getContent());
            }
        }

        return testList;
    }

    /**
     * 获取对话文本
     */
    public List<MrTextEntity> getTextByDoctorAndPatient(String doctorId,String patientId,Integer page, Integer size)  throws Exception
    {
        if(page==null)
        {
            page = defaultPage;
        }
        if(size==null)
        {
            size = defaultSize;
        }

        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        return matericalDao.findByCreaterAndPatientId(doctorId, patientId, new PageRequest(page-1, size,sort));
    }


    /******************************************************************************************/
    /**
     * 上传图片素材
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
        //mrDocumentEntity.setDocumentContent("");
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

        documentDao.save(mrDocumentEntity);

        return path;
    }

    /**
     * 获取图片素材
     */
    public List<MrDocumentEntity> getImgMaterial(String creatorId, String patientId,  Integer page, Integer size) throws Exception{
        if(page==null)
        {
            page = defaultPage;
        }
        if(size==null)
        {
            size = defaultSize;
        }

        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        List<MrDocumentEntity> list = documentDao.findByCreaterAndPatientId(creatorId, patientId, new PageRequest(page-1, size,sort));
       if(list!=null && list.size()>0)
       {
           for(MrDocumentEntity item:list)
           {
               item.setFileUrl(getHttpUrl(item.getFileUrl()));
           }
       }
        return list;
    }

    /**
     * 获取http图片地址
     * @return
     */
    private String getHttpUrl(String url) throws Exception
    {
        String[] urls = url.split(":");
        if(urls.length==2)
        {
            url = fastDFSUrl + "/"+urls[0]+"/"+urls[1];
        }
        return url;
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
                item.setFileUrl(getHttpUrl(item.getFileUrl()));
            }
        }

        return re;
    }
}
