package com.yihu.ehr.medicalRecord.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.PageArg;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.dao.intf.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalLabelDao;
import com.yihu.ehr.medicalRecord.dao.intf.MedicalRecordDao;
import com.yihu.ehr.medicalRecord.dao.intf.PatientDao;
import com.yihu.ehr.medicalRecord.family.MedicalRecordsFamily;
import com.yihu.ehr.medicalRecord.model.MedicalRecordModel;
import com.yihu.ehr.medicalRecord.model.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecord.model.MrPatientsEntity;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.query.URLQueryParser;
import com.yihu.ehr.query.services.HbaseQuery;
import com.yihu.ehr.util.HttpClientUtil.HttpClientUtil;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.web.RestTemplates;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaQuery;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class PatientService extends BaseJpaService<MrPatientsEntity, PatientDao> {

    //    @Autowired
//    SolrQuery solr;
    @Autowired
    PatientDao patientDao;
    @Autowired
    MedicalRecordDao medicalRecordDao;
    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;
    @Autowired
    MedicalLabelService medicalLabelService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HbaseTemplate hbaseTemplate;
    @Autowired
    HbaseQuery hbaseQuery;

    public MrPatientsEntity getPatientInformation(String id) throws Exception {
        MrPatientsEntity p = patientDao.findByid(id);
        if (p != null)
            return p;
        else {
            HashMap<String,Object> re=(HashMap)userInfoService.getUserInfo(id);
            if (re!=null&&re.size()>0) {
               // re = objectMapper.readValue(s, Map.class);
                MrPatientsEntity mrPatientsEntity = new MrPatientsEntity();
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
                addPatient(mrPatientsEntity);
                return mrPatientsEntity;
            } else
                return null;
        }
    }

    public List<MrPatientsEntity> searchPatient(String queryCondition,int page,int size) throws Exception {
        URLQueryParser queryParser = createQueryParser(null, queryCondition, null);
        CriteriaQuery query = queryParser.makeCriteriaQuery();

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
//        List<MrMedicalRecordsEntity> list = medicalRecordDao.findBypatientIdAndDoctorIdOrderByMedicalTimeDesc(patientId, doctorId);
//        List<String> diagnosisList = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i) != null) {
//                if (!list.contains(list.get(i).getMedicalDiagnosis())) {
//                    diagnosisList.add(list.get(i).getMedicalDiagnosis());
//                }
//            }
//        }
//        return diagnosisList;
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
