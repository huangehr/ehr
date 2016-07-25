package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
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
public class PatientService {


    @Autowired
    PatientDao patientDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;

    public MrPatientsEntity getPatientInformationBydemographicIdOrPhone(String demographicId,String phone){
        if(demographicId==null || patientDao.findBydemographicId(demographicId)==null){
            if(phone!=null&&patientDao.findByphone(phone)!=null)
                return patientDao.findByphone(phone);
            else
                return null;
        }
        else
            return patientDao.findBydemographicId(demographicId);
    }

    public boolean updataPatientInformationByID(MrPatientsEntity patient){

        MrPatientsEntity patientModel=patientDao.findBydemographicId(String.valueOf(patient.getId()));
        if(patient!=null) {
            patientModel.setPhoto(patient.getPhoto());
            patientModel.setName(patient.getName());
            patientModel.setBirthday(patient.getBirthday());
            patientModel.setDemographicId(patient.getDemographicId());
            patientModel.setSex(patient.getSex());
            patientModel.setMaritalStatus(patient.getMaritalStatus());
            patientModel.setPhone(patient.getPhone());
            patientModel.setIsVerified(patient.getIsVerified());
        }
        return true;

    }

    public boolean deletePatientByID(String id){
        patientDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addPatient(MrPatientsEntity patient){
        if(patient.getPhone()==null&&patient.getDemographicId()==null){
            return false;
        }
        else if(patient.getDemographicId()!=null && patientDao.findBydemographicId(patient.getDemographicId())!=null){
                return false;
        }
        else if(patient.getPhone()!=null && patientDao.findByphone(patient.getPhone())!=null){
            return false;
        }
        else{
            patientDao.save(patient);
            return true;
        }
    }

    public List<String> getPatientDiagnosis(int patientId ,String doctorId){
        List<MrMedicalRecordsEntity>list= medicalRecordDao.findBypatientIdAndDoctorId(patientId,doctorId);
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
