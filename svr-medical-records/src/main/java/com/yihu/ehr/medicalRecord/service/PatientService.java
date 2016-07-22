package com.yihu.ehr.medicalRecord.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.web.RestTemplates;
import org.codehaus.jettison.json.JSONObject;
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


    @Autowired
    PatientDao patientDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${service-gateway.wsurl}")
    public String wsurl;
    @Value("${service-gateway.WSclientId}")
    public String WSclientId;

    public MrPatientsEntity getPatientInformation(String id){
            return patientDao.findByid(id);
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

    private Map<String, Object> getLoginParam(String api, String parma) {
        Map<String, Object> param = new HashMap<>();
        param.put("AuthInfo", "{ \"ClientId\": " + WSclientId + " }");
        param.put("SequenceNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        param.put("Api", api);
        param.put("Param", parma);
        return param;
    }
    public boolean addPatient(String id) throws Exception{
        Map<String, Object> params = getLoginParam("UserMgmt.User.queryUserInfoByID", "{ \"UserID\":\"" + id + "\"}");
        String result;
        MrPatientsEntity mrPatientsEntity=new MrPatientsEntity();
        result = doPost(wsurl, params, null, null);
        //objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.ISO8601Pattern));
        //mrPatientsEntity= objectMapper.readValue(result, MrPatientsEntity.class);


        JSONObject myJsonObject = new JSONObject(result);
        mrPatientsEntity.setId(myJsonObject.getString("UserID"));
        mrPatientsEntity.setName(myJsonObject.getString("CName"));
        mrPatientsEntity.setDemographicId(myJsonObject.getString("IdNumber "));
        mrPatientsEntity.setSex(myJsonObject.getString("UserID"));
        if(myJsonObject.getString("BirthDate")!=null&&myJsonObject.getString("BirthDate").length()>0) {
            mrPatientsEntity.setBirthday(java.sql.Timestamp.valueOf(myJsonObject.getString("BirthDate")));
        }
        mrPatientsEntity.setMaritalStatus(myJsonObject.getString("IsMarried"));
        mrPatientsEntity.setPhoto(myJsonObject.getString("PhotoUri"));
        mrPatientsEntity.setPhone(myJsonObject.getString("Phone"));
        mrPatientsEntity.setIsVerified(myJsonObject.getString("UserID"));
        patientDao.save(mrPatientsEntity);
        return true;
    }

    public List<String> getPatientDiagnosis(String patientId ,String doctorId){
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
