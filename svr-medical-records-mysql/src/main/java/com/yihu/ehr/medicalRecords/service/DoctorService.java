package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorDao;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class DoctorService {

    @Autowired
    DoctorDao doctorDao;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取医生信息,不存在则新增，存在则修改
     */
    public MrDoctorsEntity getDoctorInformation(String doctorId)throws Exception
    {
        MrDoctorsEntity re = new MrDoctorsEntity();
        WlyyResponse response = wlyyService.queryDoctorInfoByID(doctorId);
        //获取医生信息成功
        if(response.getStatus() == 200)
        {
            Map<String,Object> map = (Map<String,Object>)response.getData();
            if (map!=null && map.size()>0) {
                MrDoctorsEntity doctor = new MrDoctorsEntity();
                doctor.setId(doctorId);
                doctor.setName(map.get("name").toString());
                //doctor.setDemographicId(map.get("").toString());
                doctor.setSex(map.get("sex").toString());
                if (map.get("birthday") != null && map.get("birthday").toString().length() > 0) {
                    doctor.setBirthday(java.sql.Timestamp.valueOf(map.get("birthday").toString()));
                }
                doctor.setPhoto(map.get("photo").toString());
                doctor.setPhone(map.get("mobile").toString());

                doctor.setTitle(map.get("jobName").toString());
                doctor.setGood(map.get("expertise").toString());
                doctor.setOrgName(map.get("hospital_name").toString());
                doctor.setOrgDept(map.get("dept_name").toString());
                doctorDao.save(doctor);
                re = doctor;
            }
        }
        else {
            re = doctorDao.findById(doctorId);
            Message.debug(response.getMsg());
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
    public List<MedicalRecordDTO> getDoctorRecords(String filter, String label, String medicalTimeFrom,
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
