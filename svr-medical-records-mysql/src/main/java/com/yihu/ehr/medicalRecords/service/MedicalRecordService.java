package com.yihu.ehr.medicalRecords.service;


import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */

@Service
public class MedicalRecordService{



    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     */
    public MrMedicalRecordsEntity systemAccess(String patientId, String userId, String imei, String token) throws Exception {
        MrMedicalRecordsEntity re = new MrMedicalRecordsEntity();
       //单点登录校验
        WlyyResponse response = wlyyService.userSessionCheck(userId,imei,token);
        if (response.getStatus() != 200) {
            Message.error(response.getMsg());
        } else {
            //获取医生信息
            MrDoctorsEntity doctor = doctorService.getDoctorInformation(userId);
            //获取患者信息
            MrPatientsEntity patient = patientService.getPatientInformation(patientId);
            //获取最新病历
            MrDoctorMedicalRecordsEntity record = doctorMedicalRecordDao.getLastRecord(userId, patientId);

            //不存在则新增病历
            if (record != null) {
                //获取病历信息
                re = medicalRecordsDao.findById(record.getRecordId());
            } else {
                //新增病历信息
                re = addRecord(doctor, patient);
            }
        }
        return re;
    }

    /**
     * 获取病历
     */
    public Map<String,Object> getMedicalRecord(String recordId) throws Exception {
        return null;//medicalRecordsDao.getDataByRowkey(recordId);
    }


    /**
     * 新增病历
     */
    private MrMedicalRecordsEntity addRecord(MrDoctorsEntity doctor,MrPatientsEntity patient) throws Exception{
        MrMedicalRecordsEntity re = new MrMedicalRecordsEntity();
        /*if(doctor!=null && patient!=null)
        {
            MedicalRecord record = new MedicalRecord();
            String dataFrom = EnumClass.RecordDataFrom.MedicalRecord;
            String rowkey = MedicalRecordsFamily.getRowkey(patient.getId(),dataFrom);
            String datetimeNow = DateTimeUtil.utcDateTimeFormat(new Date());
            record.setRowkey(rowkey);
            record.setCreateTime(datetimeNow);
            record.setMedicalTime(datetimeNow);
            record.setDoctorId(doctor.getId());
            record.setDoctorName(doctor.getName());
            record.setTitle(doctor.getTitle());
            record.setOrgDept(doctor.getOrgDept());
            record.setOrgName(doctor.getOrgName());
            record.setPatientId(patient.getId());
            record.setPatientName(patient.getName());
            record.setDemographicId(patient.getDemographicId());
            record.setSex(patient.getSex());
            record.setBirthday(DateTimeUtil.utcDateTimeFormat(patient.getBirthday()));
            record.setIsMarried(patient.getMaritalStatus());
            record.setPhone(patient.getPhone());
            record.setDataFrom(EnumClass.RecordDataFrom.MedicalRecord);

            medicalRecordsDao.save(record);

            re = medicalRecordsDao.getDataByRowkey(rowkey);
        }
        else{
            Message.error("医生或者患者信息缺失！");
        }*/
        return re;
    }

    /**
     * 新增病历
     */
    @Transactional
    public MrMedicalRecordsEntity addRecord(String doctorId, String patientId) throws Exception {
        //获取医生信息
        MrDoctorsEntity doctor = doctorService.getDoctorInformation(doctorId);
        //获取患者信息
        MrPatientsEntity patient = patientService.getPatientInformation(patientId);

        //新增病历信息
        MrMedicalRecordsEntity re = addRecord(doctor,patient);

        if(re!=null)
        {
            //新增关联
            MrDoctorMedicalRecordsEntity dr = new MrDoctorMedicalRecordsEntity();
            dr.setDoctorId(doctorId);
            dr.setPatientId(patientId);
            dr.setRecordId(String.valueOf(re.getId()));
            dr.setIsCreator("1");
            dr.setRecordType(EnumClass.RecordType.Online);

            doctorMedicalRecordDao.save(dr);
        }

        return re;
    }


    /**
     * 修改病历
     */
    @Transactional
    public boolean editRecord(String recordId, Map<String,String> map) throws Exception {
        boolean re = true;
        /*if(map!=null)
        {
            for(String key : map.keySet())
            {
                try {
                    String value = map.get(key);
                    medicalRecordsDao.update(recordId,key,value);
                }
                catch (Exception ex)
                {
                    Message.debug(ex.getMessage());
                    re = false;
                }
            }
        }
        else{
            Message.error("修改参数为空！");
        }*/
        return re;
    }


    /**
     * 删除病历
     */
    public boolean deleteRecord(String recordId) throws Exception {

        return true;//medicalRecordsDao.delete(recordId);
    }

    /**
     * 病历分享
     */
    public boolean shareRecord(String recordId,String patientId,String doctorId) throws Exception {
        MrDoctorMedicalRecordsEntity dr = new MrDoctorMedicalRecordsEntity();
        dr.setDoctorId(doctorId);
        dr.setPatientId(patientId);
        dr.setRecordId(recordId);
        dr.setIsCreator("0");
        dr.setRecordType(EnumClass.RecordType.Online);

        doctorMedicalRecordDao.save(dr);

        return true;
    }
}