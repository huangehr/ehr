package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.DoctorDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class DoctorService {

    @Autowired
    DoctorDao doctorDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;

    public MrDoctorsEntity getDoctorInformationByDemographicId(String demographicId){

        return doctorDao.findBydemographicId(demographicId);
    }

    public boolean updateDoctorInformationByDemographicId(MrDoctorsEntity doctor){
        if(doctor!=null) {
            MrDoctorsEntity doctorModel = doctorDao.findBydemographicId(String.valueOf(doctor.getDemographicId()));
            if(doctorModel!=null ){
                doctorModel.setSex(doctor.getSex());
                doctorModel.setBirthday(doctor.getBirthday());
                doctorModel.setStatus(doctor.getStatus());
                doctorModel.setGood(doctor.getGood());
                doctorModel.setOrgCode(doctor.getOrgCode());
                doctorModel.setOrgName(doctor.getOrgName());
                doctorModel.setOrgDept(doctor.getOrgDept());
                doctorModel.setTitle(doctor.getTitle());
                doctorModel.setName(doctor.getName());
                doctorModel.setPhone(doctor.getPhone());
                doctorModel.setPhoto(doctor.getPhoto());
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

    }

    public boolean updateDoctorStatusByDemographicId(String status,String demographicId){

        MrDoctorsEntity DoctorModel=doctorDao.findBydemographicId(demographicId);
        if(DoctorModel!=null) {
            DoctorModel.setStatus(status);
        }
        return true;

    }

    public boolean deleteDoctorByDemographicId(String id){
        doctorDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addDoctor(MrDoctorsEntity doctor){
        if(doctorDao.findBydemographicId(doctor.getDemographicId())!=null){
            return false;
        }
        else {
            doctorDao.save(doctor);
            return true;
        }
    }

    public List<String> getDoctorDiagnosis(String doctorId){
        List<MrMedicalRecordsEntity>list= medicalRecordDao.findByDoctorId(doctorId);
        List<String> diagnosisList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            if(list.get(i)!=null){
                if(!list.contains(list.get(i).getMedicalDiagnosis())) {
                    diagnosisList.add(list.get(i).getMedicalDiagnosis());
                }
            }
        }
        return diagnosisList;
    }
}
