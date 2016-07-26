package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalDraftDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrFileDao;
import com.yihu.ehr.medicalRecord.dao.intf.MrFileRelationDao;
import com.yihu.ehr.medicalRecord.model.MrFileEntity;
import com.yihu.ehr.medicalRecord.model.MrFileRelationEntity;
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
    MrFileDao fileDao;

    @Autowired
    MrFileRelationDao fileRelationDao;

    @Autowired
    FastDFSUtil fastDFSUtil;

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
                                    , String patientId){

        MrMedicalDraftEntity draft = mDDao.findBydoctorIdAndTypeAndContentAndFastdfsUrl(doctorId, type, content,fastdfsURL);

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());
        boolean firstFlag = false;

        if(draft == null){

            draft = new MrMedicalDraftEntity();

            draft.setDoctorId(doctorId);
            draft.setType(type);
            draft.setContent(content);
            draft.setPatientId(Integer.valueOf(patientId));
            draft.setFastdfsUrl(fastdfsURL);

            firstFlag = true;
        }
        draft.setCreateTime(t);

        draft = mDDao.save(draft);

        if(firstFlag){

            MrFileEntity fileEntity = fileDao.findByfilePath(fastdfsURL);
            if(fileEntity != null){

                MrFileRelationEntity relationEntity = new MrFileRelationEntity();

                relationEntity.setOwnerType("1");//所属类型为草稿
                relationEntity.setOwnerId(String.valueOf(draft.getId()));
                relationEntity.setFileId(String.valueOf(fileEntity.getId()));
                relationEntity.setCreateTime(t);

                fileRelationDao.save(relationEntity);
            }
        }

        return true;
    }

    public boolean deleteDraft(String ids) throws Exception {

        ArrayList<Integer> idArray = new ArrayList<>();
        String[] idList = ids.split(",");

        for(String a: idList){

            idArray.add(Integer.valueOf(a));
        }

        List<MrMedicalDraftEntity> draftList = mDDao.findByidIn(idArray);
        List<MrFileRelationEntity> relationList = fileRelationDao.findByownerIdIn(idArray);

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
        mDDao.delete(draftList);
        fileRelationDao.delete(relationList);

        return true;
    }
}