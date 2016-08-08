package com.yihu.ehr.medicalRecords.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsQueryDao;
import com.yihu.ehr.medicalRecords.dao.PatientDao;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordSimpleDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    MedicalRecordsQueryDao medicalRecordsQueryDao;

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
        MrPatientsEntity re = new MrPatientsEntity();

        WlyyResponse response = wlyyService.queryPatientInfoByID(patientId);
        //获取患者信息成功
        if(response.getStatus() == 200)
        {
            Map<String,Object> map = (Map<String,Object>)response.getData();

            MrPatientsEntity patient = new MrPatientsEntity();
            patient.setId(patientId);
            patient.setName(map.get("name").toString());
            patient.setDemographicId(map.get("idCard").toString());
            patient.setSex(map.get("sex").toString());
            if (map.get("birthday") != null && map.get("birthday").toString().length() > 0) {
                patient.setBirthday(java.sql.Timestamp.valueOf(map.get("birthday").toString()));
            }
            patient.setMaritalStatus(EnumClass.IsMarried.Unknow);
            patient.setPhoto(map.get("photo").toString());
            patient.setPhone(map.get("mobile").toString());
            patientDao.save(patient);
            re = patient;
        }
        else{
            re = patientDao.findById(patientId);
            Message.debug(response.getMsg());
            }

        return re;
    }



    /**
     * 获取患者诊断
     */
    public List<DictDTO> getPatientDiagnosis(String patientId, String doctorId) throws Exception{
        return medicalRecordsQueryDao.findPatientDiagnosis(doctorId,patientId);
    }

    /**
     * 获取患者病历
     */
    public List<MedicalRecordSimpleDTO> getPatientRecords(String doctorId, String patientId, String label, String medicalTimeFrom, String medicalTimeEnd, String recordType, String medicalDiagnosisCode, String filter,int page,int size) throws Exception {

        String sql = "select * from ";
        //选了自定义标签

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
}
