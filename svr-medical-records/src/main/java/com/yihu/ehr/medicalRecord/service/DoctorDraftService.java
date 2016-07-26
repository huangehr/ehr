package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.DoctorDraftDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@Service
@Transactional
public class DoctorDraftService extends BaseJpaService<MrDoctorDraftEntity,DoctorDraftDao> {

    @Autowired
    DoctorDraftDao dDDao;

    public List<MrDoctorDraftEntity> getDraftByDoctorId(int doctorId, String type){

         return dDDao.findBydoctorId(doctorId, type);
    }

    public MrDoctorDraftEntity saveDoctorDraft(MrDoctorDraftEntity doctorDraft){

        return dDDao.save(doctorDraft);
    }

    public MrDoctorDraftEntity updateDraftBy(int id){

        MrDoctorDraftEntity Draft = dDDao.findByid(id);
        Draft.setUsageCount(Draft.getUsageCount() + 1);

        return dDDao.save(Draft);
    }

    public boolean checkDoctorDraft(String doctorId, String type, String content){

        MrDoctorDraftEntity draft = dDDao.findBydoctorIdAndTypeAndContent(doctorId, type, content);

        if(draft == null){

            draft = new MrDoctorDraftEntity();

            draft.setDoctorId(doctorId);
            draft.setType(type);
            draft.setContent(content);
            draft.setUsageCount(1);
        }else{

            draft.setUsageCount(draft.getUsageCount() + 1);
        }

        dDDao.save(draft);
        return true;
    }

    /**
     * 资源获取
     *
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<MrDoctorDraftEntity> getDraft(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return dDDao.findAll(pageable);
    }

    public boolean deleteDraft(String ids){

        ArrayList<Integer> idArray = new ArrayList<>();
        String[] idList = ids.split(",");

        for(String a: idList){

            idArray.add(Integer.valueOf(a));
        }

        List<MrDoctorDraftEntity> draftList = dDDao.findByidIn(idArray);
        dDDao.delete(draftList);

        return true;
    }
}