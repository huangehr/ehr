package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalLabelDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalLabelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class MedicalLabelService extends BaseRestEndPoint {

    @Autowired
    MedicalLabelDao medicalLabelDao;
    public List<MrMedicalLabelEntity> getMedicalLabelInformationByRecordId(String RecordId){
       return medicalLabelDao.findByrecordsId(RecordId);
    }

    public boolean updateMedicalLabel(List<MrMedicalLabelEntity> MedicalLabels){

        if(MedicalLabels.size()>0) {
            for (int i = 0; i < MedicalLabels.size(); i++) {

                MrMedicalLabelEntity m= MedicalLabels.get(i);
                if(m!=null) {
                    if (medicalLabelDao.findByrecordsId(m.getRecordsId()) != null
                            && medicalLabelDao.findByrecordsId(m.getRecordsId()).size() > 0){
                        medicalLabelDao.deleteByrecordsId(m.getRecordsId());
                        medicalLabelDao.save(MedicalLabels.get(i));
                    }
                }
            }
            return true;
        }
        else
            return false;


    }

    public boolean deleteMedicalLabelByR(String id){
        medicalLabelDao.deleteByrecordsId(id);
        return true;
    }

    public boolean addMedicalLabels(List<MrMedicalLabelEntity> MedicalLabels){
        if(MedicalLabels.size()>0) {
            for (int i = 0; i < MedicalLabels.size(); i++) {

                MrMedicalLabelEntity m= MedicalLabels.get(i);
                if(m!=null) {
                    if (medicalLabelDao.findByrecordsId(m.getRecordsId()) != null
                            && medicalLabelDao.findByrecordsId(m.getRecordsId()).size() > 0){

                    }
                    else{
                        medicalLabelDao.save(MedicalLabels.get(i));
                    }

                }
            }
            return true;
        }
        else
            return false;

    }

    public List<String> getRecordIdByLabels(String...Lable){
        List<MrMedicalLabelEntity>m=medicalLabelDao.findByLabels(Lable);
        Map<String,Integer>map=new HashMap<>();
        for(int i=0;i<m.size();i++) {
            if (m.get(i) != null) {
                if (map.get(m.get(i).getRecordsId()) != null) {
                    String k= m.get(i).getRecordsId();
                    map.put(m.get(i).getRecordsId(),map.get(k)+1);
                }
                else{
                    map.put(m.get(i).getRecordsId(),1);
                }

            }
        }
        List<String>list=new ArrayList<>();
        for(String key:map.keySet()){
            if(map.get(key)==Lable.length){
                list.add(key);
            }
        }
       return list;
    }

}
