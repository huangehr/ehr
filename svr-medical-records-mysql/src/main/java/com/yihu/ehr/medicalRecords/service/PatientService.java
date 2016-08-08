package com.yihu.ehr.medicalRecords.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.PatientDao;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
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
public class PatientService {

    @Autowired
    PatientDao patientDao;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取患者信息
     */
    public MrPatientsEntity getPatient(String patientId) throws Exception
    {
        return patientDao.findById(patientId);
    }

    /**
     * 保存患者信息
     */
    public boolean savePatient(MrPatientsEntity patient) throws Exception
    {
        patientDao.save(patient);
        return true;
    }

    /**
     * 获取患者信息，不存在则新增
     * @param patientId
     * @return
     * @throws Exception
     */
    public MrPatientsEntity getPatientInformation(String patientId) throws Exception {
        MrPatientsEntity re = patientDao.findById(patientId);
        if (re == null)
        {
            /*String response = wlyyService.queryPatientInfoByID(patientId);
            if(response.getCode() == 10000)
            {
                Map<String,Object> map = (Map<String,Object>)response.getResult();

                MrPatientsEntity patient = new MrPatientsEntity();
                patient.setId(patientId);
                patient.setName(map.get("CName").toString());
                patient.setDemographicId(map.get("IDNumber").toString());
                patient.setSex(map.get("Sex").toString());
                if (map.get("BirthDate") != null && map.get("BirthDate").toString().length() > 0) {
                    patient.setBirthday(java.sql.Timestamp.valueOf(map.get("BirthDate").toString()));
                }
                patient.setMaritalStatus(map.get("IsMarried").toString());
                patient.setPhoto(map.get("PhotoUri").toString());
                patient.setPhone(map.get("Phone").toString());
                patientDao.save(patient);
                re = patient;
            }
            else{
                Message.error(response.getMessage());
            }*/
        }

        return re;
    }

    /**
     * like：使用"?"来表示，如：name?%医
     * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * not in：使用"<>"来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * =：使用"="来表示，如：status=2
     * >=：使用大于号和大于等于语法，如：createDate>2012
     * <=：使用小于号和小于等于语法，如：createDate<=2015
     * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接 GB/T 2261.2-2003
     * 多条件组合：使用";"来分隔
     * *//*
    public List<MrPatientsEntity> searchPatient(String queryCondition,int page,int size) throws Exception {
        URLQueryParser queryParser = createQueryParser(null, queryCondition, null);
        CriteriaQuery query = queryParser.makeCriteriaQuery();

        if(page<1) page=1;
        if(size<0) size=15;

        return entityManager
                .createQuery(query)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }*/

    /**
     * 获取患者诊断
     */
    public Map<String,String> getPatientDiagnosis(String patientId, String doctorId) throws Exception{

        /*List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorIdAndPatientId(doctorId,patientId);
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
        Page<Map<String, Object>> result = hbaseQuery.queryBySolr("1", q, null, 1, 1000000000);
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
        return list;*/

        return null;
    }

    /**
     * 获取患者病历
     */
    public List<MedicalRecordDTO> getPatientRecords(String patientId, String label, String medicalTimeFrom,
                                                    String medicalTimeEnd, String recordType, String medicalDiagnosisCode, String doctorId) throws Exception {

        /*List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorIdAndPatientId(doctorId, patientId);
        List<String> recordIdList = new ArrayList<>();
        List<MedicalRecord> medicalRecordModelList = new ArrayList<>();
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
            Result result = hbaseTemplate.get("", recordsIdList.get(i), new RowMapper<Result>() {
                public Result mapRow(Result result, int rowNum) throws Exception {
                    return result;
                }
            });
            KeyValue[] kv = result.raw();

            MedicalRecord m = objectMapper.readValue(kv.toString(), MedicalRecord.class);
//            for (int j = 0; j< kv.length; j++)
//            {
//                // 循环每一列
//                String qualifier = new String(kv[i].getQualifier());
//            }
            if(m.getCreateTime().toString().compareTo(medicalTimeFrom)>0 && m.getCreateTime().toString().compareTo(medicalTimeEnd)<0 && m.getDataFrom().equals(recordType) && m.getMedicalDiagnosisCode().equals(medicalDiagnosisCode))
            medicalRecordModelList.add(m);

        }
        return medicalRecordModelList;*/
        return  null;
    }
}
