package com.yihu.ehr.medicalRecord.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.util.HttpClientUtil.HttpClientUtil;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.web.RestTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class PatientService extends RestTemplates  {

//    @Autowired
//    SolrQuery solr;
    @Autowired
    PatientDao patientDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ObjectMapper objectMapper;

    public MrPatientsEntity getPatientInformation(String id)throws Exception{
        MrPatientsEntity p=patientDao.findByid(id);
        if(p !=null)
            return p;
        else{
            Map re=new HashMap<>();
            String s=userInfoService.getUserInfo(id);
            if (s!=null) {
                re = objectMapper.readValue(s, Map.class);
                MrPatientsEntity mrPatientsEntity=new MrPatientsEntity();
                mrPatientsEntity.setId(re.get("UserID").toString());
                mrPatientsEntity.setName(re.get("CName").toString());
                mrPatientsEntity.setDemographicId(re.get("IdNumber").toString());
                mrPatientsEntity.setSex(re.get("Sex").toString());
                if (re.get("BirthDate") != null && re.get("BirthDate").toString().length() > 0) {
                    mrPatientsEntity.setBirthday(java.sql.Timestamp.valueOf(re.get("BirthDate").toString()));
                }
                mrPatientsEntity.setMaritalStatus(re.get("IsMarried").toString());
                mrPatientsEntity.setPhoto(re.get("PhotoUri").toString());
                mrPatientsEntity.setPhone(re.get("Phone").toString());
                mrPatientsEntity.setIsVerified(re.get("IsMarried").toString());
                addPatient(mrPatientsEntity);
                return mrPatientsEntity;
            } else
                return null;
        }
    }

//    public List<MrPatientsEntity> searchPatient(String queryCondition)throws Exception{
//        ObjectMapper mapper = new ObjectMapper();
//        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
//        List<Map<String,Object>> list = objectMapper.readValue(queryCondition,javaType);
//        List<QueryCondition> ql = new ArrayList<>();
//        if(list!=null && list.size()>0)
//        {
//            for(Map<String,Object> item : list)
//            {
//                String field = String.valueOf(item.get("field")).trim();
//                String cond = String.valueOf(item.get("condition")).trim();
//                String value = String.valueOf(item.get("value"));
//                if(value.indexOf(",")>0)
//                {
//                    ql.add(new QueryCondition("And", cond, field, value.split(",")));
//                }
//                else{
//                    ql.add(new QueryCondition("And", cond, field, value));
//                }
//            }
//        }
//        solr.conditionToString(ql);
//    }

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

    public boolean IsCreated(String id) {
        if (id!=null) {
            MrPatientsEntity mp = patientDao.findByid(id);
            if (mp != null) {
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }


    }

    public boolean deletePatientByID(String id){
        patientDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addPatient(MrPatientsEntity mrPatientsEntity) throws Exception{

        patientDao.save(mrPatientsEntity);
        return true;
    }

    public List<String> getPatientDiagnosis(String patientId ,String doctorId){
        List<MrMedicalRecordsEntity>list= medicalRecordDao.findBypatientIdAndDoctorIdOrderByMedicalTimeDesc(patientId,doctorId);
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
