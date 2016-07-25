package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalDraftDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalDraftEntity;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class MedicalDraftService extends BaseJpaService<MrMedicalDraftEntity,MedicalDraftDao> {

    @Autowired
    MedicalDraftDao mDDao;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    public List<MrMedicalDraftEntity> getDraftByDoctorId(int doctorId, String type){

         return  mDDao.findBydoctorIdAndType(doctorId, type);
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<MrMedicalDraftEntity> getDraft(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return mDDao.findAll(pageable);
    }

    public boolean checkMedicalDraft( String doctorId
                                    , String type
                                    , String content
                                    , String fastdfsURL
                                    , int patientId){

        MrMedicalDraftEntity draft = mDDao.findBydoctorIdAndTypeAndContentAndFastdfsUrl(doctorId, type, content,fastdfsURL);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());

        if(draft == null){

            draft = new MrMedicalDraftEntity();

            draft.setDoctorId(doctorId);
            draft.setType(type);
            draft.setContent(content);
            draft.setPatientId(patientId);
            draft.setFastdfsUrl(fastdfsURL);
        }
        draft.setCreateTime(t);

        mDDao.save(draft);
        return true;
    }

    public boolean deleteDraft(String ids){

        ArrayList<Integer> idArray = new ArrayList<>();
        String[] idList = ids.split(",");

        for(String a: idList){

            idArray.add(Integer.valueOf(a));
        }

        List<MrMedicalDraftEntity> draftList = mDDao.findByidIn(idArray);
        mDDao.delete(draftList);

        return true;
    }
}