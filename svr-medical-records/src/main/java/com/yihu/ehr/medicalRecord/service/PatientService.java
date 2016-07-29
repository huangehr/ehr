package com.yihu.ehr.medicalRecord.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.family.MedicalRecordsFamily;
import com.yihu.ehr.medicalRecord.model.MedicalRecordModel;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.URLQueryParser;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.yihu.UserMgmt;
import com.yihu.ehr.yihu.YihuResponse;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class PatientService extends BaseJpaService<MrPatientsEntity, PatientDao> {

    @Autowired
    PatientDao patientDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;
    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;
    @Autowired
    MedicalLabelService medicalLabelService;
    @Autowired
    UserMgmt userMgmt;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HbaseTemplate hbaseTemplate;
    @Autowired
    HbaseQuery hbaseQuery;

    public MrPatientsEntity getPatientInformation(String id) throws Exception {
        MrPatientsEntity re = patientDao.findByid(id);
        if (re == null)
        {
            YihuResponse response = userMgmt.queryUserInfoByID(id);
            if(response.getCode() == 10000)
            {
                Map<String,Object> map = (Map<String,Object>)response;

                MrPatientsEntity mrPatientsEntity = new MrPatientsEntity();
                mrPatientsEntity.setId(map.get("UserID").toString());
                mrPatientsEntity.setName(map.get("CName").toString());
                mrPatientsEntity.setDemographicId(map.get("IDNumber").toString());
                mrPatientsEntity.setSex(map.get("Sex").toString());
                if (map.get("BirthDate") != null && map.get("BirthDate").toString().length() > 0) {
                    mrPatientsEntity.setBirthday(java.sql.Timestamp.valueOf(map.get("BirthDate").toString()));
                }
                mrPatientsEntity.setMaritalStatus(map.get("IsMarried").toString());
                mrPatientsEntity.setPhoto(map.get("PhotoUri").toString());
                mrPatientsEntity.setPhone(map.get("Phone").toString());
                addPatient(mrPatientsEntity);
                re = mrPatientsEntity;
            }
            else{
                throw new Exception(response.getMessage());
            }
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
     * */
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
    }

    public boolean updatePatientInformationByID(MrPatientsEntity patient) {

        MrPatientsEntity patientModel = patientDao.findByid(patient.getId());
        if (patient != null && patientModel != null) {
            patientModel.setPhoto(patient.getPhoto());
            patientModel.setName(patient.getName());
            patientModel.setBirthday(patient.getBirthday());
            patientModel.setDemographicId(patient.getDemographicId());
            patientModel.setSex(patient.getSex());
            patientModel.setMaritalStatus(patient.getMaritalStatus());
            patientModel.setPhone(patient.getPhone());
        }
        return true;

    }

    public boolean IsCreated(String id) {
        if (id != null) {
            MrPatientsEntity mp = patientDao.findByid(id);
            if (mp != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

    public boolean deletePatientByID(String id) {
        patientDao.deleteBydemographicId(id);
        return true;
    }

    public boolean addPatient(MrPatientsEntity mrPatientsEntity) throws Exception {

        if (patientDao.findByid(mrPatientsEntity.getId()) != null) {
            return false;
        } else {
            patientDao.save(mrPatientsEntity);
            return true;
        }
    }

    public Map<String,String> getPatientDiagnosis(String patientId, String doctorId) throws Exception{

        List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorIdAndPatientId(doctorId,patientId);
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

    public List<MedicalRecordModel> getPatientRecords(String patientId, String label, String medicalTimeFrom,
                                                      String medicalTimeEnd, String recordType, String medicalDiagnosisCode, String doctorId) throws Exception {

        List<MrDoctorMedicalRecordsEntity> Mlist = doctorMedicalRecordDao.findBydoctorIdAndPatientId(doctorId, patientId);
        List<String> recordsIdList = new ArrayList<>();
        List<MedicalRecordModel> medicalRecordModelList = new ArrayList<>();
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
            if(m.getCreateTime().toString().compareTo(medicalTimeFrom)>0 && m.getCreateTime().toString().compareTo(medicalTimeEnd)<0 && m.getDataFrom().equals(recordType) && m.getMedicalDiagnosisCode().equals(medicalDiagnosisCode))
            medicalRecordModelList.add(m);

        }
        return medicalRecordModelList;

    }
}
