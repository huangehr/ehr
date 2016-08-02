package com.yihu.ehr.medicalRecord.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorDao;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.family.MedicalRecordsFamily;
import com.yihu.ehr.medicalRecord.model.*;
import com.yihu.ehr.query.services.HbaseQuery;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class DoctorService {

    @Autowired
    DoctorDao doctorDao;
    @Autowired
    PatientService patientService;
    @Autowired
    MedicalRecordDao medicalRecordDao;
    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HbaseTemplate hbaseTemplate;
    @Autowired
    MedicalLabelService medicalLabelService;
    @Autowired
    HbaseQuery hbaseQuery;

    public MrDoctorsEntity getDoctorInformation(String id)throws Exception{
        MrDoctorsEntity m=doctorDao.findById(id);
        if(m==null) {
            HashMap<String,Object> re=(HashMap)userInfoService.getUserInfo(id);
            if (re!=null&&re.size()>0) {
                //re = objectMapper.readValue(s, Map.class);
                MrDoctorsEntity mrDoctorsEntity=new MrDoctorsEntity();
                mrDoctorsEntity.setId(re.get("UserID").toString());
                mrDoctorsEntity.setName(re.get("CName").toString());
                mrDoctorsEntity.setDemographicId(re.get("IDNumber").toString());
                mrDoctorsEntity.setSex(re.get("Sex").toString());
                if (re.get("BirthDate") != null && re.get("BirthDate").toString().length() > 0) {
                    mrDoctorsEntity.setBirthday(java.sql.Timestamp.valueOf(re.get("BirthDate").toString()));
                }
                mrDoctorsEntity.setTitle(re.get("Lczc").toString());
                mrDoctorsEntity.setPhoto(re.get("PhotoUri").toString());
                mrDoctorsEntity.setPhone(re.get("Phone").toString());
                mrDoctorsEntity.setGood(re.get("Skill").toString());
                addDoctor(mrDoctorsEntity);
                return mrDoctorsEntity;
            } else
                return null;
        }
        else
            return m;
    }
    public MrDoctorsEntity getDoctorInformationByDemographicId(String demographicId){

        return doctorDao.findBydemographicId(demographicId);
    }

    public boolean updateDoctorInformationById(MrDoctorsEntity doctor){
        if(doctor!=null) {
            MrDoctorsEntity doctorModel = doctorDao.findById(doctor.getId());
            if(doctorModel!=null ){
                doctorModel.setSex(doctor.getSex());
                doctorModel.setBirthday(doctor.getBirthday());
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

//    public boolean updateDoctorStatusByDemographicId(String status,String demographicId){
//
//        MrDoctorsEntity DoctorModel=doctorDao.findBydemographicId(demographicId);
//        if(DoctorModel!=null) {
//            DoctorModel.setStatus(status);
//        }
//        return true;
//
//    }

    public boolean deleteDoctorByDemographicId(String id){
        doctorDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addDoctor(MrDoctorsEntity doctor){
        if(doctorDao.findById(doctor.getId())!=null){
            return false;
        }
        else {
            doctorDao.save(doctor);
            return true;
        }
    }

    public Map<String, String> getDoctorDiagnosis(String doctorId)throws Exception {
//        List<MrMedicalRecordsEntity>list= medicalRecordDao.findByDoctorIdOrderByMedicalTimeDesc(doctorId);
//        List<String> diagnosisList=new ArrayList<>();
//        for(int i=0;i<list.size();i++){
//            if(list.get(i)!=null){
//                if(!list.contains(list.get(i).getMedicalDiagnosis())) {
//                    diagnosisList.add(list.get(i).getMedicalDiagnosis());
//                }
//            }
//        }
//        return diagnosisList;
        List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorId(doctorId);
        String q="";
        if (Mlist != null && Mlist.size() > 0) {
            for (int i = 0; i < Mlist.size(); i++) {
                if (Mlist.get(i) != null) {
                    q="rowKey:"+Mlist.get(i).getRecordId()+" or ";
                }
            }
        }
        q.substring(0,q.length()-4);
        if("".equals(q)){
            return null;
        }
        Page<Map<String, Object>> result = hbaseQuery.queryBySolr(MedicalRecordsFamily.TableName, "rowkey", null, 1, 1000000000);
        Map<String, String> list = new HashMap<>();
        if (result.getContent() != null && result.getContent().size() > 0) {

            //遍历所有行
            for (int i = 0; i < result.getContent().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) result.getContent().get(i);
                if (obj.get("MEDICAL_DIAGNOSIS_CODE") != null) {
                    if(obj.get("MEDICAL_DIAGNOSIS") != null){
                        obj.put(obj.get("MEDICAL_DIAGNOSIS_CODE").toString(),obj.get("MEDICAL_DIAGNOSIS").toString());
                    }
                    else {
                        obj.put(obj.get("MEDICAL_DIAGNOSIS_CODE").toString(),"");
                    }

                }

            }
        }
        return list;
    }

    public List<MedicalRecordModel> getDoctorRecords(String filter, String label, String medicalTimeFrom,
                                                      String medicalTimeEnd, String recordType, String doctorId,int page,int size) throws Exception {

        List<MrPatientsEntity>patientsEntityList=patientService.searchPatient(filter,1,1000000000);
        List<MedicalRecordModel> medicalRecordModelList = new ArrayList<>();
        if(patientsEntityList!=null && patientsEntityList.size()>0) {
            for (int l = 0; l < patientsEntityList.size(); l++) {
                String patientId = patientsEntityList.get(l).getId();
                List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorIdAndPatientId(doctorId, patientId);
                List<String> recordsIdList = new ArrayList<>();
                if (label != null && label.length() > 0) {
                    List<String> Ilist = medicalLabelService.getRecordIdByLabels(label.split(","));
                    if (Mlist != null && Mlist.size() > 0) {
                        for (int i = 0; i < Mlist.size(); i++) {
                            if (Mlist.get(i) != null && Ilist.contains(Mlist.get(i).getId())) {
                                recordsIdList.add(Mlist.get(i).getRecordId());
                            }
                        }
                    }

                }
                for (int i = 0; i < recordsIdList.size(); i++) {
                    Result result = hbaseTemplate.get(MedicalRecordsFamily.TableName, recordsIdList.get(i), new RowMapper<Result>() {
                        public Result mapRow(Result result, int rowNum) throws Exception {
                            return result;
                        }
                    });
                    KeyValue[] kv = result.raw();

                    MedicalRecordModel m = objectMapper.readValue(kv.toString(), MedicalRecordModel.class);
//            for (int j = 0; j< kv.length; j++)
//            {
//                // 循环每一列
//                String qualifier = new String(kv[i].getQualifier());
//            }
                    if(m.getCreateTime().toString().compareTo(medicalTimeFrom)>0 && m.getCreateTime().toString().compareTo(medicalTimeEnd)<0 && m.getDataFrom().equals(recordType))
                        medicalRecordModelList.add(m);

                }
            }

        }

        return medicalRecordModelList;

    }
}
