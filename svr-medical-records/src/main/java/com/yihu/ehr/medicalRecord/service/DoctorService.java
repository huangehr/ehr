package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.comom.Message;
import com.yihu.ehr.medicalRecord.comom.UserMgmt;
import com.yihu.ehr.medicalRecord.comom.YihuResponse;
import com.yihu.ehr.medicalRecord.dao.DoctorDao;
import com.yihu.ehr.medicalRecord.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.model.DTO.MedicalRecord;
import com.yihu.ehr.medicalRecord.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.model.EnumClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    UserMgmt userMgmt;


    @Autowired
    PatientService patientService;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取医生信息,不存在则新增
     */
    public MrDoctorsEntity getDoctorInformation(String doctorId)throws Exception{
        MrDoctorsEntity re = doctorDao.findById(doctorId);
        if(re ==null) {
            YihuResponse response = userMgmt.queryUserInfoByID(doctorId);
            if(response.getCode() == 10000)
            {
                Map<String,Object> map = (Map<String,Object>)response.getResult();
                if (map!=null && map.size()>0) {
                    String type = map.get("UserType").toString();
                    if(type.equals(EnumClass.UserType.Doctor))
                    {
                        MrDoctorsEntity doctor = new MrDoctorsEntity();
                        doctor.setId(doctorId);
                        doctor.setName(map.get("CName").toString());
                        doctor.setDemographicId(map.get("IDNumber").toString());
                        doctor.setSex(map.get("Sex").toString());
                        if (map.get("BirthDate") != null && map.get("BirthDate").toString().length() > 0) {
                            doctor.setBirthday(java.sql.Timestamp.valueOf(map.get("BirthDate").toString()));
                        }
                        doctor.setPhoto(map.get("PhotoUri").toString());
                        doctor.setPhone(map.get("Phone").toString());

                        if(map.containsKey("Lczc"))
                        {
                            doctor.setTitle(map.get("Lczc").toString());
                        }
                        if(map.containsKey("Skill"))
                        {
                            doctor.setGood(map.get("Skill").toString());
                        }
                        doctorDao.save(doctor);
                        re = doctor;
                    }
                    else{
                        Message.debug("该用户不是医生，id:"+doctorId);
                    }
                }
            }
            else {
                Message.error(response.getMessage());
            }

        }
        return re;
    }

    /**
     * 获取医生信息
     */
    public MrDoctorsEntity getDoctor(String doctorId) throws Exception
    {
        return doctorDao.findById(doctorId);
    }


    /**
     * 保存医生信息
     */
    public boolean saveDoctor(MrDoctorsEntity doctor){
        doctorDao.save(doctor);
        return true;
    }


    /**
     * 获取医生诊断
     */
    public Map<String, String> getDoctorDiagnosis(String doctorId)throws Exception {

        /*List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorId(doctorId);
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
        Map<String, String> list = new HashMap<>();
        Page<Map<String, Object>> result = hbaseQuery.queryBySolr(MedicalRecordsFamily.TableName, "rowkey", null, 1, 1000000000);

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
        }*/
        return null;
    }

    /**
     * 获取医生病历
     */
    public List<MedicalRecord> getDoctorRecords(String filter, String label, String medicalTimeFrom,
                                                String medicalTimeEnd, String recordType, String doctorId, int page, int size) throws Exception {

        /*List<MrPatientsEntity>patientsEntityList=patientService.searchPatient(filter,1,1000000000);
        List<MedicalRecord> medicalRecordModelList = new ArrayList<>();
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
                /*for (int i = 0; i < recordsIdList.size(); i++) {
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

        return medicalRecordModelList;*/
        return null;

    }
}
